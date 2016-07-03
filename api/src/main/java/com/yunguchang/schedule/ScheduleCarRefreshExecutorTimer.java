package com.yunguchang.schedule;

import com.yunguchang.alarm.RulesExecutor;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Created by 禕 on 2016/1/2.
 */
@Singleton
@Startup
public class ScheduleCarRefreshExecutorTimer {
    @Inject
    private RulesExecutor rulesExecutor;





    @Schedule( minute = "*/5", hour = "*", persistent = false)
    public void refreshScheduleCar() {
        rulesExecutor.refreshScheduleCar();
    }

}
