package com.yunguchang.alarm;

import com.yunguchang.data.DepotRepository;
import com.yunguchang.data.GpsRepository;
import com.yunguchang.gps.GpsUtil;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import com.yunguchang.model.persistence.TBusAlarmEntity;
import com.yunguchang.model.persistence.TBusNoReturningDepotEntity;
import com.yunguchang.model.persistence.TGpsPointEntity;
import org.joda.time.DateTime;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Created by gongy on 2015/10/14.
 */
@Stateless
public class NoGpsPointsExecutor {

    @Inject
    private GpsRepository gpsRepository;

    @Inject
    private DepotRepository depotRepository;


    @Inject
    @Fire
    private Event<AlarmEvent> alarmEventSource;


    @Inject
    @Clear
    private Event<AlarmEvent> clearAlarmEventSource;
    @Asynchronous
    public void cacl() {
        DateTime now=DateTime.now();
        List<TAzCarinfoEntity> results = gpsRepository.getCarsNoGpsMoreThan(9);
        for (TAzCarinfoEntity carEntity : results) {
            AlarmEvent alarmEvent=new AlarmEvent();
            alarmEvent.setCar(EntityConvert.fromEntity(carEntity));
            alarmEvent.setStart(now);
            alarmEvent.setEnd(now);
            alarmEvent.setAlarm(Alarm.NOGPSPOINTS);
            alarmEventSource.fire(alarmEvent);
            clearAlarmEventSource.fire(alarmEvent);
        }
    }
}
