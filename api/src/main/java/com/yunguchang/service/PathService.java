package com.yunguchang.service;

import com.google.common.collect.Iterables;
import com.yunguchang.data.GpsRepository;
import com.yunguchang.data.ScheduleRepository;
import com.yunguchang.data.VehicleRepository;
import com.yunguchang.gps.GpsUtil;
import com.yunguchang.model.common.Car;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.common.PathWithAlarm;
import com.yunguchang.model.persistence.*;
import com.yunguchang.sam.PrincipalExt;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.yunguchang.model.EntityConvert.fromEntity;
import static java.util.Arrays.asList;

/**
 * Created by 禕 on 2016/1/23.
 */
@Stateless
public class PathService {
    @Inject
    private GpsRepository gpsRepository;
    @Inject
    private VehicleRepository vehicleRepository;
    @Inject
    private ScheduleRepository scheduleRepository;
    public List<PathWithAlarm> caclPathWithAlarms( String carId, DateTime start, DateTime end, PrincipalExt principalExt) {
        if (end == null) {
            end = DateTime.now();
        }
        if (start == null) {
            start = end.withTimeAtStartOfDay();
        }

        List<TGpsPointEntity> gpsEntites = gpsRepository.getGpsRecordByCarIdAndStartEnd(carId, start, end, principalExt);

        TAzCarinfoEntity carinfoEntity = vehicleRepository.getCarById(carId, principalExt);
        Car car = fromEntity(carinfoEntity);
        List<PathWithAlarm> pathWithAlarmList = new ArrayList<>();
        if (gpsEntites.size() == 0) {
            //这个时间段没有gps数据
            //我们查询之前的的数据
            GpsPoint gpsPoint = getGpsPointAtOrBeforTime(carId, start, principalExt);
            if (gpsPoint==null){
                return Collections.EMPTY_LIST;
            }
            PathWithAlarm pathWithAlarm = new PathWithAlarm();
            pathWithAlarm.setCar(car);
            pathWithAlarm.setStart(start);
            pathWithAlarm.setEnd(end);
            pathWithAlarm.setGpsPoints(asList(gpsPoint));
            pathWithAlarm.setAdjustedPoints(asList(gpsPoint));
            pathWithAlarm.setMoving(false);
            pathWithAlarm.setDriver(car.getDriver());
            pathWithAlarmList.add(pathWithAlarm);
        } else {
            List<GpsPoint> gpsPoints = getAllMovingGpsPoints(gpsEntites);


            PathWithAlarm pathWithAlarm = null;
            GpsPoint previous = null;
            for (GpsPoint gpsPoint : gpsPoints) {
                if (gpsPoint.getSampleTime().isBefore(start)){
                    gpsPoint.setSampleTime(start);
                }
                // previous是null，创建新path
                // 这里的关键是确定停车
                // 因为不能直接从gps原始数据判断，所以做一些假设
                // 如果两个连续的gps点位之间超过10分钟
                // 那么我们假设车辆在前面那个位置停留了10分钟
                // 但是，我们要排除gps上报数据缺失的情况
                // 所以，我们计算一下速度。只有速度足够小的情况下，才认为是停车。
                //

                if (previous == null ||
                        Minutes.minutesBetween(previous.getPersistTime(), gpsPoint.getPersistTime()).getMinutes() > 10
                                && GpsUtil.getDistance(previous.getLat(), previous.getLng(), gpsPoint.getLat(), gpsPoint.getLng())/
                                Minutes.minutesBetween(previous.getPersistTime(), gpsPoint.getPersistTime()).getMinutes()<50
                    //TODO: 根据告警类型创建新路径
                        ) {
                    //这是之前的路径
                    if (pathWithAlarm != null) {
                        if (pathWithAlarm.getGpsPoints().size() > 1) {
                            // 如果有多个gps点,说明已经移动过,我们结束前一次路径
                            pathWithAlarm.setEnd(previous.getPersistTime());
                            // 然后创建一个停止的路径
                            pathWithAlarm = new PathWithAlarm();
                            pathWithAlarm.setStart(previous.getPersistTime());
                            pathWithAlarm.setEnd(gpsPoint.getPersistTime());
                            pathWithAlarmList.add(pathWithAlarm);
                            pathWithAlarm.getGpsPoints().add(previous);
                        } else {
                            // 否则之前的唯一gps点一直停止到当前时间,直接关掉这条路径
                            pathWithAlarm.setEnd(gpsPoint.getPersistTime());

                        }

                    }
                    //创建新路径
                    pathWithAlarm = new PathWithAlarm();
                    pathWithAlarm.setStart(gpsPoint.getPersistTime());
                    pathWithAlarm.getGpsPoints().add(gpsPoint);
                    pathWithAlarmList.add(pathWithAlarm);
                } else {
                    //pathWithAlarm mut be a valid instance here.
                    //otherwise it's a bug
                    pathWithAlarm.getGpsPoints().add(gpsPoint);
                }
                previous = gpsPoint;

            }
            //循环结束时，我们最后一条路径没有结束时间
            pathWithAlarm.setEnd(previous.getPersistTime());

            //如果查询出来的最后一个gps远远早于截至时间,我们要创建一个新的停止path
            if (Minutes.minutesBetween(previous.getPersistTime(), end).getMinutes() > 10) {
                pathWithAlarm = new PathWithAlarm();
                pathWithAlarm.setStart(previous.getPersistTime());
                pathWithAlarm.setEnd(end);
                pathWithAlarm.getGpsPoints().add(previous);
                pathWithAlarmList.add(pathWithAlarm);
            }


            pathWithAlarm.setEnd(end);          // TODO  是不是需要放在上面的 区域
            Iterator<PathWithAlarm> pathIter = pathWithAlarmList.iterator();
            PathWithAlarm previousPath = null;

            while (pathIter.hasNext()) {
                PathWithAlarm singlePathWithAlarm = pathIter.next();


                if (previousPath != null && previousPath.getGpsPoints().size() == 1 &&
                        singlePathWithAlarm.getGpsPoints().size() == 1) {
                    //连续两段都是gps停止状态。延长前一段并且丢弃后一段
                    previousPath.setEnd(singlePathWithAlarm.getEnd());
                    pathIter.remove();
                    continue;

                }
                previousPath = singlePathWithAlarm;
                singlePathWithAlarm.setCar(car);
                TBusScheduleCarEntity scheduleCar = scheduleRepository.getScheduleCarByCarIdAndTime(car.getId(),
                        Iterables.getLast(singlePathWithAlarm.getGpsPoints()).getPersistTime(), principalExt);
                TRsDriverinfoEntity driverinfoEntity = null;
                if (scheduleCar != null && scheduleCar.getDriver() != null) {
                    driverinfoEntity = scheduleCar.getDriver();
                } else {
                    driverinfoEntity = carinfoEntity.getDriver();
                }
                singlePathWithAlarm.setDriver(fromEntity(driverinfoEntity));
                List<GpsPoint> adjustedPoints = new ArrayList<>();
                singlePathWithAlarm.setAdjustedPoints(adjustedPoints);
                if (singlePathWithAlarm.getGpsPoints().size() == 1) {
                    singlePathWithAlarm.setMoving(false);
                    GpsPoint originGpsPoint = singlePathWithAlarm.getGpsPoints().get(0);
                    GpsPoint gpsPoint = new GpsPoint();
                    gpsPoint.setCar(car);
                    gpsPoint.setLat(originGpsPoint.getLat());
                    gpsPoint.setLng(originGpsPoint.getLng());
                    gpsPoint.setSpeed(originGpsPoint.getSpeed());
                    adjustedPoints.add(gpsPoint);
                } else {
                    singlePathWithAlarm.setMoving(true);
                    DateTime pathStart = singlePathWithAlarm.getStart();
                    DateTime pathEnd = singlePathWithAlarm.getEnd();
                    List<TGpsAdjustedPath> adjustedPath =
                            gpsRepository.getValidGpsAdjustedPathByCarIdAndTime(car.getId(), pathStart, pathEnd, principalExt);
                    String prePathLocation = null;

                    for (TGpsAdjustedPath tGpsAdjustedPath : adjustedPath) {
                        String pathSegment = tGpsAdjustedPath.getPath();
                        String[] pathLngLat = pathSegment.split(";");
                        for (String lngLat : pathLngLat) {
                            if (prePathLocation == null) {
                                prePathLocation = lngLat;
                            } else {
                                if (prePathLocation.equals(lngLat)) {
                                    continue;
                                } else {
                                    prePathLocation = lngLat;
                                }
                            }

                            double lng = Double.valueOf(lngLat.split(",")[0]);
                            double lat = Double.valueOf(lngLat.split(",")[1]);
                            GpsPoint gpsPoint = new GpsPoint();
                            gpsPoint.setCar(car);
                            gpsPoint.setLat(lat);
                            gpsPoint.setLng(lng);
                            Double nullableSpeed = tGpsAdjustedPath.getSpeed();
                            if (nullableSpeed != null) {
                                gpsPoint.setSpeed(tGpsAdjustedPath.getSpeed());
                            } else {
                                gpsPoint.setSpeed(0);
                            }
                            adjustedPoints.add(gpsPoint);
                        }

                    }
                }
            }

        }


        return pathWithAlarmList;
    }


    public GpsPoint getGpsPointAtOrBeforTime(String id, DateTime datetime, PrincipalExt principalExt) {
        List<TGpsPointEntity> gpsEntities = gpsRepository.getGpsRecordByCarIdAndBeforeTime(id, datetime, 2, principalExt);
        if (gpsEntities.size() == 0) {
            return null;
        }

        //因为返回的数据是按照时间逆序
        Collections.reverse(gpsEntities);
        List<GpsPoint> gpsPoints = new ArrayList<>();
        //计算出所有的gps点,并且按照时间顺序排序
        for (TGpsPointEntity gpsEntity : gpsEntities) {
            gpsPoints.addAll(GpsUtil.fromPersistPointsWithShallowCarObject(gpsEntity));
        }

        Collections.reverse(gpsPoints);
        for (GpsPoint gpsPoint : gpsPoints) {
            if (
                    gpsPoint != null && (
                            gpsPoint.getPersistTime().isEqual(datetime) ||
                                    gpsPoint.getPersistTime().isBefore(datetime)
                    )
                    ) {
                gpsPoint.setCar(fromEntity(gpsEntities.get(0).getCar()));
                return gpsPoint;
            }
        }
        return null;
    }

    public List<GpsPoint> getAllMovingGpsPoints(List<TGpsPointEntity> gpsEntites) {
        if (gpsEntites.size()==0){
            return Collections.EMPTY_LIST;
        }
        List<GpsPoint> gpsPoints = new ArrayList<>();
        Car car = fromEntity(gpsEntites.get(0).getCar());
        //为了检测是否是同一个车辆位置.我们在这里存储最后一个有效的gps点位

        GpsPoint previous = null;
        for (TGpsPointEntity gpsEntite : gpsEntites) {
            for (GpsPoint gpsPoint : GpsUtil.fromPersistPointsWithShallowCarObject(gpsEntite)) {
                //本次GPS点位不为null并且previous为null=>是第一个有效的gps点位
                //本次GPS点位不为null并且和previous不是同一个位置=>车辆的新位置
                //其他点位都是停止状态.不用加入gpsPoint列表
                if (gpsPoint != null &&
                        (previous == null || !GpsUtil.isSameLocation(previous, gpsPoint))) {
                    {
                        gpsPoint.setCar(car);
                        gpsPoints.add(gpsPoint);
                        previous = gpsPoint;
                    }
                }
            }
        }

        return gpsPoints;
    }
}
