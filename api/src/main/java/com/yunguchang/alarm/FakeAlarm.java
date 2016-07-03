package com.yunguchang.alarm;

import com.yunguchang.data.VehicleRepository;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.Car;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import org.joda.time.DateTime;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gongy on 2015/10/4.
 */
@Stateless
public class FakeAlarm {
    @Resource
    private SessionContext ctx;
    @Inject
    @KSession("basicKSession")
    private KieSession kSession;
    @Inject
    private VehicleRepository vehicleRepository;
    @Asynchronous
    public void fakeSpeedingAlarm(){


        List<GpsPoint> gpsPoints=new ArrayList<>();
        double lng=120.73347013219;

        for(int i=0;i<2;i++){
            GpsPoint gpsPoint=new GpsPoint();
            gpsPoint.setLat(30.800698965253);
            gpsPoint.setLng(lng);
            gpsPoint.setSpeed(60);
            gpsPoints.add(gpsPoint);
            lng -= 0.002;
        }


        for(int i=0;i<10;i++){
            GpsPoint gpsPoint=new GpsPoint();
            gpsPoint.setLat(30.800698965253);
            gpsPoint.setLng(lng);
            gpsPoint.setSpeed(130);
            gpsPoints.add(gpsPoint);
            lng -= 0.002;
        }




        for(int i=0;i<6;i++){
            GpsPoint gpsPoint=new GpsPoint();
            gpsPoint.setLat(30.800698965253);
            gpsPoint.setLng(lng);
            gpsPoint.setSpeed(60);
            gpsPoints.add(gpsPoint);
            lng -= 0.002;
        }


        for (int i=0;i<gpsPoints.size();i++){
            GpsPoint gpsPoint=gpsPoints.get(i);
            ctx.getTimerService().createSingleActionTimer((i+1)*15000l,new TimerConfig(gpsPoint.toJson(),false));

        }

    }


    @Asynchronous
    public void goOut()  {


        List<GpsPoint> gpsPoints=new ArrayList<>();
        double lng=120.60386760642;




        for(int i=0;i<20;i++){
            GpsPoint gpsPoint=new GpsPoint();
            gpsPoint.setLat(30.786726237643);
            gpsPoint.setLng(lng);
            gpsPoint.setSpeed(60);
            gpsPoints.add(gpsPoint);
            lng += 0.002;
        }





        for (int i=0;i<gpsPoints.size();i++){
            GpsPoint gpsPoint=gpsPoints.get(i);
            ctx.getTimerService().createSingleActionTimer((i+1)*15000l,new TimerConfig(gpsPoint.toJson(),false));

        }

    }


    @Asynchronous
    public void returnDepot()  {


        List<GpsPoint> gpsPoints=new ArrayList<>();
        double lng=120.733552;



        for(int i=0;i<20;i++){
            GpsPoint gpsPoint=new GpsPoint();
            gpsPoint.setLat(30.799048);
            gpsPoint.setLng(lng);
            gpsPoint.setSpeed(60);
            gpsPoints.add(gpsPoint);
            lng += 0.002;
        }

        Collections.reverse(gpsPoints);





        for (int i=0;i<gpsPoints.size();i++){
            GpsPoint gpsPoint=gpsPoints.get(i);
            ctx.getTimerService().createSingleActionTimer((i+1)*15000l,new TimerConfig(gpsPoint.toJson(),false));

        }

    }


    @Timeout
    public void timeoutHadnel(Timer timer)  {
        GpsPoint gpsPoint = GpsPoint.fromJson((String) timer.getInfo());
        if (gpsPoint==null){
            return;
        }
        gpsPoint.setSampleTime(DateTime.now());
        TAzCarinfoEntity carEntity = vehicleRepository.getCarByBadge("37291");
        Car car = EntityConvert.fromEntity(carEntity);
        gpsPoint.setCar(car);
        kSession.insert(gpsPoint);

    }
}
