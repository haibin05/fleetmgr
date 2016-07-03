package com.yunguchang.schedule;

import com.yunguchang.gps.GpsFetcher;
import com.yunguchang.logger.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Created by gongy on 2015/10/14.
 */
@Singleton
@Startup
public class GpsFetcherTimer {

    @Inject
    private Logger logger;

    @Inject
    private GpsFetcher gpsFetcher;


    @Schedule(second = "*/15", minute = "*", hour = "*", persistent = false)
    public void startFetch(){
        logger.fetchGpsData();
        gpsFetcher.fetchGps();
        logger.fetchGpsDataFinished();
    }



}
