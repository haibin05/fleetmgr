package com.yunguchang.schedule;

import com.yunguchang.data.SyncDataStatusRepository;
import com.yunguchang.model.persistence.TSyncDataStatusEntity;
import com.yunguchang.sam.PrincipalExt;
import org.joda.time.DateTime;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by gongy on 2015/10/14.
 */
@Singleton
@Startup
public class SyncDataRectifyTimer {
    @Inject
    private SyncDataStatusRepository syncDataStatusRepository;

    @Schedule(second = "0", minute = "*/2", hour = "*", persistent = false)
    public void rectifySyncData() throws Exception {
        PrincipalExt userPrincipal = null;

        DateTime endTime = DateTime.now();
        DateTime startTime = endTime.minusDays(2);

        List<TSyncDataStatusEntity> failedSyncData = syncDataStatusRepository.listExternalSyncDataStatus(startTime, endTime, 5, null);

        for (TSyncDataStatusEntity syncData : failedSyncData) {
            syncDataStatusRepository.businessDispatcher(syncData.getUuid(), syncData.getDataValue(), userPrincipal);
        }
    }
}
