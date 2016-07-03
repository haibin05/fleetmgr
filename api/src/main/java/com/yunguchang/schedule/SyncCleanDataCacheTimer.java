package com.yunguchang.schedule;

import org.joda.time.DateTime;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Set;

import static com.yunguchang.rest.impl.SyncServiceImpl.cachedData;
import static com.yunguchang.rest.impl.SyncServiceImpl.cachedDataTimer;

/**
 * Created by haibin on 2016/3/31.
 */
@Singleton
@Startup
public class SyncCleanDataCacheTimer {
    @Schedule(second = "0", minute = "0", hour = "0", dayOfMonth = "1", persistent = false)
    public void cleanSyncDataCache() throws Exception {
        DateTime lastMonthTime = DateTime.now();
        lastMonthTime.plusMonths(-1);
        lastMonthTime.plusMillis(-1 * lastMonthTime.getMillisOfDay());
        Set<String> keySet = cachedDataTimer.keySet();
        for(String uuid : keySet) {
            Long dataInsertTime = cachedDataTimer.get(uuid);
            if(dataInsertTime == null) {
                continue;
            }
            if(dataInsertTime < lastMonthTime.getMillis()) {
                cachedData.remove(uuid);
            }
        }
    }
}
