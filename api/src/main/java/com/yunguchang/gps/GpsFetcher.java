package com.yunguchang.gps;


import com.google.common.base.Joiner;
import com.yunguchang.baidu.KeyPool;
import com.yunguchang.data.VehicleRepository;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

/**
 * 定时抓取GPS数据
 */
@Stateful
@ApplicationScoped
public class GpsFetcher {

    //    private static int MAX_AVAILABLE = 20;
//    private final Semaphore available = new Semaphore(MAX_AVAILABLE);
    @Inject
    private GpsCorrelate gpsCorrelate;
    @Inject
    private Logger logger;
    @Inject
    private GpsClient gpsClient;
    @Inject
    private BaiduClient baiduClient;
    @Inject
    private VehicleRepository vehicleRepository;
    @Resource(lookup = "java:jboss/ee/concurrency/executor/gps")
    private ManagedExecutorService mes;

    @Inject
    private KeyPool keyPool;


    public void fetchGps() {

        DateTime persistTime = new DateTime().millisOfSecond().roundFloorCopy();

        List<GpsPoint> points = new ArrayList<>();


        List<TAzCarinfoEntity> cars = vehicleRepository.listAllCarsWithGPS();
        List<Future<GpsPoint>> futureList = new ArrayList<>();
        for (final TAzCarinfoEntity car : cars) {

            try {
                Future<GpsPoint> futuer = mes.submit(new Callable<GpsPoint>() {
                    @Override
                    public GpsPoint call() throws Exception {
                        try {
                            if (StringUtils.isBlank(car.getCphm())) {
                                logger.error(String.format("Badge is empty of car id %s", car.getCphm()));
                                return null;
                            }
                            Map<String, String> gpsRawData = gpsClient.getGpsData(car.getCphm());

                            if (gpsRawData == null) {
                                return null;
                            }
                            GpsPoint gpsPoint = GpsUtil.fromResponse(gpsRawData);
                            if (gpsPoint == null) {
                                return null;
                            }

                            if (isInChina(gpsPoint)) {
                                return gpsPoint;
                            } else {
                                return null;
                            }

                        } catch (RuntimeException e) {
                            Throwable rootCause = ExceptionUtils.getRootCause(e);
                            if (rootCause == null) {
                                rootCause = e;
                            }
                            logger.error(String.format("Cant' fetch %s", car.getCphm()), rootCause);
                        } finally {
                        }
                        return null;
                    }
                });
                futureList.add(futuer);
            } catch (RejectedExecutionException e) {
            }

        }

        for (Future<GpsPoint> future : futureList) {
            try {
                GpsPoint gpsPoint = future.get();
                if (gpsPoint != null) {
                    points.add(gpsPoint);
                }
            } catch (InterruptedException e) {

            } catch (ExecutionException e) {

            }
        }
        try {
            gpsCorrelate.putGpsPoints(convertToBaidu(points), persistTime);
        } catch (Exception e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if (rootCause == null) {
                rootCause = e;
            }
            logger.error("error when persist gps points", rootCause);
            e.printStackTrace();
        }
    }

    private boolean isInChina(GpsPoint gpsPoint) {
//          最北：漠河以北黑龙江主航道的中心线，北纬53度多。 
//        　最南：南海南沙群岛中的曾母暗沙，北纬4度附近。 
//        　最东：黑龙江与乌苏里江主航道中心线的交汇处，东经135度多。 
//        　最西；新疆帕米尔高原，东经75度附近。
//          否则百度会出错

        if (gpsPoint.getLat() > 3 && gpsPoint.getLat() < 54 && gpsPoint.getLng() > 74 && gpsPoint.getLng() < 136) {
            return true;
        } else {
            return false;
        }

    }

    //这个函数会修改原始数据。并且返回的就是原始的gpsPoints参数
    //小心使用
    private List<GpsPoint> convertToBaidu(List<GpsPoint> gpsPoints) {


        //百度只支持最多100个坐标转化
        for (int p = 0; p < gpsPoints.size(); p += 100) {
            List<String> coords = new ArrayList<String>();
            List<GpsPoint> gpsPointSubList = gpsPoints.subList(p, Math.min(gpsPoints.size(), p + 100));
            for (GpsPoint gpsPoint : gpsPointSubList) {
                coords.add(gpsPoint.getLng() + "," + gpsPoint.getLat());
            }


            GeoconvResult result = baiduClient.geoconv(Joiner.on(";").join(coords), 1, 5, keyPool.getReservedKey());
            if (result.getResult() == null) {
                return Collections.EMPTY_LIST;
            }

            for (int i = 0; i < result.getResult().length; i++) {
                GpsPoint gpsPoint = gpsPointSubList.get(i);
                gpsPoint.setLat(result.getResult()[i].getY());
                gpsPoint.setLng(result.getResult()[i].getX());
            }
        }


        return gpsPoints;
    }


}
