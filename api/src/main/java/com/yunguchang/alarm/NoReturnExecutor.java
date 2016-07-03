package com.yunguchang.alarm;

import com.yunguchang.data.DepotRepository;
import com.yunguchang.data.GpsRepository;
import com.yunguchang.gps.Const;
import com.yunguchang.gps.GpsUtil;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.Car;
import com.yunguchang.model.common.Depot;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.persistence.TBusNoReturningDepotEntity;
import com.yunguchang.model.persistence.TGpsPointEntity;
import org.joda.time.DateTime;

import javax.ejb.Asynchronous;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Created by gongy on 2015/10/14.
 */
@Stateless
public class NoReturnExecutor {

    @Inject
    private GpsRepository gpsRepository;

    @Inject
    private DepotRepository depotRepository;
    @Asynchronous
    public void cacl() {
        List<TGpsPointEntity> results = gpsRepository.getLastGpsPointOfEachCar();
        for (TGpsPointEntity pointEntity : results) {
            List<GpsPoint> points = GpsUtil.fromPersistPointsWithShallowCarObject(pointEntity);
            Car car= EntityConvert.fromEntity(pointEntity.getCar());
            if (car == null || !car.hasValidDepot()){
                continue;
            }

            Collections.reverse(points);
            for (GpsPoint point : points) {
                if (point != null) {
                    Depot depot = car.getDepot();
                    double distance = GpsUtil.getDistance(depot.getLat(), depot.getLng(), point.getLat(), point.getLng());
                    if (distance > Const.DISTANCE_TO_DEPOT) {
                        TBusNoReturningDepotEntity noReturningDepotEntity=new TBusNoReturningDepotEntity();
                        noReturningDepotEntity.setCar(pointEntity.getCar());
                        noReturningDepotEntity.setDateTime(DateTime.now());
                        depotRepository.save(noReturningDepotEntity);

                    }
                    break;
                }
            }
        }
    }
}
