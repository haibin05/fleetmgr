package com.yunguchang.gps;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.yunguchang.data.GpsRepository;
import com.yunguchang.data.VehicleRepository;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import com.yunguchang.model.persistence.TGpsPointEntity;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GPS 数据归集
 * 将多点GPS归集到单条记录上
 */
@ApplicationScoped
@Stateful
public class GpsCorrelate {


    @Inject
    VehicleRepository vehicleRepository;
    private Map<String, TAzCarinfoEntity> badgesMap = new HashMap<String, TAzCarinfoEntity>();
    //private Map<String, TGpsPointEntity> gpsPointMap = new HashMap<String, TGpsPointEntity>();
    private Map<String, GpsPoint> gpsLastPointMap = new HashMap<String, GpsPoint>();
    @Inject
    private Logger logger;
    @Inject
    private GpsRepository gpsRepository;


    private Map<String, TGpsPointEntity> gpsEntitiesMapping=new HashMap<>();

    @Inject
    private Event<GpsPoint> gpsEvent;

    @PostConstruct
    public void fillBadgesMap() {
        List<TAzCarinfoEntity> cars = vehicleRepository.listAllCarsWithGPS();
        for (TAzCarinfoEntity car : cars) {
            badgesMap.put(car.getCphm(), car);
        }
        List<TGpsPointEntity> lastGpsPoints = gpsRepository.getLastGpsPointOfEachCar();
        for (TGpsPointEntity lastGpsPoint : lastGpsPoints) {
            List<GpsPoint> points = GpsUtil.fromPersistPointsWithShallowCarObject(lastGpsPoint);
            Collections.reverse(points);
            GpsPoint point = Iterables.find(points, Predicates.notNull(), null);
            if (point != null) {
                gpsLastPointMap.put(lastGpsPoint.getCar().getId(), point);
            }
        }

    }

    public void putGpsPoints(List<GpsPoint> gpsPoints, DateTime persistTime) {
        try {
            DateTime pointTime=GpsUtil.getPersistPointTime(persistTime);
            for (GpsPoint point : gpsPoints) {
                TAzCarinfoEntity car = getCarFromBadge(point.getCar().getBadge());
                if (car == null) {
                    continue;
                }
                point.getCar().setId(car.getId());

                if (shouldPersist(point)) {
                    try {
                        TGpsPointEntity gpsEntity = getGpsEntity(car.getId(), pointTime);
                        gpsRepository.mergePoint(gpsEntity.getId(), point, persistTime);
                        //这个事件由规则引擎处理
                        gpsEvent.fire(point);
                    } catch (Exception e) {
                        logger.error(String.format("errors happen when persistin point %s", point.toString()));
                        e.printStackTrace();
                    }
                } else {
                    logger.dropGpsPoint(
                            point.getCar().getBadge(), persistTime, point.getSampleTime());
                }
            }

        } finally {

        }

    }


    private boolean shouldPersist(GpsPoint gpsPoint) {
        if (gpsPoint == null) {
            return false;
        }
        GpsPoint lastPoint = gpsLastPointMap.get(gpsPoint.getCar().getId());
        if (lastPoint == null) {
            gpsLastPointMap.put(gpsPoint.getCar().getId(), gpsPoint);
            return true;
        }


        //如果前后两次采样时间相同，忽略


        if (lastPoint.getSampleTime().isEqual(gpsPoint.getSampleTime())) {
            return false;
        } else {
            gpsLastPointMap.put(gpsPoint.getCar().getId(), gpsPoint);
            return true;
        }

    }

    private TGpsPointEntity getGpsEntity(String carId,DateTime pointTime){
        TGpsPointEntity gpsEntity = gpsEntitiesMapping.get(carId);
        if (gpsEntity==null || !gpsEntity.getPointTime().isEqual(pointTime)){
            gpsEntity=gpsRepository.getGpsRecordByCarIdAndAfterPointTime(carId, pointTime);
            if (gpsEntity==null){
                gpsEntity=gpsRepository.createRecord(carId, pointTime);
            }
            gpsEntitiesMapping.put(carId,gpsEntity);
        }
        return gpsEntity;
    }

    private TAzCarinfoEntity getCarFromBadge(String badge) {
        TAzCarinfoEntity car = badgesMap.get(badge);
        if (car == null) {
            car = vehicleRepository.getCarByBadge(badge);
            if (car != null) {
                badgesMap.put(badge, car);
            }
        }
        return car;
    }
}
