package com.yunguchang.rest.impl;

import com.yunguchang.data.SyncDataStatusRepository;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.sync.*;
import com.yunguchang.rest.SecurityUtil;
import com.yunguchang.rest.SyncService;
import com.yunguchang.sam.PrincipalExt;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.naming.AuthenticationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 禕 on 2015/10/15.
 */
public class SyncServiceImpl implements SyncService {
    // 调度单
    @Inject
    private SyncDataStatusRepository syncDataStatusRepository;
    @Inject
    private Logger logger;


    public static Map<String, List<String>> cachedData = new HashMap<>();
    public static Map<String, Long> cachedDataTimer = new HashMap<>();

    public Response requestDispatcher(Map<String, Object> paramMap, @Context SecurityContext securityContext) throws Exception {
        PrincipalExt principalExt = SecurityUtil.getPrincipalExtOrNull(securityContext);
        if (principalExt == null ||
                !("sync".equals(principalExt.getUserIdOrNull())
                        || "admin".equals(principalExt.getUserIdOrNull())
                        || "txyw".equals(principalExt.getUserIdOrNull())
                        || "gsyw".equals(principalExt.getUserIdOrNull()))) {
            throw new AuthenticationException("no auth : " + (principalExt == null ? principalExt : principalExt.getUserIdOrNull()));
        }
        logger.info(paramMap);

        String paramStr = String.valueOf(paramMap.get("key"));
        if (paramStr.indexOf("@$") == -1) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        int idIndex = paramStr.lastIndexOf("@$");
        String data = paramStr.substring(0, idIndex);

        String uuid = paramStr.substring(idIndex + 2);
        String[] dataPart = uuid.split(":");
        if (dataPart.length > 1) {
            String orgUuid = dataPart[0];
            String[] pageInfo = dataPart[1].split("/");
            if (pageInfo.length == 2) {
                Integer currentPageIndex = Integer.valueOf(pageInfo[0]);
                Integer fullPage = Integer.valueOf(pageInfo[1]);
                List<String> orgData;
                if (!"1/1".equals(dataPart[1])) {
                    orgData = cachedData.get(orgUuid);
                    if (orgData == null) {
                        orgData = new ArrayList<>(fullPage);
                        for (int i = 0; i < fullPage; i++) {
                            orgData.add(null);
                        }
                        cachedData.put(orgUuid, orgData);
                        cachedDataTimer.put(orgUuid, DateTime.now().getMillis());
                    }
                    if(orgData.size() != fullPage) {
                        throw new InvalidParameterException("Data is error: " + orgData.size() + " -> " + fullPage);
                    }
                    orgData.set(currentPageIndex - 1, data);
                    List tmp = new ArrayList<>(orgData);
                    tmp.remove(null);
                    if (tmp.size() != fullPage) {
                        return Response.ok().build();
                    } else {        // 全部信息齐全
                        StringBuffer fullData = new StringBuffer();

                        for (String partData : orgData) {
                            fullData.append(partData);
                        }
                        data = fullData.toString();
                        cachedData.remove(orgUuid);
                        cachedDataTimer.remove(orgUuid);
                    }
                }
                if(threadPool.isShutdown()) {
                    threadPool = Executors.newFixedThreadPool(50);
                }
                threadPool.execute(new SyncRunnable(uuid, data, principalExt));
                return Response.ok().build();
            } else {
                throw new InvalidParameterException("Page info is error");
            }
        } else if (paramStr.indexOf("#") == -1) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return syncDataStatusRepository.businessDispatcher(uuid, data, principalExt);
    }

    public static ExecutorService threadPool = Executors.newFixedThreadPool(50);

    private class SyncRunnable implements Runnable {
        private final String uuid;
        private final String data;
        private final PrincipalExt userInfo;
        public SyncRunnable(String uuid, String data, PrincipalExt userInfo){
            this.uuid = uuid;
            this.data = data;
            this.userInfo = userInfo;
        }
        @Override
        public void run() {
            syncDataStatusRepository.businessDispatcher(uuid,data,userInfo);
        }
    }
    @Override
    public Response updateScheduleCar(String scheduleCarId, String newCarId, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.updateScheduleCar(scheduleCarId, newCarId, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response updateScheduleDriver(String scheduleId, String newDriverId, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.updateScheduleDriver(scheduleId, newDriverId, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response devolveApply(String applyId, String isSend, String status, String reason, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.devolveApply(applyId, isSend, status, reason, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response retreatDevolveApply(String applyId, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.retreatDevolveApply(applyId, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response concertApply(String applyId, String sendUserId, String sscd, String isSend, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.concertApply(applyId, sendUserId, sscd, isSend, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response retreatApply(String applyId, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.retreatApply(applyId, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response cancelApply(String applyId, ScheduleCancelType cancelType, String reason, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.cancelApply(applyId, cancelType, reason, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response finishSchedule(String scheduleCarId, SyncRecord syncRecord, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.finishSchedule(scheduleCarId, syncRecord, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response deleteSchedule(String scheduleId, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.deleteSchedule(scheduleId, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response cancelSchedule(String scheduleId, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.cancelSchedule(scheduleId, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response checkSyncResult(@Context SecurityContext securityContext) throws Exception {
        return syncDataStatusRepository.checkSyncResult(SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response checkBackSyncResult(String param, @Context SecurityContext securityContext) throws Exception {
        return syncDataStatusRepository.checkBackSyncResult(param, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response insert(SyncFleet fleet, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.insert(fleet, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response delete(String fleetId, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.delete(fleetId, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response update(String orgId, SyncFleet fleet, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.update(orgId, fleet, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response updateGps(String carId, String gpsFlg, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.updateGps(carId, gpsFlg, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response insert(CarJJForSync car, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.insert(car, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response update(String carGuid, SyncCar car, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.update(carGuid, car, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response deleteCar(String carGuid, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.deleteCar(carGuid, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response insert(SyncDriver driver, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.insert(driver, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response update(String driverGuid, SyncDriver driver, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.update(driverGuid, driver, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response deleteDriver(String driverGuid, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.deleteDriver(driverGuid, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response update(String userGuid, List<String> roles, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.update(userGuid, roles, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response insert(SyncUser user, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.insert(user, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response update(String userGuid, SyncUser user, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.update(userGuid, user, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response deleteUser(String userGuid, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.deleteUser(userGuid, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response update(String carGuid, SyncCarRepairing repairing, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.update(carGuid, repairing, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response update(String carGuid, SyncViolation violation, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.update(carGuid, violation, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response insertOrUpdate(ScheduleApplyInfoForSync applyInfoForSync, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.insertOrUpdate(applyInfoForSync, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response insert(String applyNo, ApproveSugForSync applyInfo, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.insert(applyNo, applyInfo, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response insert(String applyId, String applyNo, SyncEvaluateInfo evaluateInfo, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.insert(applyId, applyNo, evaluateInfo, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response update(SyncSchedule schedule, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.update(schedule, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response update(String driverGuid, SyncLeave leave, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.update(driverGuid, leave, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response create(SyncMainUser mainUser, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.create(mainUser, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response update(String mainUserId, SyncMainUser mainUser, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.update(mainUserId, mainUser, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response deleteMainUser(String mainUserId, @Context SecurityContext securityContext) {
        return syncDataStatusRepository.deleteMainUser(mainUserId, SecurityUtil.getPrincipalExtOrNull(securityContext));
    }

    @Override
    public Response getCacheData(@Context SecurityContext securityContext) throws Exception {
        return Response.ok(cachedData).build();
    }
}
