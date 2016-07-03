package com.yunguchang.schedule;

import com.yunguchang.gps.PathAdjusterExecutor;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Created by gongy on 2015/10/14.
 */
@Singleton
@Startup
public class PathAdjusterTimer {


    @Inject
    private PathAdjusterExecutor pathAdjuster;


    @Schedule(second = "0", minute = "*/5", hour = "*", persistent = false)
    public void adjustPath() {
        pathAdjuster.adjust();
    }
}
