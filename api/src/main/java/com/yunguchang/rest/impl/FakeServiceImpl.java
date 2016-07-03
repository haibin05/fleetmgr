package com.yunguchang.rest.impl;

import com.yunguchang.alarm.*;
import com.yunguchang.data.VehicleRepository;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.common.AlarmEvent;
import com.yunguchang.model.common.Car;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gongy on 2015/11/12.
 */
@Path("/fake")
@RequestScoped
@Stateful
public class FakeServiceImpl {

    @Inject
    private VehicleRepository vehicleRepository;

    @Inject
    @Fire
    private Event<AlarmEvent> alarmEventSource;

    @Inject
    private FakeAlarm fakeAlarm;
    @Inject
    private NoReturnExecutor noReturnExecutor;

    @Inject
    private NoGpsPointsExecutor noGpsPointsExecutor;


    @Inject
    @Clear
    private Event<AlarmEvent> clearAlarmEventSource;

    @Inject
    @KSession("basicKSession")
    private KieSession kSession;

    @GET
    @NoCache
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test(@QueryParam("alarm") String alarmName) {

        final AlarmEvent event = new AlarmEvent();

        Alarm alarm = null;
        try {
            if (alarmName != null)
                alarm = Alarm.valueOf(alarmName);
        } catch (Exception e) {

        }
        if (alarm == null) {
            alarm = Alarm.SPEEDING;
        }
        event.setAlarm(alarm);
        TAzCarinfoEntity carEntity = vehicleRepository.getCarByBadge("37291");
        Car car = EntityConvert.fromEntity(carEntity);
        event.setCar(car);
        event.setStart(DateTime.now());
        GpsPoint gpsPoint = new GpsPoint();
        gpsPoint.setCar(car);
        gpsPoint.setLng(120.73347013219);
        gpsPoint.setLat(30.800698965253);
        gpsPoint.setSpeed(130);
        gpsPoint.setSampleTime(DateTime.now());
        event.setGpsPoint(gpsPoint);
        event.setStart(DateTime.now());

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                event.setEnd(DateTime.now());
                clearAlarmEventSource.fire(event);
            }
        }, 3000);
        alarmEventSource.fire(event);

        return "OK";
    }




    @GET
    @NoCache
    @Path("/speeding")
    @Produces(MediaType.TEXT_PLAIN)
    public String testSpeeding() {
        fakeAlarm.fakeSpeedingAlarm();
        return "OK";
    }


    @GET
    @NoCache
    @Path("/goout")
    @Produces(MediaType.TEXT_PLAIN)
    public String testGoOut() {
        fakeAlarm.goOut();
        return "OK";
    }

    @GET
    @NoCache
    @Path("/return")
    @Produces(MediaType.TEXT_PLAIN)
    public String testComeBack() {
        fakeAlarm.returnDepot();
        return "OK";
    }


    @GET
    @NoCache
    @Path("/noReturn")
    @Produces(MediaType.TEXT_PLAIN)
    public String testNoReturnAlarm() {
        noReturnExecutor.cacl();
        return "OK";
    }


    @GET
    @NoCache
    @Path("/noGps")
    @Produces(MediaType.TEXT_PLAIN)
    public String testNoGpsAlarm() {
        noGpsPointsExecutor.cacl();
        return "OK";
    }
}
