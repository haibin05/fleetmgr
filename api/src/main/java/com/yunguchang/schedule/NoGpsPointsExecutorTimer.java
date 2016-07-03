package com.yunguchang.schedule;

import com.yunguchang.alarm.NoGpsPointsExecutor;
import com.yunguchang.alarm.NoReturnExecutor;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Created by gongy on 2015/10/14.
 */
@Singleton
@Startup
public class NoGpsPointsExecutorTimer {



    @Inject
    private NoGpsPointsExecutor noGpsPointsExecutor;



    @Schedule(second = "0", minute = "0", hour = "21", persistent = false)
    public void caclNoGpsPoint(){
        noGpsPointsExecutor.cacl();
    }




}
