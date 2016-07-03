package com.yunguchang.alarm;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.yunguchang.data.GpsRepository;
import com.yunguchang.data.VehicleRepository;
import com.yunguchang.gps.GpsUtil;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import com.yunguchang.model.persistence.TEmbeddedDepot;
import com.yunguchang.model.persistence.TGpsPointEntity;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Created by 禕 on 2015/12/24.
 */
@Stateless
public class RuleBridge {
    @Inject
    private GpsRepository gpsRepository;

    @Inject
    private Logger logger;
    @Inject
    private VehicleRepository vehicleRepository;

    public boolean isInDepot(String carId, double distance) {

        logger.debug("check if " + carId + " in depot, target distance is " + distance);


        GpsPoint point = getLastPoint(carId);
        if (point == null) {
            logger.warn("getLastPoint is null of carId " + carId);
            return false;
        }
        logger.debug("getLastPoint " + point);

        TAzCarinfoEntity carEntity = vehicleRepository.getCarById(carId);
        if (carEntity == null) {
            logger.warn("carEntity is null of carId " + carId);
            return false;
        }
        TEmbeddedDepot depot = carEntity.getDepot();
        if (depot.getLatitude() == null || depot.getLongitude() == null) {
            logger.warn("depot is invalid of carId " + carId);
            return false;
        }
        logger.debug("depot.getLatitude " + depot.getLatitude() + " depot.getLongitude " + depot.getLongitude());

        double currentDistance = GpsUtil.getDistance(depot.getLatitude(), depot.getLongitude(), point.getLat(), point.getLng());
        logger.debug("current distance is " + currentDistance);

        return currentDistance < distance;


    }


    public GpsPoint getLastPoint(String carId) {
        TGpsPointEntity pointEntity = gpsRepository.getLastGpsPointRecord(carId);
        if (pointEntity == null) {
            return null;
        }

        List<GpsPoint> points = GpsUtil.fromPersistPointsWithShallowCarObject(pointEntity);
        Collections.reverse(points);
        return Iterables.find(points, Predicates.notNull(), null);
    }
}
