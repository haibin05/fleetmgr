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
public class SyncDataSendMessageTimer {
    @Inject
    private SyncDataStatusRepository syncDataStatusRepository;

    @Schedule( minute = "*/5", hour = "*", persistent = false)
    public void sendMessageForSyncData() throws Exception {
        PrincipalExt userPrincipal = null;
        // 获取当前时间 从外部到内部 && 未被删除 && 尝试次数小于maxTimes && 插入时间是指定区间的数据
        List<TSyncDataStatusEntity> syncDataStatusEntityList = syncDataStatusRepository.listSyncData4SendMessageToSns(DateTime.now().minusDays(2), DateTime.now().minusMillis(1));
        for(TSyncDataStatusEntity entity : syncDataStatusEntityList) {
            int hashIndex = entity.getDataValue().indexOf("#");
            SyncDataStatusRepository.RequestType postType = SyncDataStatusRepository.RequestType.valueOf(entity.getDataValue().split("#")[0]);
            String syncData = entity.getDataValue().substring(hashIndex + 1);
            boolean result = syncDataStatusRepository.sendMessage4ExternalSyncData(syncData, postType, entity, userPrincipal);
            int loopCount = 3;
            while(result == false) {
                if(loopCount-- < 0) {
                    break;
                }
                Thread.sleep(300);
                result = syncDataStatusRepository.sendMessage4ExternalSyncData(syncData, postType, entity, userPrincipal);
            }
            Thread.sleep(600);
        }
    }
}
