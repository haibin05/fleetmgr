package com.yunguchang.gps;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.yunguchang.baidu.KeyPool;
import com.yunguchang.data.GpsRepository;
import com.yunguchang.data.VehicleRepository;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import com.yunguchang.model.persistence.TGpsAdjustedPath;
import com.yunguchang.model.persistence.TGpsPointEntity;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Arrays.asList;

/**
 * Created by 禕 on 2015/9/13.
 */
@Stateful
@Path("/adjust")
@ApplicationScoped
public class PathAdjusterExecutor {
    //忽略时间接近的点位。
    private static int POINTS_NUMBER_SKIP = 3;
    //每次路劲规划时最多取几条gps记录行数。
    private static int MAX_GPS_RECORDS_PER_PLAN =10;
    final private Map<String, Lock> executionLocks = new ConcurrentHashMap();
    @Inject
    private GpsRepository gpsRepository;
    @Inject
    private VehicleRepository vehicleRepository;
    @Inject
    private Logger logger;
    @Inject
    private BaiduClient baiduClient;
    @Resource(lookup = "java:jboss/ee/concurrency/executor/path")
    private ManagedExecutorService mes;
    @Inject
    private KeyPool keyPool;

    @GET
    public void adjust() {
        try {
            logger.startAdjustPath();

            List<TAzCarinfoEntity> cars = gpsRepository.getCarIdToAdjusted();

            for (final TAzCarinfoEntity car : cars) {
                String carId=car.getId();
                if (!executionLocks.containsKey(carId)) {
                    executionLocks.put(carId, new ReentrantLock());
                }
                mes.submit(new Runnable() {
                    @Override
                    public void run() {
                        adjustSingleCar(car);
                    }
                });
            }
        } catch (Exception e) {
            logger.errorWhenAdjust(e);
        }
    }

    private void adjustSingleCar(final TAzCarinfoEntity car) {
        String carId=car.getId();
        Lock locker = executionLocks.get(carId);
        boolean hasLocker = false;
        boolean shouldAdjustAgain=false;
        try {
            hasLocker = locker.tryLock();

            if (!hasLocker) {
                return;
            }
            final Phaser phaser = new Phaser();
            phaser.register();

            DateTime pointTime = null;
            TGpsAdjustedPath lastAdjustedPathEntity = gpsRepository.getLastAdjustedPathByCarId(carId);
            if (lastAdjustedPathEntity != null) {
                pointTime = GpsUtil.getPersistPointTime(lastAdjustedPathEntity.getEnd());
            }

            List<GpsPoint> gpsPoints = new ArrayList<>();
            List<TGpsPointEntity> pointCandidates = gpsRepository.getGpsRecordByCarIdAndAfterPointTime(carId, pointTime, MAX_GPS_RECORDS_PER_PLAN);
            if (pointCandidates.size() > 0) {
                for (TGpsPointEntity pointCandidate : pointCandidates) {
                    if (lastAdjustedPathEntity != null && lastAdjustedPathEntity.getEnd() != null) {
                        final DateTime previousEndTime = lastAdjustedPathEntity.getEnd();
                        Iterable<GpsPoint> pointsToAdd = Iterables.filter(GpsUtil.fromPersistPointsWithShallowCarObject(pointCandidate), new Predicate<GpsPoint>() {
                            @Override
                            public boolean apply(GpsPoint input) {
                                if (input == null) {
                                    return true;
                                }
                                return input.getPersistTime().isEqual(previousEndTime) ||
                                        input.getPersistTime().isAfter(previousEndTime);
                            }
                        });
                        Iterables.addAll(gpsPoints, pointsToAdd);
                    } else {
                        Iterables.addAll(gpsPoints, GpsUtil.fromPersistPointsWithShallowCarObject(pointCandidate));
                    }
                }
            } else {
                return;
            }
            if (gpsPoints.size() <= 1) {
                return;
            }


            GpsPoint origin = null;
            GpsPoint destination;
            int originIdx = 0;

            //find fist point
            for (int i = 0; i < gpsPoints.size(); i++) {
                origin = gpsPoints.get(i);
                originIdx = i;
                if (origin != null) {
                    break;
                }
            }

            boolean atLeastOnePath=false;
            // find dest point
            if (origin != null) {
                //skip the minimal time duration
                for (int i = originIdx + POINTS_NUMBER_SKIP; i < gpsPoints.size(); i++) {
                    destination = gpsPoints.get(i);
                    if (destination == null || GpsUtil.isSameLocation(origin, destination)) {
                        continue;
                    }
                    atLeastOnePath=true;
                    phaser.register();

                    final GpsPoint fOrigin = origin;
                    final GpsPoint fDestination = destination;
                    mes.submit(new Runnable() {
                                   @Override
                                   public void run() {
                                       try {
                                           logger.adjustPath(POINTS_NUMBER_SKIP, fOrigin, fDestination);
                                           TGpsAdjustedPath tGpsAdjustedPath = initGpsAdjustedPath(car, fOrigin, fDestination);

                                           DrivingResult result = getPath(fOrigin, fDestination);
                                           if (result == null) {
                                               return;
                                           }
                                           tGpsAdjustedPath.setOriginCity(result.getOriginCity());
                                           tGpsAdjustedPath.setDestinationCity(result.getDestinationCity());

                                           if (isValidPath(result, fOrigin, fDestination)) {
                                               tGpsAdjustedPath.setPath(getCompletePath(result));

                                           } else {
                                               String path = fOrigin.getLng() + "," + fOrigin.getLat() + ";" + fDestination.getLng() + "," + fDestination.getLat();
                                               tGpsAdjustedPath.setStatus(1);
                                               tGpsAdjustedPath.setPath(path);

                                           }


                                           gpsRepository.saveAdjustedPath(asList(tGpsAdjustedPath));
                                       }catch (Exception e){
                                           logger.error("Error when adjust path",e);
                                       }
                                       finally {
                                           phaser.arrive();
                                       }

                                   }
                               }

                    );
                    origin = destination;
                    i += POINTS_NUMBER_SKIP;

                }


            } else {
                logger.cantAdjustWholeRecords();
                return;
            }
            phaser.arriveAndAwaitAdvance();

            //以上的记录没有任何一条可用
            //但是，为了下次进行线路规划能够获得最新的数据
            //我们需要将以上记录排除掉
            //我们直接造一条最简单的路径
            if (!atLeastOnePath && pointCandidates.size()== MAX_GPS_RECORDS_PER_PLAN){
                Collections.reverse(gpsPoints);
                GpsPoint dest = Iterables.find(gpsPoints, Predicates.notNull(), null);
                DrivingResult result = getPath(origin, dest);
                if (result!=null){
                    TGpsAdjustedPath tGpsAdjustedPath=initGpsAdjustedPath(car, origin, dest);
                    tGpsAdjustedPath.setOriginCity(result.getOriginCity());
                    tGpsAdjustedPath.setDestinationCity(result.getDestinationCity());
                    String path = origin.getLng() + "," + origin.getLat() + ";" + dest.getLng() + "," + dest.getLat();
                    tGpsAdjustedPath.setStatus(0);
                    tGpsAdjustedPath.setPath(path);
                    gpsRepository.saveAdjustedPath(asList(tGpsAdjustedPath));

                }
            }
            logger.startFixInvalidPath();
            TGpsAdjustedPath adjustedPathToFix;
            DateTime dateTimeToAdjusted = DateTime.now().minusMinutes(10);
            while ((adjustedPathToFix = gpsRepository.getOneInvalidAdjustedPathByCarIdAndEndBeforeTime(carId, dateTimeToAdjusted)) != null) {
                fixOneAdjustedPath(adjustedPathToFix);
            }

            //很可能是历史数据，我们继续规划，直到单次规划的数量降下来
            if (pointCandidates.size()== MAX_GPS_RECORDS_PER_PLAN){
                shouldAdjustAgain=true;
            }
        } finally {
            if (hasLocker) {
                locker.unlock();
            }
        }


        if (shouldAdjustAgain){
            mes.submit(new Runnable() {
                @Override
                public void run() {
                    adjustSingleCar(car);
                }
            });
        }


    }


    private void fixOneAdjustedPath(TGpsAdjustedPath adjustedPathToFix) {

        //系统检测出有无效线路后
        //向前查询一个点位
        //再向后查询若干线路点位
        //跳过无效点位后重新进行线路规划
        List<TGpsAdjustedPath> prevPoints = gpsRepository.getLimitedValidGpsAdjustedPathByCarIdAndBeforeTime(adjustedPathToFix.getCar().getId(), adjustedPathToFix.getStart(), 1);
        List<TGpsAdjustedPath> nextPoints = gpsRepository.getLimitedGpsAdjustedPathByCarIdAndAfterTime(adjustedPathToFix.getCar().getId(), adjustedPathToFix.getStart(), 3);
        List<Integer> overwriteIds = new ArrayList<>();

        TGpsAdjustedPath firstPoint;
        if (prevPoints.size() > 0) {
            firstPoint = prevPoints.get(0);
            overwriteIds.add(firstPoint.getId());
            overwriteIds.add(adjustedPathToFix.getId());
        } else {
            firstPoint = adjustedPathToFix;
            overwriteIds.add(adjustedPathToFix.getId());
        }

        GpsPoint origin = new GpsPoint();
        origin.setLat(firstPoint.getGpsOriginLat());
        origin.setLng(firstPoint.getGpsOriginLng());
        origin.setSampleTime(firstPoint.getGpsOriginSampleTime());
        origin.setSpeed(firstPoint.getSpeed());
        overwriteIds.add(firstPoint.getId());

        for (TGpsAdjustedPath nextPoint : nextPoints) {
            overwriteIds.add(nextPoint.getId());
            GpsPoint destination = new GpsPoint();
            destination.setLat(nextPoint.getGpsDestinationLat());
            destination.setLng(nextPoint.getGpsDestinationLng());
            destination.setSampleTime(nextPoint.getGpsDestinationSampleTime());
            destination.setSpeed(nextPoint.getSpeed());
            DrivingResult result = getPath(origin, destination);
            if (isValidPath(result, origin, destination)) {
                TGpsAdjustedPath validAdjustedPath = initGpsAdjustedPath(firstPoint.getCar(), origin, destination);
                validAdjustedPath.setPath(getCompletePath(result));
                validAdjustedPath.setStart(firstPoint.getStart());
                validAdjustedPath.setEnd(nextPoint.getEnd());
                validAdjustedPath.setOriginCity(result.getOriginCity());
                validAdjustedPath.setDestinationCity(result.getDestinationCity());
                //如果查找出合适的新线路，我们保存新线路，
                //并且把所有被新线路覆盖调的路线设置为被重写
                gpsRepository.fixAdjustedPath(validAdjustedPath, overwriteIds);
                return;
            }
        }

        //实在找不到有效路径，那么只好把这条路径作为有效路径了
        gpsRepository.setAdjustedPathAsValid(adjustedPathToFix);


    }

    private boolean isValidPath(DrivingResult result, GpsPoint origin, GpsPoint destination) {
        if (result.getStatus() != 0) {
            return false;
        }
        int adjustedDuration = result.getResult().getRoutes()[0].getDuration();
        int duration = Seconds.secondsBetween(origin.getSampleTime(), destination.getSampleTime()).getSeconds();
        return adjustedDuration < duration * 3;
    }

//    private void saveStopPoint(TAzCarinfoEntity car, GpsPoint origin, DateTime endTime) {
//        logger.debug("only one gps point to adjust, persist time is {}", origin.getPersistTime());
//        String city = baiduClient.geocoder(origin.getLat() + "," + origin.getLng(), "json", BaiduClient.BAIDU_AK).getResult().getAddressComponent().getCity();
//        TGpsAdjustedPath tGpsAdjustedPath = new TGpsAdjustedPath();
//        tGpsAdjustedPath.setCar(car);
//        tGpsAdjustedPath.setStart(origin.getPersistTime());
//        tGpsAdjustedPath.setEnd(endTime);
//        tGpsAdjustedPath.setPath(origin.getLng() + "," + origin.getLat());
//        tGpsAdjustedPath.setGpsOriginLat(origin.getLat());
//        tGpsAdjustedPath.setGpsOriginLng(origin.getLng());
//        tGpsAdjustedPath.setGpsDestinationLat(origin.getLat());
//        tGpsAdjustedPath.setGpsDestinationLng(origin.getLng());
//        tGpsAdjustedPath.setOriginCity(city);
//        tGpsAdjustedPath.setDestinationCity(city);
//        gpsRepository.saveAdjustedPath(asList(tGpsAdjustedPath));
//    }

    private String getCompletePath(DrivingResult result) {
        List<String> paths = new ArrayList<String>();
        for (DrivingResult.Result.Route.Step step : result.getResult().getRoutes()[0].getSteps()) {
            paths.add(step.getPath());
        }
        return Joiner.on(";").join(paths);
    }

    private TGpsAdjustedPath initGpsAdjustedPath(TAzCarinfoEntity car, GpsPoint origin, GpsPoint destination) {
        TGpsAdjustedPath tGpsAdjustedPath = new TGpsAdjustedPath();
        tGpsAdjustedPath.setCar(car);
        tGpsAdjustedPath.setStart(origin.getPersistTime());
        tGpsAdjustedPath.setEnd(destination.getPersistTime());
        tGpsAdjustedPath.setSpeed(destination.getSpeed());
        tGpsAdjustedPath.setGpsOriginLat(origin.getLat());
        tGpsAdjustedPath.setGpsOriginLng(origin.getLng());
        tGpsAdjustedPath.setGpsDestinationLat(destination.getLat());
        tGpsAdjustedPath.setGpsDestinationLng(destination.getLng());
        tGpsAdjustedPath.setGpsOriginSampleTime(origin.getSampleTime());
        tGpsAdjustedPath.setGpsDestinationSampleTime(destination.getSampleTime());

        tGpsAdjustedPath.setStatus(0);
        return tGpsAdjustedPath;
    }

    public DrivingResult getPath(GpsPoint origin, GpsPoint destination) {
        String key = keyPool.acquire();
        if (key == null) {
            return null;
        }
        try {
            String originLocation = origin.getLat() + "," + origin.getLng();
            String destinationLocation = destination.getLat() + "," + destination.getLng();
            String originCity = baiduClient.geocoder(originLocation, "json", key).getResult().getAddressComponent().getCity();
            String destinationCity = baiduClient.geocoder(destinationLocation, "json", key).getResult().getAddressComponent().getCity();
            DrivingResult drivingResult = baiduClient.driving("driving", originLocation, destinationLocation, originCity, destinationCity, 12, "json", key);
            drivingResult.setOriginCity(originCity);
            drivingResult.setDestinationCity(destinationCity);
            return drivingResult;
        } finally {
            if (key!=null) {
                keyPool.release(key);
            }
        }
    }
}
