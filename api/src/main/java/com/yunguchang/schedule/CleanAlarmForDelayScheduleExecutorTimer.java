package com.yunguchang.schedule;

import com.yunguchang.data.AlarmRepository;
import com.yunguchang.data.ScheduleRepository;
import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.persistence.TBusAlarmEntity;
import com.yunguchang.model.persistence.TBusScheduleCarEntity;
import com.yunguchang.sam.PrincipalExt;
import org.joda.time.DateTime;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.yunguchang.schedule.ScheduleRunnerUtility.getOneUserWithAdminRole;

/**
 * Created by 禕 on 2016/1/2.
 */
@Singleton
@Startup
public class CleanAlarmForDelayScheduleExecutorTimer {

    @Inject
    private AlarmRepository alarmRepository;
    @Inject
    private ScheduleRepository scheduleRepository;

    @Schedule(second = "0", minute = "*/5", hour = "*", persistent = false)
    public void matchScheduleCarAndAlarm() {

        DateTime endTime = DateTime.now();
        DateTime alarmStartTime = DateTime.now().withTimeAtStartOfDay();
        Alarm alarm = Alarm.NOSCHEDULE;
        PrincipalExt principalExt = getOneUserWithAdminRole();

        DateTime delayStartTime = DateTime.now().minusMinutes(15);
        List<TBusScheduleCarEntity> scheduleCarEntityList = scheduleRepository.getAllScheduleCarsInfo(delayStartTime, endTime, principalExt);
        for (TBusScheduleCarEntity scheduleCarEntity : new ArrayList<>(scheduleCarEntityList)) {
            List<TBusAlarmEntity> alarmEntityList = alarmRepository.getCarAlarmByStartEndType(scheduleCarEntity.getCar().getId(), alarmStartTime, endTime, alarm, principalExt);

            for (TBusAlarmEntity alarmEntity : alarmEntityList) {
                if (alarmEntity.getCar().getId().equals(scheduleCarEntity.getCar().getId()))
                    alarmRepository.cleanAlarm(alarmEntity.getId());
            }
        }

    }
}
