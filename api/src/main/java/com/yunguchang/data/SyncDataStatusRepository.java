package com.yunguchang.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.SyncEntityConvert;
import com.yunguchang.model.common.ApplyStatus;
import com.yunguchang.model.common.ScheduleStatus;
import com.yunguchang.model.persistence.*;
import com.yunguchang.model.sync.*;
import com.yunguchang.sam.PrincipalExt;
import com.yunguchang.sharetome.MessageCreator;
import com.yunguchang.sharetome.ShareTomeEvent;
import com.yunguchang.utils.tools.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.Response;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import static com.yunguchang.model.persistence.TSyncDataStatusEntity.SyncDataType;
import static com.yunguchang.sharetome.ShareTomeEvent.*;
import static com.yunguchang.sharetome.ShareTomeMessage.*;


/**
 * Created by gongy on 9/27/2015.
 */
@Stateless
public class SyncDataStatusRepository extends GenericRepository {
    @Inject
    private Logger logger;

    @TransactionAttribute(REQUIRES_NEW)
    public TSyncDataStatusEntity saveSyncDataStatus(TSyncDataStatusEntity syncDataStatusEntity, PrincipalExt principalExt) {
        if (syncDataStatusEntity.getExceptionMessage() != null && "0".equals(syncDataStatusEntity.getState())) {
            String subMessage = syncDataStatusEntity.getExceptionMessage();
            syncDataStatusEntity.setExceptionMessage(subMessage.substring(0, subMessage.length() > 300 ? 300 : subMessage.length()));
        } else {
            syncDataStatusEntity.setExceptionMessage(null);
        }
        if(syncDataStatusEntity.getUuid() == null) {
            em.persist(syncDataStatusEntity);
            return syncDataStatusEntity;
        } else {
            return em.merge(syncDataStatusEntity);
        }
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TSyncDataStatusEntity saveLocalSyncDataStatus(SyncDataType syncDataType, Object data) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String dataValue;
        String exception = null;
        if(data instanceof String) {
            dataValue = data.toString();
        } else {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper = objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSSZ"));
                dataValue = objectMapper.writeValueAsString(data);
                System.out.println( "++++++++++++++++\r\n++++++++++++++++\r\n" + dataValue);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                exception = e.getMessage();
                dataValue = data.toString();
            }
        }
        return saveLocalSyncDataStatus(uuid, syncDataType, dataValue, exception, null);
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TSyncDataStatusEntity saveLocalSyncDataStatus(String uuid, SyncDataType syncDataType, String data, String exception, PrincipalExt principalExt) {
        TSyncDataStatusEntity entity = new TSyncDataStatusEntity();
        entity.setUuid(uuid);
        entity.setToOut(true);
        entity.setDeleteFlg(false);
        entity.setTryTimes(1);
        entity.setPingTimes(0);
        entity.setDataValue(data);
        entity.setExceptionMessage(exception);
        entity.setDataType(syncDataType);
        entity.setInsertdate(new Timestamp(DateTime.now().getMillis()));
        entity.setUpdatedate(new Timestamp(DateTime.now().getMillis()));
        em.persist(entity);
        return entity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TSyncDataStatusEntity updateSyncDataStatus(String uuid, String state, String message, PrincipalExt principalExt) {
        TSyncDataStatusEntity dataStatusEntity = em.find(TSyncDataStatusEntity.class, uuid);
        dataStatusEntity.setState(state);
        dataStatusEntity.setUpdatedate(new Timestamp(new DateTime().getMillis()));
        dataStatusEntity.addTryTimes();
        dataStatusEntity.addPingTimes();
        dataStatusEntity.setExceptionMessage(message);
        return dataStatusEntity;
    }

    public TSyncDataStatusEntity findOneById(String uuid, PrincipalExt principalExt) {
        if (uuid == null) {
            return null;
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSyncDataStatusEntity> cq = cb.createQuery(TSyncDataStatusEntity.class);
        Root<TSyncDataStatusEntity> syncDataStatusEntityRoot = cq.from(TSyncDataStatusEntity.class);
        cq.where(cb.equal(syncDataStatusEntityRoot.get(TSyncDataStatusEntity_.uuid), uuid));
        cb.and(cb.equal(syncDataStatusEntityRoot.get(TSyncDataStatusEntity_.deleteFlg), false));
        List<TSyncDataStatusEntity> entityList = em.createQuery(cq).getResultList();
        if (entityList.size() != 1) {
            return null;
        }
        return entityList.get(0);
    }

    // 获取当前时间 从外部到内部 && 未被删除 && 尝试次数小于maxTimes && 插入时间是指定区间的数据
    @TransactionAttribute(REQUIRES_NEW)
    public List<TSyncDataStatusEntity> listExternalSyncDataStatus(DateTime startTime, DateTime endTime, Integer maxTimes, PrincipalExt principalExt) {
        if (startTime == null) {
            startTime = DateTimeUtil.appStartTime;
        }
        if (endTime == null) {
            endTime = DateTimeUtil.appEndTime;
        }
        int tryTimes = (maxTimes == null) ? 3 : maxTimes;

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSyncDataStatusEntity> cq = cb.createQuery(TSyncDataStatusEntity.class);
        Root<TSyncDataStatusEntity> syncDataStatus = cq.from(TSyncDataStatusEntity.class);
        Predicate predicate = cb.and(
                cb.equal(syncDataStatus.get(TSyncDataStatusEntity_.toOut), false),
                cb.lessThanOrEqualTo(syncDataStatus.get(TSyncDataStatusEntity_.tryTimes), tryTimes),
                cb.between(syncDataStatus.get(TSyncDataStatusEntity_.insertdate), new Timestamp(startTime.getMillis()), new Timestamp(endTime.getMillis()))
        );
        if (maxTimes == null) {
            predicate = cb.and(
                    predicate,
                    cb.equal(syncDataStatus.get(TSyncDataStatusEntity_.deleteFlg), false)
            );
        }
        cq.where(predicate);
        cq.orderBy(cb.asc(syncDataStatus.get(TSyncDataStatusEntity_.insertdate)));

        List<TSyncDataStatusEntity> resultList = em.createQuery(cq).getResultList();
        for (TSyncDataStatusEntity entity : resultList) {
            if (("1".equals(entity.getState()) && !entity.isDeleteFlg()) || ("0".equals(entity.getState()) && 3 == entity.getTryTimes())) {
                entity.setDeleteFlg(true);
            }
        }
        return resultList;
    }

    public List<TSyncDataStatusEntity> listSyncData4SendMessageToSns(DateTime startTime, DateTime endTime) {
        if (startTime == null) {
            startTime = DateTimeUtil.appStartTime;
        }
        if (endTime == null) {
            endTime = DateTimeUtil.appEndTime;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSyncDataStatusEntity> cq = cb.createQuery(TSyncDataStatusEntity.class);
        Root<TSyncDataStatusEntity> syncDataStatus = cq.from(TSyncDataStatusEntity.class);
        Predicate predicate = cb.and(
                cb.equal(syncDataStatus.get(TSyncDataStatusEntity_.toOut), false),
                cb.equal(syncDataStatus.get(TSyncDataStatusEntity_.deleteFlg), true),
                cb.equal(syncDataStatus.get(TSyncDataStatusEntity_.sendMessage), false),
                cb.between(syncDataStatus.get(TSyncDataStatusEntity_.insertdate), new Timestamp(startTime.getMillis()), new Timestamp(endTime.getMillis()))
        );
        cq.where(predicate);
        cq.orderBy(cb.asc(syncDataStatus.get(TSyncDataStatusEntity_.flowStep)), cb.asc(syncDataStatus.get(TSyncDataStatusEntity_.insertdate)));
        return em.createQuery(cq).getResultList();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public boolean sendMessage4ExternalSyncData(String syncData, RequestType postType, TSyncDataStatusEntity entity, PrincipalExt userPrincipal) {
        boolean result = false;
        try {
            MessageCategory messageCategory;
            switch (postType) {
                case CLDDTJSQ:        // 车辆调度-提交调度申请
                    ScheduleApplyInfoForSync applyInfoForSync = objectMapper.readValue(syncData, ScheduleApplyInfoForSync.class);
                    TBusApplyinfoEntity applyEntity = applicationRepository.getApplicationById(applyInfoForSync.getUuId());
                    if(applyEntity == null) {
                        return false;
                    }
                    shareTomeEventEvent.fire(messageCreator.sendPostMessageToSnsByApplicationEntity(applyEntity, MessageCategory.APPLICATION_CREATE, null).build());
                    break;
                case CLDDSQXG:        // 车辆调度-修改申请
                    applyInfoForSync = objectMapper.readValue(syncData, ScheduleApplyInfoForSync.class);
                    applyEntity = applicationRepository.getApplicationById(applyInfoForSync.getUuId());
                    if(applyEntity == null || applyEntity.getPostId4Coordinator() == null) {
                        return false;
                    }
                    shareTomeEventEvent.fire(messageCreator.sendPostMessageToSnsByApplicationEntity(applyEntity, MessageCategory.APPLICATION_CREATE, null).build());
                    break;
                case CLDDYCSH:       // 车辆调度-车辆调度用车审核
                    ApproveSugForSync approveSugInfo = objectMapper.readValue(syncData, ApproveSugForSync.class);
                    TBusApproveSugEntity approveSugEntity = applicationRepository.getApplyApproveInfo(approveSugInfo.getUuid());
                    if(approveSugEntity == null) {
                        return false;
                    }
                    applyEntity = applicationRepository.getApplicationByNo(approveSugInfo.getApplyNo(), userPrincipal);
                    if(applyEntity == null || applyEntity.getPostId4Coordinator() == null) {
                        return false;
                    }
                    if(approveSugEntity.getSuggest().equals("01")) {
                        messageCategory = MessageCategory.APPLICATION_APPROVE;
                    } else {
                        messageCategory = MessageCategory.APPLICATION_REJECT;
                    }
                    shareTomeEventEvent.fire(messageCreator.sendPostMessageToSnsByApplicationEntity(approveSugEntity.getApplication(), messageCategory, null).build());
                    break;
                case CLDD:           // 车辆调度
                    SyncSchedule schedule = objectMapper.readValue(syncData, SyncSchedule.class);
                    for(ScheduleApplyInfoForSync applyInfo : schedule.getApply()) {
                        applyEntity = applicationRepository.getApplicationById(applyInfo.getUuId());
                        if (applyEntity == null || applyEntity.getPostId4Coordinator() == null || applyEntity.getPostId4Auditor() == null) {
                            return false;
                        }
                        TBusApproveSugEntity approveEntity = applicationRepository.getApplyApproveInfoByApplyNo(applyEntity.getApplyno());
                        if(approveEntity == null) {
                            return false;
                        }
                    }
                    if (schedule.getRela() != null) {
                        List<TBusScheduleRelaEntity> relaEntityList = new ArrayList<>();
                        for (SchedulerelaForSync scheduleRela : schedule.getRela()) {
                            TBusScheduleRelaEntity scheduleRelaEntity = scheduleRepository.getScheduleById(scheduleRela.getUuId(), userPrincipal);
                            if(scheduleRelaEntity == null) {
                                return false;
                            }
                            relaEntityList.add(scheduleRelaEntity);
                        }
                        for(TBusScheduleRelaEntity scheduleRelaEntity : relaEntityList) {
                            if (ScheduleStatus.AWAITING.id() == scheduleRelaEntity.getStatus()) {
                                List<Builder> builderList = messageCreator.sendPostMessageToSnsByScheduleEntity(scheduleRelaEntity, MessageCategory.APPLICATION_DISPATCH);
                                for (Builder builder : builderList) {
                                    shareTomeEventEvent.fire(builder.build());
                                }
                            }
                        }
                    }
                    break;
                case CLDDAPPLYCANCEL:    // 取消已经调度的申请单
                    applyEntity = applicationRepository.getApplicationById(syncData.split("#")[0]);
                    if(applyEntity == null || applyEntity.getPostId4Coordinator() == null || applyEntity.getPostId4Auditor() == null || applyEntity.getPostId4Dispatcher() == null) {
                        return false;
                    }
                    shareTomeEventEvent.fire(messageCreator.sendPostMessageToSnsByApplicationEntity(applyEntity, null, ApplyStatus.APPLY_CANCEL.toStringValue()).build());
                    break;
                case CLDDSQSC:       // 车辆调度-申请删除
                    applyEntity = applicationRepository.getApplicationById(syncData.split("#")[0]);
                    if(applyEntity == null || applyEntity.getPostId4Coordinator() == null || applyEntity.getPostId4Auditor() == null || applyEntity.getPostId4Dispatcher() == null) {
                        return false;
                    }
                    shareTomeEventEvent.fire(messageCreator.sendPostMessageToSnsByApplicationEntity(applyEntity, null, ApplyStatus.APPLY_CANCEL.toStringValue()).build());
                    break;
                case CLDDCAR:        // 修改调度单 车辆        已经做掉
                    break;
                case CLDDJSY:        // 修改调度单 驾驶员      已经做掉
                    break;
                case DDQX:       // 车辆调度取消          已经做掉
                    break;
                case DDZF:           // 车辆调度作废
                    break;
                case CLDDXCJL:     // 车辆调度-行车记录
                    String endMile = syncData.split("#")[3];
                    String scheduleCarId = syncData.split("#")[1];
                    messageCategory = endMile == null ? MessageCategory.SCHEDULE_START : MessageCategory.SCHEDULE_END;
                    TBusRecordinfoEntity recordEntity = scheduleRepository.getScheduleCarRecordByScheduleCarIdWithPermission(scheduleCarId, userPrincipal);
                    if(recordEntity == null && recordEntity.getEndmile() < 0) {
                        return false;
                    }
                    List<Builder> builderList = messageCreator.sendPostMessageToSnsByRecordEntity(recordEntity, messageCategory);
                    for (Builder builder : builderList) {
                        shareTomeEventEvent.fire(builder.build());
                    }
                    break;
                case CLDDDDTH:       // 车辆调度-调度退回
                    applyEntity = applicationRepository.getApplicationByNo(syncData.split("#")[0], userPrincipal);
                    if(applyEntity == null || applyEntity.getPostId4Coordinator() == null || applyEntity.getPostId4Auditor() == null || applyEntity.getPostId4Dispatcher() == null) {
                        return false;
                    }
                    shareTomeEventEvent.fire(messageCreator.sendPostMessageToSnsByApplicationEntity(applyEntity, MessageCategory.SCHEDULE_RETREAT, null).build());
                    break;
                case CLDDYCPJ:       // 车辆调度-添加评价接口
                    SyncEvaluateInfo evaluateInfo = objectMapper.readValue(syncData, SyncEvaluateInfo.class);
                    TBusEvaluateinfoEntity evaluateInfoEntity = applicationRepository.evaluateApplication(null, evaluateInfo.getApplyNo(), SyncEntityConvert.toEntity(evaluateInfo), userPrincipal);
                    if(evaluateInfoEntity == null) {
                        return false;
                    }
                    shareTomeEventEvent.fire(messageCreator.sendPostMessageToSnsByEvaluateEntity(evaluateInfoEntity, MessageCategory.APPLICATION_RATE_DRIVER).build());
                    break;
                case PCZF:           // 派车作废(作废驾驶员和车辆)  PCZF#派车单id#取消类型#取消原因
                    TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCar(syncData.split("#")[0], userPrincipal);
                    if(scheduleCarEntity == null || scheduleCarEntity.getPostId4Driver() == null) {
                        return false;
                    }
                    shareTomeEventEvent.fire(messageCreator.getBuilderForDriver(scheduleCarEntity.getSchedule(), scheduleCarEntity, MessageCategory.SCHEDULE_CHANGE_DELETE_SCHEDULE_CAR).build());
                    for (TBusApplyinfoEntity applyinfoEntity : scheduleCarEntity.getSchedule().getApplications()) {
                        shareTomeEventEvent.fire(messageCreator.getBuilderForApply(applyinfoEntity, MessageCategory.APPLICATION_DISPATCH_UPDATE).build());
                    }
                    shareTomeEventEvent.fire(messageCreator.getBuilderForSchedule(scheduleCarEntity.getSchedule(), MessageCategory.APPLICATION_DISPATCH_UPDATE).build());
                    break;
                default:
                    return false;
            }
            result = true;
            Thread.sleep(500);
            return result;
        } catch (Exception e) {
            return false;
        } finally {
            if(result) {
                entity.setSendMessage(result);
                em.flush();
            }
        }
    }

    // 获取当前时间 从内部到外部 && 未被删除 && 状态是成功 && 尝试次数小于maxTimes && 插入时间是指定区间的数据
    @TransactionAttribute(REQUIRES_NEW)
    public List<TSyncDataStatusEntity> listInternalSyncDataStatus(DateTime startTime, DateTime endTime, int maxTimes, SyncDataType syncDataType, PrincipalExt principalExt) {
        if (startTime == null) {
            startTime = DateTimeUtil.appStartTime;
        }
        if (endTime == null) {
            endTime = DateTimeUtil.appEndTime;
        }

        int maxSize = 50;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSyncDataStatusEntity> cq = cb.createQuery(TSyncDataStatusEntity.class);
        Root<TSyncDataStatusEntity> syncDataStatus = cq.from(TSyncDataStatusEntity.class);
        Predicate predicate = cb.and(
                cb.equal(syncDataStatus.get(TSyncDataStatusEntity_.toOut), true),
                cb.between(syncDataStatus.get(TSyncDataStatusEntity_.lastFetchTime), new Timestamp(startTime.getMillis()), new Timestamp(endTime.getMillis()))
        );
        if (!SyncDataType.OUTPUT_ALL.equals(syncDataType)) {
            predicate = cb.and(
                    predicate,
                    cb.equal(syncDataStatus.get(TSyncDataStatusEntity_.dataType), syncDataType)
            );
            maxSize = 10;
        }
        if (maxTimes > 0) {
            predicate = cb.and(
                    predicate,
                    cb.equal(syncDataStatus.get(TSyncDataStatusEntity_.deleteFlg), false),
                    cb.lt(syncDataStatus.get(TSyncDataStatusEntity_.pingTimes), maxTimes)
            );
        } else {
            predicate = cb.and(
                    predicate,
                    cb.ge(syncDataStatus.get(TSyncDataStatusEntity_.pingTimes), 0 - maxTimes)
            );
        }
        cq.where(predicate);
        cq.orderBy(cb.asc(syncDataStatus.get(TSyncDataStatusEntity_.insertdate)));

        List<TSyncDataStatusEntity> resultList = em.createQuery(cq).setMaxResults(maxSize).getResultList();
        for (TSyncDataStatusEntity entity : resultList) {
            if (!entity.isDeleteFlg() || maxTimes == entity.getPingTimes() || (0 - maxTimes) == entity.getPingTimes()) {
                entity.setDeleteFlg(true);
            }
            entity.addPingTimes();
        }
        return resultList;
    }


    // 车队信息
    @Inject
    private FleetRepository fleetRepository;
    // 车辆信息
    @Inject
    private VehicleRepository vehicleRepository;
    // 驾驶员信息
    @Inject
    private DriverRepository driverRepository;
    // 申请人
    @Inject
    private UserRepository userRepository;
    // 申请人
    @Inject
    private RoleRepository roleRepository;
    // 用车人
    @Inject
    private PassengerRepository passengerRepository;
    // 申请单
    @Inject
    private ApplicationRepository applicationRepository;
    // 调度单
    @Inject
    private ScheduleRepository scheduleRepository;
    // 调度单
    @Inject
    private BusBusinessRelationRepository busBusinessRelationRepository;
    @Inject
    private AlarmRepository alarmRepository;
    @Inject
    private Event<ShareTomeEvent> shareTomeEventEvent;
    @Inject
    private MessageCreator messageCreator;

    private static ObjectMapper objectMapper = new ObjectMapper();


    private String[] repairWithJsonDataArgs(String data) {
        if (data.indexOf("#") < 0) {
            return new String[0];
        }
        String[] args = new String[2];
        args[0] = data.substring(0, data.indexOf("#"));
        args[1] = data.substring(data.indexOf("#") + 1, data.length());
        return args;
    }

//    @TransactionAttribute(REQUIRES_NEW)
    public Response businessDispatcher(String uuid, String data, PrincipalExt userPrincipal) {
        String[] args = data.split("#");

        // 处理结果(默认为true:正常结束)
        boolean result = true;
        String message = null;
        TSyncDataStatusEntity syncDataStatusEntity;

        syncDataStatusEntity = findOneById(uuid, userPrincipal);
        // 如果数据同步成功
        if (syncDataStatusEntity != null && "1".equals(syncDataStatusEntity.getState())) {
            Map<String, Boolean> successResult = new HashMap<>();
            successResult.put("result", true);
            return Response.ok(successResult).build();
        }
        int flowStep = 0;
        boolean sendMsg = false;
        try {
            RequestType postType = RequestType.valueOf(args[0]);
            switch (postType) {
                // 车队信息变更
                case BMBG:      // 车队信息变更
                    return updateFleet(args, userPrincipal);
                case BMXZ:      // 车队信息新增
                    return insertFleet(args, userPrincipal);
                case BMSC:      // 车队信息删除
                    return deleteFleet(args, userPrincipal);

                // 车辆信息变更
                case CLBG:      // 车辆信息变更
                    return updateCarInfo(args, userPrincipal);
                case CLBGWBJJ:      // 车辆交接 从外部移交进来
                    return insertCarInfo(repairWithJsonDataArgs(data), userPrincipal);

                // 人员信息变更
                case RYXZ:      // 驾驶员新增
                    return insertDriverInfo(args, userPrincipal);
                case RYBG:      // 驾驶员变更信息
                    return updateDriverInfo(args, userPrincipal);
                case RYSC:      // 驾驶员信息删除
                    return deleteDriver(args, userPrincipal);

                // 车辆维修
                case CLWX:      // 车辆维修(报修)
                    return updateCarRepairing(args, userPrincipal);
                case CLWXQX:      // 进厂取消
                    return cancelCarRepairing(args, userPrincipal);

                // 车辆违章信息
                case CLWZ:      // 车辆违章信息
                    return updateViolation(args, userPrincipal);
                case RYQJ:     // 驾驶员请假状态
                    return updateLeaveInfo(args, userPrincipal);

                case LXGG:     // 用车联系人更改
                    return updateMainUser(args, userPrincipal);
                case LXXZ:      // 用户信息修改
                    return insertMainUser(args, userPrincipal);
                case LXSC:      // 用车联系人删除
                    return deleteMainUser(args, userPrincipal);

                case XZYH:      // 用户信息新增
                    return insertSystemUser(args, userPrincipal);
                case UIGG:      // 用户信息修改
                    return updateSystemUser(args, userPrincipal);
                case YHSC:   // 删除用户
                    return deleteSystemUser(args, userPrincipal);
                case JSBG:      // 角色更新
                    return updateUserRole(args, userPrincipal);

                // 角色管理
                case JSXZ:      // 新增角色
                    return insertRole(args, userPrincipal);
                case JSXG:      // 修改角色
                    return updateRole(args, userPrincipal);
                case JSSC:      // 删除角色
                    return deleteRole(args, userPrincipal);
                case JSSQ:      // 角色授权     TODO  无用
                    return grantRole(repairWithJsonDataArgs(data), userPrincipal);

                // 车辆调度模块
                case CLDDTJSQ:              // 车辆调度-提交调度申请                    OK    OK
                    flowStep = 100;
                    return updateApplyInfo(repairWithJsonDataArgs(data), userPrincipal);
                case CLDDSQXG:              // 车辆调度-修改申请
                    flowStep = 101;
                    return updateApplyInfo(repairWithJsonDataArgs(data), userPrincipal);
                case CLDDYCSH:              // 车辆调度-车辆调度用车审核                NG  OK
                    flowStep = 200;
                    return approveApply(repairWithJsonDataArgs(data), userPrincipal);
                case CLDDAPPLYCANCEL:     // 车辆调度 多个申请单时 取消申请            OK OK
                    flowStep = 201;
                    return cancelApply(args, userPrincipal);
                case CLDDSQSC:     // 车辆调度-申请删除                                 NG  OK
                    flowStep = 202;
                    return deleteApply(args, userPrincipal);
                case CLDDZDXT:             // 车辆调度-车辆调度总调协调（提交给总调）   NG NG  TODO
                    flowStep = 900;
                    return devolveApply(args, userPrincipal);
                case CLDDXTTH:   // 车辆调度-协调退回                                   NG  NULL
                    flowStep = 901;
                    return retreatDevolveApply(args, userPrincipal);
                case CLDDDDXT:   // 车辆调度-车辆调度调度协调（变更车队）               NG    NULL
                    flowStep = 902;
                    return concertApply(args, userPrincipal);
                case CLDD:      // 车辆调度                                             OK  OK
                    flowStep = 300;
                    return dispatchApplys(repairWithJsonDataArgs(data), userPrincipal);
                case CLDDCAR:   // 车辆调度 更改车辆                                    NG  OK
                    flowStep = 301;
                    sendMsg = true;
                    return updateScheduleCar(args, userPrincipal);
                case CLDDJSY:   // 车辆调度 更改驾驶员                                  NG   OK
                    flowStep = 302;
                    sendMsg = true;
                    return updateScheduleDriver(args, userPrincipal);
                case DDQX:   // 车辆调度取消                                            NG
                    flowStep = 303;
                    sendMsg = true;
                    return cancelSchedule(args, userPrincipal);
                case DDZF:    // 车辆调度作废                                           NG
                    flowStep = 400;
                    sendMsg = true;
                    return deleteSchedule(args, userPrincipal);
                case CLDDDDTH:   // 车辆调度-调度退回                                  NG
                    flowStep = 401;
                    return retreatApply(args, userPrincipal);
                case CLDDDGQRFH:   // 车辆调度任务完成 确认返回                        OK
                    flowStep = 500;
                    return finishSchedule(args, userPrincipal);
                case CLDDXCJL:   // 车辆调度-行车记录                                  OK
                    flowStep = 600;
                    return updateScheduleCarRecord(args, userPrincipal);
                case CLDDYCPJ:   // 添加评价接口                                       OK
                    flowStep = 700;
                    return evaluateApply(repairWithJsonDataArgs(data), userPrincipal);
                case PCZF:          // 派车作废(作废驾驶员和车辆)                      NG
                    flowStep = 400;
                    return cancelScheduleCar(args, userPrincipal);

                case TDTYDDSZ:      // 车队统一调度设置
                    return setUniteDispatch(args, userPrincipal);
                case YWGXDDSZ:      // 业务关系同意调度设置
                    return setBusinessRelation(args, userPrincipal);
                case YWDWSZLX:      // 业务单位设置联系
                    return updateBusinessRelation(args, userPrincipal);

                case GPS:   // GPS安装情况更新
                    return updateGpsInfo(args, userPrincipal);
            }
        } catch (Exception e) { // 处理失败 抛出异常
            message = e.getMessage();
            if (message == null) {
                if (e.getStackTrace() != null && e.getStackTrace().length > 0) {
                    message = e.getStackTrace()[0].toString();
                }
            }
            e.printStackTrace();
            result = false;
            logger.error(data);
            return Response.status(Response.Status.BAD_REQUEST).entity(message.substring(0, message.length() > 300 ? 300 : message.length())).build();
        } finally {
            if (syncDataStatusEntity != null) {
                syncDataStatusEntity.setFlowStep(flowStep);
                if(sendMsg && message == null)
                    syncDataStatusEntity.setSendMessage(true);
                updateSyncDataStatus(uuid, result ? "1" : "0", message, userPrincipal);
            } else {
                syncDataStatusEntity = new TSyncDataStatusEntity();
                syncDataStatusEntity.setUuid(uuid);
                syncDataStatusEntity.setFlowStep(flowStep);
                if(sendMsg && message == null)
                    syncDataStatusEntity.setSendMessage(true);
                syncDataStatusEntity.setDataValue(data);
                syncDataStatusEntity.setToOut(false);
                syncDataStatusEntity.setExceptionMessage(message);
                syncDataStatusEntity.setInsertdate(new Timestamp(DateTime.now().getMillis()));
                syncDataStatusEntity.setUpdatedate(new Timestamp(DateTime.now().getMillis()));
                syncDataStatusEntity.setState(result ? "1" : "0");
                saveSyncDataStatus(syncDataStatusEntity, userPrincipal);
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // 车辆调度 更改车辆
    public Response updateScheduleCar(String[] args, PrincipalExt userPrincipal) {
        // CLDDCAR# schedule_car的id#车辆的id   更改车辆
        if (args.length == 3 && StringUtils.isNotBlank(args[1]) && StringUtils.isNotBlank(args[2])) {
            return updateScheduleCar(args[1], args[2], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度 更改车辆
     *
     * @param scheduleCarId 调度单车辆ID
     * @return
     */
    public Response updateScheduleCar(String scheduleCarId, String carId, PrincipalExt userPrincipal) {
        TAzCarinfoEntity carinfoEntity = vehicleRepository.getCarById(carId);
        if (carinfoEntity == null) {
            throw new EntityNotFoundException("Car is not exist!");
        }
        TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCar(scheduleCarId, userPrincipal);
        if (scheduleCarEntity == null) {
            throw new EntityNotFoundException("Schedule is not exist!");
        }
        TAzCarinfoEntity oldCar = scheduleCarEntity.getCar();
        scheduleCarEntity.setCar(carinfoEntity);
        TBusScheduleCarEntity newScheduleCarEntity = scheduleRepository.updateScheduleCar4Sync(scheduleCarId, scheduleCarEntity, userPrincipal);
        for(TBusScheduleCarEntity carEntity : newScheduleCarEntity.getSchedule().getScheduleCars()){
            if(carEntity.getUuid().equals(newScheduleCarEntity.getUuid())) {
                carEntity.setCar(carinfoEntity);
            }
        }
        scheduleCarEntity.setCar(oldCar);
        List<Builder> builderList = messageCreator.sendPostMessageToSnsByMergeScheduleEntity(scheduleCarEntity.getSchedule(), newScheduleCarEntity.getSchedule());
        for (Builder builder : builderList) {
            shareTomeEventEvent.fire(builder.build());
        }

        return Response.ok().build();
    }

    // 车辆调度 更改驾驶员
    public Response updateScheduleDriver(String[] args, PrincipalExt userPrincipal) {
        // CLDDJSY# schedule_car的id#驾驶员的id  更改驾驶员
        if (args.length == 3 && StringUtils.isNotBlank(args[1]) && StringUtils.isNotBlank(args[2])) {
            return updateScheduleDriver(args[1], args[2], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度 更改驾驶员
     *
     * @param scheduleCarId 调度单车辆ID
     * @return
     */
    public Response updateScheduleDriver(String scheduleCarId, String driverId, PrincipalExt userPrincipal) {
        TRsDriverinfoEntity driverinfoEntity = driverRepository.loadDriverById(driverId, userPrincipal);
        if (driverinfoEntity == null) {
            throw new EntityNotFoundException("Driver is not exist!");
        }
        TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCar(scheduleCarId, userPrincipal);
        if (scheduleCarEntity == null) {
            throw new EntityNotFoundException("Schedule is not exist!");
        }
        TRsDriverinfoEntity oldDriver = scheduleCarEntity.getDriver();
        scheduleCarEntity.setDriver(driverinfoEntity);
        TBusScheduleCarEntity newScheduleCarEntity = scheduleRepository.updateScheduleCar4Sync(scheduleCarId, scheduleCarEntity, userPrincipal);
        for(TBusScheduleCarEntity carEntity : newScheduleCarEntity.getSchedule().getScheduleCars()){
            if(carEntity.getUuid().equals(newScheduleCarEntity.getUuid())) {
                carEntity.setDriver(driverinfoEntity);
            }
        }
        scheduleCarEntity.setDriver(oldDriver);
        List<Builder> builderList = messageCreator.sendPostMessageToSnsByMergeScheduleEntity(scheduleCarEntity.getSchedule(), newScheduleCarEntity.getSchedule());
        for (Builder builder : builderList) {
            shareTomeEventEvent.fire(builder.build());
        }

        return Response.ok(driverId).build();
    }

    // 车辆调度-车辆调度总调协调（提交给总调）
    public Response devolveApply(String[] args, PrincipalExt userPrincipal) {
        // CLDDZDXT#applyNo #isSend#status#原因
        if (args.length == 5) {
            if (StringUtils.isNotBlank(args[1]) && StringUtils.isNotBlank(args[2]) && StringUtils.isNotBlank(args[3])) {
                return devolveApply(args[1], args[2], args[3], args[4], userPrincipal);
            }
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度-车辆调度总调协调（提交给总调）
     *
     * @param applyNo 申请单
     * @param isSend  是否为总调
     * @param status  状态
     * @param reason  原因
     * @return
     */
    public Response devolveApply(String applyNo, String isSend, String status, String reason, PrincipalExt userPrincipal) {
        TBusApplyinfoEntity applyEntity = applicationRepository.devolveApply(null, applyNo, isSend, status, reason, true, userPrincipal);
        List<TSysUserEntity> userEntityList = userRepository.getOverallDispatcher();
        shareTomeEventEvent.fire(messageCreator.addNewMember(applyEntity, userEntityList, MessageCategory.ADD_POST_MEMBER).build());
        return Response.ok(applyNo + status).build();
    }

    // 车辆调度-协调退回
    public Response retreatDevolveApply(String[] args, PrincipalExt userPrincipal) {
        // CLDDXTTH#applyId
        if (args.length == 2) {
            if (StringUtils.isNotBlank(args[1])) {
                return retreatDevolveApply(args[1], userPrincipal);
            }
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度-协调退回
     *
     * @param applyId 申请单
     * @return
     */
    public Response retreatDevolveApply(String applyId, PrincipalExt userPrincipal) {
        applicationRepository.retreatDevolveApply(applyId, null, true, userPrincipal);
        return Response.ok(applyId).build();
    }

    // 车辆调度-车辆调度调度协调（变更车队）
    public Response concertApply(String[] args, PrincipalExt userPrincipal) {
        // CLDDDDXT#applyInfo的id#sendUser的id#sscd的id#isSend
        if (args.length == 4) {
            if (StringUtils.isNotBlank(args[1]) && StringUtils.isNotBlank(args[2]) && StringUtils.isNotBlank(args[3])) {
                return concertApply(args[1], args[2], args[3], "0", userPrincipal);
            }
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度-车辆调度调度协调（变更车队）
     *
     * @param applyId    申请单
     * @param sendUserId 调度作废类型
     * @param sscd       所属车队
     * @param isSend     是否为总调
     * @return
     */
    public Response concertApply(String applyId, String sendUserId, String sscd, String isSend, PrincipalExt userPrincipal) {
        applicationRepository.concertApply(applyId, sendUserId, sscd, isSend, true, userPrincipal);
        return Response.ok(applyId + sscd).build();
    }

    // 车辆调度-调度退回
    public Response retreatApply(String[] args, PrincipalExt userPrincipal) {
        // CLDDDDTH#applyNo
        if (args.length == 2) {
            if (StringUtils.isNotBlank(args[1])) {
                return retreatApply(args[1], userPrincipal);
            }
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度-调度退回
     *
     * @param applyNo 申请单
     * @return
     */
    public Response retreatApply(String applyNo, PrincipalExt userPrincipal) {
        applicationRepository.retreatApply(null, applyNo, true, userPrincipal);
        return Response.ok(applyNo).build();
    }

    // 车辆调度 多个申请单时 取消申请
    public Response cancelApply(String[] args, PrincipalExt userPrincipal) {
        // CLDDAPPLYCANCEL#申请单的id#取消的类型#取消的原因  取消申请
        if (args.length == 4 && StringUtils.isNotBlank(args[1])) {
            return cancelApply(args[1], ScheduleCancelType.toEnumType(args[2]), args[3], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度-申请删除
     */
    public Response deleteApply(String[] args, PrincipalExt userPrincipal) {
        // CLDDSQSC#applyId
        if (args.length == 2 && StringUtils.isNotBlank(args[1])) {
            return deleteApply(args[1], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度-申请删除
     *
     * @param applyId 申请单
     * @return
     */
    public Response deleteApply(String applyId, PrincipalExt userPrincipal) {
        TBusApplyinfoEntity applyinfoEntity = applicationRepository.getApplicationById(applyId, userPrincipal);

        applicationRepository.deleteApply(applyId, userPrincipal);

        return Response.ok(applyId).build();
    }

    /**
     * 车辆调度 多个申请单时 取消申请
     *
     * @param applyId    申请单
     * @param cancelType 调度作废类型
     * @param reason     取消的原因
     * @return
     */
    public Response cancelApply(String applyId, ScheduleCancelType cancelType, String reason, PrincipalExt userPrincipal) {
        TBusApplyinfoEntity applyinfoEntity = applicationRepository.getApplicationById(applyId, userPrincipal);
        if (applyinfoEntity == null) {
            throw new EntityNotFoundException("Apply is not exist!");
        }
        applyinfoEntity.setStatus(ApplyStatus.APPLY_CANCEL.toStringValue());
        applyinfoEntity.setCanceltype(cancelType.getValue());
        applyinfoEntity.setCancelseason(reason);
        applyinfoEntity.setCanceltime(DateTime.now());
//        applyinfoEntity.setCanceluser(reason);
        applyinfoEntity.setSchedule(null);
        applicationRepository.cancelApplication(applyId, applyinfoEntity, userPrincipal);

        return Response.ok(applyId + reason).build();
    }

    // 车辆调度任务完成 确认返回
    public Response finishSchedule(String[] args, PrincipalExt userPrincipal) {
        // CLDDDGQRFH#开始时间的毫秒数#结束时间的毫秒数#schedule_car的id#XCJLID 确认返回
        if (args.length == 5 && StringUtils.isNotBlank(args[3]) && StringUtils.isNotBlank(args[4])) {
            SyncRecord syncRecord = new SyncRecord();
            syncRecord.setStarttime(new DateTime(Long.valueOf(args[1])));
            syncRecord.setEndtime(new DateTime(Long.valueOf(args[2])));
            syncRecord.setId(args[4]);
            return finishSchedule(args[3], syncRecord, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    // 车辆调度-行车记录
    public Response updateScheduleCarRecord(String[] args, PrincipalExt userPrincipal) {
        // CLDDXCJL#UUID#SCHEDULEID#STARTMILE#ENDMILE
        if (args.length == 5 && StringUtils.isNotBlank(args[2]) && StringUtils.isNotBlank(args[3])) {
            SyncRecord syncRecord = new SyncRecord();
            syncRecord.setId(args[1]);
            syncRecord.setSchedule(args[2]);
            syncRecord.setStartmile(Double.valueOf(args[3]));
            syncRecord.setEndmile(Double.valueOf(args[4]));
            return finishSchedule(args[2], syncRecord, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度任务完成 确认返回
     *
     * @return
     */
    public Response finishSchedule(String scheduleCarId, SyncRecord syncRecord, PrincipalExt userPrincipal) {
        TBusRecordinfoEntity recordEntity = scheduleRepository.getScheduleCarRecordByScheduleCarIdWithPermission(scheduleCarId, userPrincipal);
        TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCar(scheduleCarId, userPrincipal);
        if (scheduleCarEntity == null) {
            throw logger.entityNotFound(TBusScheduleCarEntity.class, scheduleCarId);
        }
        if (recordEntity == null) {
            recordEntity = new TBusRecordinfoEntity();
            recordEntity.setRemark("数据同步完成");
        }
        recordEntity.setUuid(syncRecord.getId());
        if(recordEntity.getStarttime() == null) {
            if (syncRecord.getStarttime() != null) {
                recordEntity.setStarttime(new DateTime(syncRecord.getStarttime().getTime()));
            } else {
                recordEntity.setStarttime(scheduleCarEntity.getSchedule().getStarttime());
            }
        }
        recordEntity.setStartmile(syncRecord.getStartmile());
        if(recordEntity.getEndtime() == null) {
            if (syncRecord.getEndtime() != null) {
                recordEntity.setEndtime(new DateTime(syncRecord.getEndtime().getTime()));
            } else {
                recordEntity.setEndtime(scheduleCarEntity.getSchedule().getEndtime());
            }
        }
        recordEntity.setEndmile(syncRecord.getEndmile());
        scheduleRepository.updateScheduleCarRecordWith(scheduleCarId, EntityConvert.fromEntity(recordEntity), userPrincipal);

        return Response.ok(scheduleCarId).build();
    }

    // 车辆调度-添加评价
    public Response evaluateApply(String[] args, PrincipalExt userPrincipal) throws Exception {
        // CLDDYCPJ#EvaluateInfo的json格式
        if (args.length == 2) {
            SyncEvaluateInfo evaluateInfo = objectMapper.readValue(args[1], SyncEvaluateInfo.class);
            if (StringUtils.isNotBlank(evaluateInfo.getApplyNo())) {
                return insert(null, evaluateInfo.getApplyNo(), evaluateInfo, userPrincipal);
            }
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度-添加评价
     *
     * @param applyId      申请单
     * @param applyNo      申请单
     * @param evaluateInfo 评价信息
     * @return
     */
    public Response insert(String applyId, String applyNo, SyncEvaluateInfo evaluateInfo, PrincipalExt userPrincipal) {
        applicationRepository.evaluateApplication(applyId, applyNo, SyncEntityConvert.toEntity(evaluateInfo), userPrincipal);
        return Response.ok(evaluateInfo).build();
    }

    // 作废 调度单 信息 调度作废
    public Response deleteSchedule(String[] args, PrincipalExt userPrincipal) {
        if (args.length == 2 && StringUtils.isNotBlank(args[1])) {
            return deleteSchedule(args[1], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 作废 调度单 信息 调度作废
     *
     * @param scheduleId 调度单ID
     * @return
     */
    public Response deleteSchedule(String scheduleId, PrincipalExt userPrincipal) {
        TBusScheduleRelaEntity scheduleRelaEntity = scheduleRepository.getScheduleById(scheduleId, userPrincipal);
        if (scheduleRelaEntity == null) {
            throw new EntityNotFoundException("Schedule is not exist!" + scheduleId);
        }
        scheduleRepository.deleteSchedule(scheduleId, userPrincipal);  // status -> 7 (调度作废)

        List<Builder> builderList = messageCreator.sendPostMessageToSnsByScheduleEntity(scheduleRelaEntity, MessageCategory.SCHEDULE_DELETE);
        for (Builder builder : builderList) {
            shareTomeEventEvent.fire(builder.build());
        }

        return Response.ok(scheduleId).build();
    }

    // 取消 调度单 信息 取消申请
    public Response cancelSchedule(String[] args, PrincipalExt userPrincipal) {
        if (args.length == 2 && StringUtils.isNotBlank(args[1])) {
            return cancelSchedule(args[1], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 取消 调度单 信息 取消调度
     *
     * @param scheduleId 调度单ID
     * @return
     */
    public Response cancelSchedule(String scheduleId, PrincipalExt userPrincipal) {
        TBusScheduleRelaEntity scheduleRelaEntity = scheduleRepository.getScheduleById(scheduleId, userPrincipal);
        if (scheduleRelaEntity == null) {
            throw new EntityNotFoundException("Schedule is not exist!");
        }
        List<TBusApplyinfoEntity> applyList = applicationRepository.getApplicationByScheduleId(scheduleId, userPrincipal);

        // status -> 3 (申请未分配状态)
        applicationRepository.deleteSchedule(scheduleId, userPrincipal);

        for (TBusApplyinfoEntity applyinfoEntity : applyList) {
            applicationRepository.updateApplicationStatus(applyinfoEntity.getUuid(), ApplyStatus.DISPATCH_CANCEL.toStringValue(), true, userPrincipal);

            shareTomeEventEvent.fire(
                    messageCreator.sendPostMessageToSnsByApplicationEntity(applyinfoEntity, null, ApplyStatus.DISPATCH_CANCEL.toStringValue()).build()
            );
        }

        return Response.ok(scheduleId).build();
    }

    public Response checkBackSyncResult(String param, PrincipalExt userPrincipal) {
        if (StringUtils.isBlank(param)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            String[] args = param.split("!!!");
            if (args.length != 2) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            String uuid = args[0];
            String status = args[1].substring(0, 1);    // "1"成功 : "0" 失败
            TSyncDataStatusEntity syncDataStatusEntity = findOneById(uuid, userPrincipal);
            if (syncDataStatusEntity == null) {
                throw new EntityNotFoundException("Sync Data Entity not found");
            }
            updateSyncDataStatus(uuid, status, null, userPrincipal);
        }
        return Response.ok(param).build();
    }

    private static DateTime lastQueryStartTime = null;
    private static int maxFetchTime = 1;

    /**
     * 获得 数据同步结果
     *
     * @param userPrincipal
     * @return
     */
    public Response checkSyncResult(PrincipalExt userPrincipal) {
        Map<String, Object> dataResult = new HashMap<>();
        SyncInterval queryTime = getSyncQueryIntervalTime();
        try {
            List<TSyncDataStatusEntity> allDataList = listInternalSyncDataStatus(queryTime.getStartTime(), queryTime.getEndTime(), maxFetchTime, SyncDataType.OUTPUT_ALL, userPrincipal);
            List<TSyncDataStatusEntity> applyList = new ArrayList<>();
            List<TSyncDataStatusEntity> canceledApplyIdList = new ArrayList<>();
            List<TSyncDataStatusEntity> approveList = new ArrayList<>();
            List<TSyncDataStatusEntity> syncScheduleList = new ArrayList<>();
            List<TSyncDataStatusEntity> syncEvaluateList = new ArrayList<>();
            List<TSyncDataStatusEntity> syncEvaluateApplyList = new ArrayList<>();
            List<TSyncDataStatusEntity> recordList = new ArrayList<>();
            List<TSyncDataStatusEntity> rejectedApplyIdList = new ArrayList<>();
            List<TSyncDataStatusEntity> deletedApplyIdList = new ArrayList<>();
            List<TSyncDataStatusEntity> addedApplyIdList = new ArrayList<>();
            List<TSyncDataStatusEntity> deleteScheduleIdList = new ArrayList<>();
            List<TSyncDataStatusEntity> updatedScheduleCarList = new ArrayList<>();
            List<TSyncDataStatusEntity> deleteScheduleCarList = new ArrayList<>();

            for(TSyncDataStatusEntity dataStatusEntity : allDataList) {
                switch (dataStatusEntity.getDataType()) {
                    case APPLY:
                        applyList.add(dataStatusEntity);
                        break;
                    case APPLY_CANCELED:
                        canceledApplyIdList.add(dataStatusEntity);
                        break;
                    case APPROVE:
                        approveList.add(dataStatusEntity);
                        break;
                    case SCHEDULE:
                        syncScheduleList.add(dataStatusEntity);
                        break;
                    case EVALUATE:
                        syncEvaluateList.add(dataStatusEntity);
                        break;
                    case EVALUATE_APPLY:
                        syncEvaluateApplyList.add(dataStatusEntity);
                        break;
                    case RECORD:
                        recordList.add(dataStatusEntity);
                        break;
                    case REJECTED:
                        rejectedApplyIdList.add(dataStatusEntity);
                        break;
                    case APPLY_DELETE:
                        deletedApplyIdList.add(dataStatusEntity);
                        break;
                    case SCHEDULE_ADD_APPLY:
                        addedApplyIdList.add(dataStatusEntity);
                        break;
                    case DELETED:
                        deleteScheduleIdList.add(dataStatusEntity);
                        break;
                    case UPDATED:
                        updatedScheduleCarList.add(dataStatusEntity);
                        break;
                    case DELETE_CAR:
                        deleteScheduleCarList.add(dataStatusEntity);
                        break;
                }
            }
            // 查询 新生成的 用车申请 信息
            dataResult.put("result_apply", SyncEntityConvert.fromDataEntity(applyList));
            // 查询 用车申请取消  result_cancel_apply [APPLY_CANCEL#applyId]
            dataResult.put("result_cancel_apply", SyncEntityConvert.fromDataEntity(canceledApplyIdList));
            // 查询 审核 信息
            dataResult.put("result_approve", SyncEntityConvert.fromDataEntity(approveList));
            // 查询 新生成的 用车调度 信息
            dataResult.put("result_schedule", SyncEntityConvert.fromDataEntity(syncScheduleList));
            // 查询 评价 信息
            dataResult.put("result_evaluate", SyncEntityConvert.fromDataEntity(syncEvaluateList));
            dataResult.put("result_evaluate_apply", SyncEntityConvert.fromDataEntity(syncEvaluateApplyList));
            // 查询 行车记录 信息
            dataResult.put("result_record", SyncEntityConvert.fromDataEntity(recordList));
            // 查询 拒绝派单 用车申请  result_reject_apply : [APPLY_REJECT#applyId#userId]
            dataResult.put("result_reject_apply", SyncEntityConvert.fromDataEntity(rejectedApplyIdList));
            // 查询 撤销的 用车申请  result_delete_apply : [APPLY_DELETE#applyId#cancelType#cancelReason#userId]
            dataResult.put("result_delete_apply", SyncEntityConvert.fromDataEntity(deletedApplyIdList));
            // 查询 撤销的 用车申请  result_schedule_add_apply : [SCHEDULE_ADD_APPLY#scheduleId#applyId]
            dataResult.put("result_schedule_add_apply", SyncEntityConvert.fromDataEntity(addedApplyIdList));
            // 查询 撤销的 调度单 result_delete_schedule : [SCHEDULE_DELETE#scheduleId#cancelType#cancelReason#userId]
            dataResult.put("result_delete_schedule", SyncEntityConvert.fromDataEntity(deleteScheduleIdList));
            // 查询 更换的车辆  result_schedule_car_driver : [SCHEDULE_CAR#scheduleCarId#newCarid#newDriverId#userId]
            dataResult.put("result_schedule_car_driver", SyncEntityConvert.fromDataEntity(updatedScheduleCarList));
            // 查询 删除调度车辆   result_delete_schedule_car : [SCHEDULE_DELETE_CAR#scheduleCarId#userId]
            dataResult.put("result_delete_schedule_car", SyncEntityConvert.fromDataEntity(deleteScheduleCarList));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.ok(dataResult).build();
    }

    // 获得查询时间区间  当前时间 -2 小时 -> 当前时间
    private SyncInterval getSyncQueryIntervalTime() {
        SyncInterval queryInterval = new SyncInterval();
        // 反向同步数据  提前 2 小时
        DateTime queryTime = DateTime.now();
        DateTime startTime;
        if (lastQueryStartTime == null) {
            lastQueryStartTime = queryTime;
            startTime = DateTimeUtil.appStartTime;
        } else {
            startTime = queryTime.minusHours(2);
        }

        queryInterval.setStartTime(startTime);
        queryInterval.setEndTime(queryTime);
        return queryInterval;
    }

    private Set<String> getLocalSyncDataStatus(DateTime startTime, DateTime endTime, int maxFetchTime, SyncDataType syncDataType, PrincipalExt userPrincipal) {
        // 已经发过的发送帧 默认 发送一次
        List<TSyncDataStatusEntity> sendDataList = listInternalSyncDataStatus(startTime.minusHours(1), endTime, maxFetchTime, syncDataType, userPrincipal);
        Set<String> excludeUUID = new HashSet<>();
        for (TSyncDataStatusEntity sendData : sendDataList) {
            excludeUUID.add(sendData.getUuid());
        }
        return excludeUUID;
    }

    private List<TBusApplyinfoEntity> getApplyInfoByStatus(DateTime startTime, DateTime endTime, String status, Boolean isSend, int maxFetchTime, SyncDataType syncDataType, PrincipalExt userPrincipal) {
        Set<String> haveSendIdList = getLocalSyncDataStatus(startTime, endTime, 0 - maxFetchTime, syncDataType, userPrincipal);

        List<TBusApplyinfoEntity> applyInfoList = applicationRepository.getApplicationForSync(startTime, endTime, status, isSend, userPrincipal);
        // 申请信息
        for (TBusApplyinfoEntity syncApply : new ArrayList<>(applyInfoList)) {
            String syncStatusUUID = syncApply.getUuid() + "-" + status;
            if (syncApply.getSenduser() != null) {
                syncStatusUUID += syncApply.getSenduser().getUserid();
            }
            // 已经包含
            if (haveSendIdList.contains(syncStatusUUID)) {
                applyInfoList.remove(syncApply);
                continue;
            }
            updateSyncDataStatus(syncApply.getUuid() + "-" + status, syncDataType, syncApply, userPrincipal);
        }
        return applyInfoList;
    }

    private List<TBusApproveSugEntity> getApproveInfo(DateTime startTime, DateTime endTime, int maxFetchTime, SyncDataType syncDataType, PrincipalExt userPrincipal) {
        Set<String> haveSendIdList = getLocalSyncDataStatus(startTime, endTime, 0 - maxFetchTime, syncDataType, userPrincipal);

        List<TBusApproveSugEntity> approveList = applicationRepository.listApplyApproveInfo(startTime, endTime, userPrincipal);
        for (TBusApproveSugEntity syncApprove : new ArrayList<>(approveList)) {
            String syncStatusUUID = syncApprove.getUuid();
            // 已经包含
            if (haveSendIdList.contains(syncStatusUUID)) {
                approveList.remove(syncApprove);
                continue;
            }
            updateSyncDataStatus(syncApprove.getUuid(), syncDataType, syncApprove, userPrincipal);
        }
        return approveList;
    }

    private List<TBusScheduleRelaEntity> getScheduleInfo(DateTime startTime, DateTime endTime, int maxFetchTime, SyncDataType syncDataType, PrincipalExt userPrincipal) {
        Set<String> haveSendIdList = getLocalSyncDataStatus(startTime, endTime, 0 - maxFetchTime, syncDataType, userPrincipal);

        List<TBusScheduleRelaEntity> scheduleList = scheduleRepository.getAllSchedulesForSync(startTime, endTime, userPrincipal);
        // 调度信息
        for (TBusScheduleRelaEntity scheduleEntity : new ArrayList<>(scheduleList)) {
            String syncStatusUUID = scheduleEntity.getUuid();
            // 已经包含
            if (haveSendIdList.contains(syncStatusUUID)) {
                scheduleList.remove(scheduleEntity);
                continue;
            }
            updateSyncDataStatus(syncStatusUUID, syncDataType, scheduleEntity, userPrincipal);
        }
        return scheduleList;
    }

    private List<TBusEvaluateinfoEntity> getEvaluateInfo(DateTime startTime, DateTime endTime, int maxFetchTime, SyncDataType syncDataType, PrincipalExt userPrincipal) {
        Set<String> haveSendIdList = getLocalSyncDataStatus(startTime, endTime, 0 - maxFetchTime, syncDataType, userPrincipal);

        List<TBusEvaluateinfoEntity> applyEvaluateInfoList = scheduleRepository.getApplyEvaluateInfoListByTime(startTime, endTime, userPrincipal);
        for (TBusEvaluateinfoEntity evaluateEntity : new ArrayList<>(applyEvaluateInfoList)) {
            String syncStatusUUID = evaluateEntity.getUuid() + evaluateEntity.getEvaldate().getMillis();
            // 已经包含
            if (haveSendIdList.contains(syncStatusUUID)) {
                applyEvaluateInfoList.remove(evaluateEntity);
                continue;
            }
            updateSyncDataStatus(syncStatusUUID, syncDataType, evaluateEntity, userPrincipal);
        }
        return applyEvaluateInfoList;
    }

    private List<TBusRecordinfoEntity> getRecord(DateTime startTime, DateTime endTime, int maxFetchTime, SyncDataType syncDataType, PrincipalExt userPrincipal) {
        Set<String> haveSendIdList = getLocalSyncDataStatus(startTime, endTime, 0 - maxFetchTime, syncDataType, userPrincipal);

        List<TBusRecordinfoEntity> scheduleRecordInfoList = scheduleRepository.getAllScheduleCarRecordByTime(startTime, endTime, userPrincipal);
        if (scheduleRecordInfoList != null && scheduleRecordInfoList.size() > 0) {
            for (TBusRecordinfoEntity recordEntity : new ArrayList<>(scheduleRecordInfoList)) {
                String syncStatusUUID = recordEntity.getUuid();
                // 已经包含
                if (haveSendIdList.contains(syncStatusUUID)) {
                    scheduleRecordInfoList.remove(recordEntity);
                    continue;
                }
                updateSyncDataStatus(syncStatusUUID, syncDataType, recordEntity, userPrincipal);
            }
        }
        return scheduleRecordInfoList;
    }

    private void updateSyncDataStatus(String syncDataUUID, SyncDataType syncDataType, Object syncData, PrincipalExt userPrincipal) {
        TSyncDataStatusEntity syncDataStatusEntity = findOneById(syncDataUUID, userPrincipal);
        if (syncDataStatusEntity == null) {
            String data;
            String exception = null;
            try {
                data = objectMapper.writeValueAsString(syncData);
            } catch (Exception e) {
                data = syncDataUUID;
                exception = e.toString();
            }
            System.out.println(data);
            // 保存发送帧
            saveLocalSyncDataStatus(syncDataUUID, syncDataType, data, exception, userPrincipal);
        } else {
            updateSyncDataStatus(syncDataUUID, "1", null, userPrincipal);
        }
    }

    // 更新车队信息
    public Response updateFleet(String[] args, PrincipalExt userPrincipal) {
        // BMBG#车队GUID#车队名称#上级机构GUID
        if (args.length == 4 && StringUtils.isNotBlank(args[1])) {
            SyncFleet fleet = new SyncFleet();
            fleet.setId(args[1]);
            fleet.setName(args[2]);
            fleet.setParentid(args[3]);
            return update(args[1], fleet, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新车队信息
     *
     * @param orgId 车队id
     * @param fleet 车队信息
     * @return
     */
    public Response update(String orgId, SyncFleet fleet, PrincipalExt userPrincipal) {
        fleetRepository.updateFleetWithPermission(orgId, SyncEntityConvert.toEntity(fleet),
                userPrincipal);
        return Response.ok(fleet).build();
    }

    // 新增车队信息
    public Response insertFleet(String[] args, PrincipalExt userPrincipal) {
        // BMXZ#车队的guid#车队部门的类型#所属大区id#车队名字#父类id#启用状态#ddtj#排序No#备注
        if (args.length == 10) {
            SyncFleet fleet = new SyncFleet();
            fleet.setId(args[1]);
            fleet.setOrgType(args[2]);
            fleet.setGsid(args[3]);
            fleet.setName(args[4]);
            fleet.setParentid("-1".equals(args[5]) ? "0" : args[5]);
            fleet.setEnable(args[6]);
            fleet.setDdtj(args[7]);
            fleet.setSortNo(args[8]);
            fleet.setRemark(args[9]);
            return insert(fleet, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 新增车队信息
     *
     * @return
     */
    public Response insert(SyncFleet fleet, PrincipalExt userPrincipal) {
        fleetRepository.createFleetWithPermission(SyncEntityConvert.toEntity(fleet), userPrincipal);
        return Response.ok(fleet).build();
    }

    // 删除车队信息
    public Response deleteFleet(String[] args, PrincipalExt userPrincipal) {
        // BMSC#车队的guid
        if (args.length == 2 && StringUtils.isNotBlank(args[1])) {
            return delete(args[1], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 删除车队信息
     *
     * @param fleetId 车队id
     * @return
     */
    public Response delete(String fleetId, PrincipalExt userPrincipal) {
        fleetRepository.deleteFleetWithPermission(fleetId, userPrincipal);
        return Response.ok("ID : " + fleetId).build();
    }

    // 更新车辆GPS信息
    public Response updateGpsInfo(String[] args, PrincipalExt userPrincipal) {
        // GPS#车辆的id#安装的情况
        if (args.length == 3 && StringUtils.isNotBlank(args[1]) && StringUtils.isNotBlank(args[2])) {
            return updateGps(args[1], args[2], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新车辆GPS信息
     *
     * @param carId  车辆ID
     * @param gpsFlg GPS 安装情况
     * @return
     */
    public Response updateGps(String carId, String gpsFlg, PrincipalExt userPrincipal) {
        vehicleRepository.updateCarWithPermission(carId, gpsFlg, userPrincipal);
        return Response.ok(carId + gpsFlg).build();
    }

    // 更新车辆信息
    public Response insertCarInfo(String[] args, PrincipalExt userPrincipal) throws Exception {
        // CLBGWBJJ#CarJJForSync的json格式
        if (args.length == 2) {
            ObjectMapper objectMapper = new ObjectMapper();
            CarJJForSync carJJForSync = objectMapper.readValue(args[1], CarJJForSync.class);
            if (StringUtils.isBlank(carJJForSync.getId())) {
                throw new InvalidParameterException("ID is null");
            }
            return insert(carJJForSync, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新车辆信息
     *
     * @param car 车辆信息
     * @return
     */
    public Response insert(CarJJForSync car, PrincipalExt userPrincipal) {
        vehicleRepository.createCarWithPermission(SyncEntityConvert.toEntity(car), userPrincipal);
        return Response.ok(car).build();
    }

    // 更新车辆信息
    public Response updateCarInfo(String[] args, PrincipalExt userPrincipal) {
        if (args.length == 5 && StringUtils.isNotBlank(args[1])) {     // 车辆信息变更(内部移交)
            // CLBG#fb28cf8d66b74a488105b544f855f846#浙F20648#内部移交#1065691
            SyncCar car = new SyncCar();
            car.setCphm(args[2]);
//                    car.setSscd(args[3]); // 内部移交 不需要 车队信息
            car.setDriver(args[4]);
//            car.setGps(args[5].equalsIgnoreCase("YES") ? "1" : "0");
            car.setInner(true);
            return update(args[1], car, userPrincipal);
        } else if (args.length == 4 && StringUtils.isNotBlank(args[1])) {
            // CLBG#fb28cf8d66b74a488105b544f855f846#浙F20648#移交到外部
            SyncCar car = new SyncCar();
            car.setCphm(args[2]);
//                    car.setSscd(args[3]); // 移交到外部  不需要 车队信息
            car.setInner(false);
            return update(args[1], car, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新车辆信息
     *
     * @param carGuid 车辆GUID
     * @param car     车辆信息
     * @return
     */
    public Response update(String carGuid, SyncCar car, PrincipalExt userPrincipal) {
        vehicleRepository.updateCarWithPermission(carGuid, SyncEntityConvert.toEntity(car), car.isInner(),
                userPrincipal);
        return Response.ok(car).build();
    }

    /**
     * 删除车辆信息
     *
     * @param carGuid 车辆GUID
     * @return
     */
    public Response deleteCar(String carGuid, PrincipalExt userPrincipal) {
        vehicleRepository.deleteCarWithPermission(carGuid,
                userPrincipal);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    // 新增驾驶员信息
    public Response insertDriverInfo(String[] args, PrincipalExt userPrincipal) {
        // RYXZ#驾驶员的id#驾驶员的名字#驾驶员的性别#驾驶员的类型#驾驶员的部门id#驾驶员的电话#驾驶员的短号#驾驶员的准驾类型#驾驶员的职务
        if (args.length == 10) {
            SyncDriver driver = new SyncDriver();
            driver.setEid(args[1]);         // 驾驶员的id
            driver.setName(args[2]);        // 驾驶员的名字
            driver.setSex(args[3]);         // 驾驶员的性别
            driver.setDriverType(args[4]);  // 驾驶员的类型
            driver.setOrgid(args[5]);       // 驾驶员的部门id
            driver.setMobile(args[6]);      // 驾驶员的电话
            driver.setPhone(args[7]);       // 驾驶员的短号
            driver.setDrivecartype(args[8]);// 驾驶员的准驾类型
            driver.setPosition(args[9]);    // 驾驶员的职务
            return insert(driver, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 新增驾驶员信息
     *
     * @param driver 驾驶员信息
     * @return
     */
    public Response insert(SyncDriver driver, PrincipalExt userPrincipal) {
        driverRepository.createDriverWithPermission(SyncEntityConvert.toEntity(driver), userPrincipal);
        return Response.ok(driver).build();
    }

    // 更新驾驶员信息
    public Response updateDriverInfo(String[] args, PrincipalExt userPrincipal) {
        // RYBG#车队GUID#徐金华#驾驶员工号#岗位#13516736682#短号#驾照类型
        if (args.length == 8 && StringUtils.isNotBlank(args[3])) {
            SyncDriver driver = new SyncDriver();
            driver.setOrgid(args[1]);
            driver.setName(args[2]);
            driver.setEid(args[3]);
            driver.setPosition(args[4]);
            driver.setMobile(args[5]);
            driver.setPhone(args[6]);
            driver.setDrivecartype(args[7]);
            return update(args[3], driver, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新驾驶员信息
     *
     * @param driverGuid 驾驶员GUID
     * @param driver     驾驶员信息
     * @return
     */
    public Response update(String driverGuid, SyncDriver driver, PrincipalExt userPrincipal) {
        driverRepository.updateDriverWithPermission(driverGuid, SyncEntityConvert.toEntity(driver),
                userPrincipal);
        return Response.ok(driver).build();
    }

    // 删除驾驶员信息
    public Response deleteDriver(String[] args, PrincipalExt userPrincipal) {
        // RYSC#驾驶员的UUID
        if (args.length == 2 && StringUtils.isNotBlank(args[1])) {
            return deleteDriver(args[1], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 删除驾驶员信息
     *
     * @param driverGuid 驾驶员GUID
     * @return
     */
    public Response deleteDriver(String driverGuid, PrincipalExt userPrincipal) {
        driverRepository.deleteDriverWithPermission(driverGuid, userPrincipal);
        return Response.ok(driverGuid).build();
    }

    // 新增用户信息
    public Response insertMainUser(String[] args, PrincipalExt userPrincipal) {
        // LXXZ#GUID#名字#手机#短号#所属班组#BMID
        if (args.length == 7) {
            SyncMainUser mainUser = new SyncMainUser();
            mainUser.setMainUserId(args[1]);  // 用车负责人ID
            mainUser.setName(args[2]);        // 用车负责人姓名
            mainUser.setMobile(args[3]);      // 用车负责人手机号
            mainUser.setPhone(args[4]);       // 用车负责人短号
            mainUser.setBelongGroup(args[5]); // 所属班组
            mainUser.setBmid(args[6]);        // 部门ID
            return create(mainUser, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 新增用户信息
     *
     * @param mainUser 用户信息
     * @return
     */
    public Response create(SyncMainUser mainUser, PrincipalExt userPrincipal) {
        if (null == passengerRepository.getMainUserWithPermission(mainUser.getMainUserId(), userPrincipal)) {
            passengerRepository.createMainUserWithPermission(SyncEntityConvert.toEntity(mainUser), userPrincipal);
        }
        return Response.ok(mainUser).build();
    }

    // 更新用户角色信息
    public Response updateUserRole(String[] args, PrincipalExt userPrincipal) {
        // JSBG#用户的id#角色id集合(" "或者 a,b,c 类型)
        if (args.length == 3 && StringUtils.isNotBlank(args[1])) {
            String[] roleIds = args[2].split(",");
            List<String> roles = Arrays.asList(roleIds);
            return update(args[1], roles, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新用户角色信息
     *
     * @param userGuid 用户GUID
     * @param roles    用户角色信息
     * @return
     */
    public Response update(String userGuid, List<String> roles, PrincipalExt userPrincipal) {
        userRepository.updateUserRolesWithPermission(userGuid, roles, userPrincipal);
        return Response.ok(userGuid).build();
    }

    // 新增用户信息
    public Response insertSystemUser(String[] args, PrincipalExt userPrincipal) {
        // XZYH#用户id#用户编号#用户名#部门id#性别#用户类型#电话#短号#邮箱#是否启用#备注#密码
        if (args.length == 13) {
            SyncUser user = new SyncUser();
            user.setId(args[1]);
            user.setUserNo(args[2]);
            user.setName(args[3]);
            user.setDeptid(args[4]);
            user.setSex(args[5]);
            user.setUserType(args[6]);
            user.setMobile(args[7]);
            user.setPhone(args[8]);
            user.setEmail(args[9]);
            user.setEnabled(args[10]);
            user.setRemark(args[11]);
            user.setPassword(args[12]);
            return insert(user, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return
     */
    public Response insert(SyncUser user, PrincipalExt userPrincipal) {
        TSysUserEntity userEntity = SyncEntityConvert.toEntity(user);
        userRepository.createUserWithPermission(userEntity, userPrincipal);

        ShareTomeEvent shareTomeEvent = builder().addRecipient(EntityConvert.fromEntity(userEntity)).setContent(new DispatcherPostContent()).setMessageCategory(MessageCategory.ADD_NEW_MEMBER).build();
        shareTomeEventEvent.fire(shareTomeEvent);

        return Response.ok(user).build();
    }

    // 更新用户信息
    public Response updateSystemUser(String[] args, PrincipalExt userPrincipal) {
        // UIGG#00181652#钱伟杰#00206#13738288320
        if (args.length == 5 && StringUtils.isNotBlank(args[1])) {
            SyncUser user = new SyncUser();
            user.setName(args[2]);
            user.setDeptid(args[3]);
            user.setMobile(args[4]);
            return update(args[1], user, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新用户信息
     *
     * @param userGuid 用户GUID
     * @param user     用户信息
     * @return
     */
    public Response update(String userGuid, SyncUser user, PrincipalExt userPrincipal) {
        userRepository.updateUserWithPermission(userGuid, SyncEntityConvert.toEntity(user), userPrincipal);
        return Response.ok(user).build();
    }

    // 删除用户信息
    public Response deleteSystemUser(String[] args, PrincipalExt userPrincipal) {
        // YHSC#用户的id
        if (args.length == 2 && StringUtils.isNotBlank(args[1])) {
            return deleteUser(args[1], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 删除用户信息
     *
     * @param userGuid 用户GUID
     * @return
     */
    public Response deleteUser(String userGuid, PrincipalExt userPrincipal) {
        userRepository.deleteUserWithPermission(userGuid, userPrincipal);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    // 更新 车辆维修状态
    public Response updateCarRepairing(String[] args, PrincipalExt userPrincipal) {
        // CLWX#9b1b238410424f50b5697754bb554d78#浙F37962#报修#2015-10-27 10:25:04@$
        if (args.length == 5 && StringUtils.isNotBlank(args[1])) {
            SyncCarRepairing repairing = new SyncCarRepairing();
            repairing.setCphm(args[2]);
            repairing.setState(RepairingState.valueOf(args[3]));
            repairing.setTime(DateTime.parse(args[4], SyncEntityConvert.dateTimeFormat));
            return update(args[1], repairing, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    // 更新 车辆维修状态
    public Response cancelCarRepairing(String[] args, PrincipalExt userPrincipal) {
        // CLWXQX#509be94102794edc8dd6d4aeff480ce1#浙F3E399@$
        if (args.length == 3 && StringUtils.isNotBlank(args[1])) {
            SyncCarRepairing repairing = new SyncCarRepairing();
            repairing.setCancle(true);
            repairing.setCphm(args[2]);
            return update(args[1], repairing, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新 车辆维修状态
     *
     * @param carGuid   车辆GUID
     * @param repairing 维修状态
     * @return
     */
    public Response update(String carGuid, SyncCarRepairing repairing, PrincipalExt userPrincipal) {
        TAzCarinfoEntity carInfoEntity = new TAzCarinfoEntity();
        carInfoEntity.setCphm(repairing.getCphm());
        if (!repairing.isCancle()) {
            carInfoEntity.setRepairingStateTime(repairing.getTime());
            carInfoEntity.setRepairingState(repairing.getState().id());
        }

        vehicleRepository.updateCarRepairInfoWithPermission(carGuid, carInfoEntity, userPrincipal);
        return Response.ok(repairing).build();
    }

    // 更新 违章信息
    public Response updateViolation(String[] args, PrincipalExt userPrincipal) {
        // CLWZ#9b87183d5a0b42cd94f1e9d3653b730d#浙F55586#4@$
        if (args.length == 4 && StringUtils.isNotBlank(args[1])) {
            SyncViolation violation = new SyncViolation();
            violation.setCphm(args[2]);
            violation.setTimes(Integer.valueOf(args[3]));
            return update(args[1], violation, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新 违章信息
     *
     * @param carGuid   车辆GUID
     * @param violation 违章信息
     * @return
     */
    public Response update(String carGuid, SyncViolation violation, PrincipalExt userPrincipal) {
        TAzCarinfoEntity carInfoEntity = new TAzCarinfoEntity();
        carInfoEntity.setViolationTimes(violation.getTimes());
        vehicleRepository.updateViolationInfoWithPermission(carGuid, carInfoEntity, userPrincipal);
        return Response.ok(violation).build();
    }

    // 车辆调度-提交调度申请
    public Response updateApplyInfo(String[] args, PrincipalExt userPrincipal) throws Exception {
        // CLDDTJSQ#ScheduleApplyInfoForSync 的json格式
        if (args.length == 2) {
            ScheduleApplyInfoForSync applyInfoForSync = objectMapper.readValue(args[1], ScheduleApplyInfoForSync.class);
            return insertOrUpdate(applyInfoForSync, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度-提交调度申请
     *
     * @param applyInfoForSync 申请单信息
     * @return
     */
    public Response insertOrUpdate(ScheduleApplyInfoForSync applyInfoForSync, PrincipalExt userPrincipal) {
        // 申请单信息
        TBusApplyinfoEntity applyinfoEntity = applicationRepository.getApplicationById(applyInfoForSync.getUuId(), userPrincipal);
        if (applyinfoEntity == null) {
            applyinfoEntity = EntityConvert.toEntity(SyncEntityConvert.toApplyEntity(applyInfoForSync));
            applicationRepository.createApplicationForSync(applyinfoEntity, userPrincipal);
        } else {
            applyinfoEntity = EntityConvert.toEntity(SyncEntityConvert.toApplyEntity(applyInfoForSync));
            applicationRepository.updateApplication(applyInfoForSync.getUuId(), applyinfoEntity, userPrincipal);
        }
        return Response.ok(applyInfoForSync).build();
    }

    // 车辆调度-车辆调度用车审核
    public Response approveApply(String[] args, PrincipalExt userPrincipal) throws Exception {
        // CLDDYCSH# ApproveSug的json格式
        if (args.length == 2) {
            ObjectMapper objectMapper = new ObjectMapper();
            ApproveSugForSync approveSugInfo = objectMapper.readValue(args[1], ApproveSugForSync.class);
            if (StringUtils.isNotBlank(approveSugInfo.getUuid()) && StringUtils.isNotBlank(approveSugInfo.getApplyNo())) {
                return insert(approveSugInfo.getApplyNo(), approveSugInfo, userPrincipal);
            }
        }
        throw new InvalidParameterException();
    }

    /**
     * 车辆调度-车辆调度用车审核
     *
     * @param approveSugInfo 审核信息
     * @return
     */
    public Response insert(String applyNo, ApproveSugForSync approveSugInfo, PrincipalExt userPrincipal) {
        // 审核信息
        TBusApproveSugEntity approveSugEntity = applicationRepository.getApplyApproveInfo(approveSugInfo.getUuid());
        if (approveSugEntity == null) {
            applicationRepository.approveApplication(null, applyNo, SyncEntityConvert.toApproveEntity(approveSugInfo), userPrincipal);
        } else {
            applicationRepository.updateApproveInfo(approveSugInfo.getUuid(), SyncEntityConvert.toApproveEntity(approveSugInfo), userPrincipal);
        }
        return Response.ok(approveSugInfo).build();
    }

    // 更新 调度单 信息
    public Response dispatchApplys(String[] args, PrincipalExt userPrincipal) throws Exception {
        // CLDD#申请单GUID#调度单GUID##浙F123456#驾驶员工号#调度员ID#用车人ID#用车人(联系方式)#用车人部门ID#嘉兴#新安江#2015-10-09 08:30#2015-10-09 17:00
        if (args.length == 2 && StringUtils.isNotBlank(args[1])) {
            SyncSchedule schedule = objectMapper.readValue(args[1], SyncSchedule.class);
            return update(schedule, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新 调度单 信息
     *
     * @param schedule 调度单信息
     * @return
     */
    public Response update(SyncSchedule schedule, PrincipalExt userPrincipal) {
        List<TBusApplyinfoEntity> applyInfoList = new ArrayList<>();
        if (schedule.getApply() != null) {
            for (ScheduleApplyInfoForSync apply : schedule.getApply()) {
                TBusApplyinfoEntity applyinfoEntity;
                if (null != apply.getApplyNo()) {
                    // 申请单信息
                    applyinfoEntity = applicationRepository.getApplicationById(apply.getUuId(), userPrincipal);
                    if (applyinfoEntity == null) {
                        applyinfoEntity = EntityConvert.toEntity(SyncEntityConvert.toApplyEntity(apply), apply.getUuId());
                        applyinfoEntity = applicationRepository.createApplicationForSync(applyinfoEntity, userPrincipal);
                    } else {
                        applyinfoEntity = EntityConvert.toEntity(SyncEntityConvert.toApplyEntity(apply));
                        applyinfoEntity = applicationRepository.updateApplication(apply.getUuId(), applyinfoEntity, userPrincipal);
                    }
                } else {
                    applyinfoEntity = EntityConvert.toEntity(SyncEntityConvert.toApplyEntity(apply));
                }
                applyInfoList.add(applyinfoEntity);
            }
        }

        // 调度单信息生成
        List<TBusScheduleRelaEntity> scheduleRelaList = new ArrayList<>();
        if (schedule.getRela() != null) {
            for (SchedulerelaForSync scheduleRela : schedule.getRela()) {
                TBusScheduleRelaEntity scheduleRelaEntity = scheduleRepository.getScheduleById(scheduleRela.getUuId(), userPrincipal);
                if (scheduleRelaEntity == null) {
                    scheduleRelaEntity = scheduleRepository.createSchedule(SyncEntityConvert.toCommonBean(scheduleRela, applyInfoList), userPrincipal);
                } else {
                    scheduleRelaEntity = scheduleRepository.updateSchedules(scheduleRela.getUuId(), SyncEntityConvert.toCommonBean(scheduleRela, applyInfoList), userPrincipal);
                }
                scheduleRelaList.add(scheduleRelaEntity);
            }
        }

        // 车辆调度信息
        if (schedule.getCar() != null) {
            for (ScheduleCarForSync scheduleCar : schedule.getCar()) {
                TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCar(scheduleCar.getUuId(), userPrincipal);
                if (scheduleCarEntity == null) {
                    scheduleRepository.createScheduleCar4Sync(SyncEntityConvert.getScheduleCarInfo(scheduleCar, scheduleRelaList), userPrincipal);
                } else {
                    scheduleRepository.updateScheduleCar4Sync(scheduleCar.getUuId(), SyncEntityConvert.getScheduleCarInfo(scheduleCar, scheduleRelaList), userPrincipal);
                }
            }
        }
        em.flush();
        return Response.ok(schedule).build();
    }

    // 更新 请假申请 信息
    public Response updateLeaveInfo(String[] args, PrincipalExt userPrincipal) {
        // RYQJ#驾驶员工号#事假#2015-10-11
        if (args.length == 4 && StringUtils.isNotBlank(args[1])) {
            SyncLeave leave = new SyncLeave();
            leave.setDriverEid(args[1]);
            leave.setLeaveType(LeaveType.valueOf(args[2]));
            leave.setDate(DateTime.parse(args[3], SyncEntityConvert.dateFormat));
            return update(args[1], leave, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新 请假申请 信息
     *
     * @param driverGuid 驾驶员ID
     * @param leave      请假信息
     * @return
     */
    public Response update(String driverGuid, SyncLeave leave, PrincipalExt userPrincipal) {
        driverRepository.updateLeaveInfoWithPermission(driverGuid, SyncEntityConvert.toKqEntity(leave),
                userPrincipal);
        return Response.ok(leave).build();
    }

    // 更新 用车负责人 信息
    public Response updateMainUser(String[] args, PrincipalExt userPrincipal) {
        if (args.length == 4 && StringUtils.isNotBlank(args[1])) {
            SyncMainUser mainUser = new SyncMainUser();
            mainUser.setMainUserId(args[1]);
            mainUser.setName(args[2]);
            mainUser.setMobile(args[3]);
            return update(args[1], mainUser, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 更新 用车负责人 信息
     *
     * @param mainUserId 用车人id
     * @param mainUser   用车负责人信息
     * @return
     */
    public Response update(String mainUserId, SyncMainUser mainUser, PrincipalExt userPrincipal) {
        passengerRepository.updateMainUserWithPermission(mainUserId, SyncEntityConvert.toEntity(mainUser),
                userPrincipal);
        return Response.ok(mainUser).build();
    }

    // 删除 用车负责人 信息
    public Response deleteMainUser(String[] args, PrincipalExt userPrincipal) {
        // LXSC#用车联系人id
        if (args.length == 2 && StringUtils.isNotBlank(args[1])) {
            return deleteMainUser(args[1], userPrincipal);
        }
        throw new InvalidParameterException();
    }

    /**
     * 删除 用车负责人 信息
     *
     * @param mainUserId 用车人GUID
     * @return
     */
    public Response deleteMainUser(String mainUserId, PrincipalExt userPrincipal) {
        boolean result = passengerRepository.deleteMainUserWithPermission(mainUserId,
                userPrincipal);
        if (result == true) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // 新增角色
    public Response insertRole(String[] args, PrincipalExt userPrincipal) {
        // JSXZ#roleId#roleName#deptId#roleType#locked#remark
        TSysRoleEntity role = new TSysRoleEntity();
        role.setRoleid(args[1]);
        role.setRolename(args[2]);
        role.setDeptid(args[3]);
        role.setRoletype(args[4]);
        role.setLocked(args[5]);
        role.setRemark(args[6]);
        TSysRoleEntity roleEntity = roleRepository.getRole(args[1], userPrincipal);
        if (roleEntity == null) {
            return insertRole(role, userPrincipal);
        } else {
            return updateRole(args[1], role, userPrincipal);
        }
    }

    public Response insertRole(TSysRoleEntity roleEntity, PrincipalExt userPrincipal) {
        roleRepository.createRole(roleEntity, userPrincipal);
        return Response.ok().build();
    }

    // 修改角色
    public Response updateRole(String[] args, PrincipalExt userPrincipal) {
        // JSXG#roleId#roleName#deptId#roleType#locked#remark
        TSysRoleEntity role = new TSysRoleEntity();
        role.setRoleid(args[1]);
        role.setRolename(args[2]);
        role.setDeptid(args[3]);
        role.setRoletype(args[4]);
        role.setLocked(args[5]);
        role.setRemark(args[6]);
        TSysRoleEntity roleEntity = roleRepository.getRole(args[1], userPrincipal);
        if (roleEntity != null) {
            return updateRole(args[1], role, userPrincipal);
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    public Response updateRole(String roleId, TSysRoleEntity roleEntity, PrincipalExt userPrincipal) {
        roleRepository.updateRole(roleId, roleEntity, userPrincipal);
        return Response.ok().build();
    }

    // 删除角色
    public Response deleteRole(String[] args, PrincipalExt userPrincipal) {
        // JSSC#roleId
        deleteRole(args[1], userPrincipal);
        return Response.ok().build();
    }

    public Response deleteRole(String roleId, PrincipalExt userPrincipal) {
        TSysRoleEntity roleEntity = roleRepository.getRole(roleId, userPrincipal);
        if (roleEntity != null) {
            roleRepository.deleteRole(roleId, userPrincipal);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // 角色授权
    public Response grantRole(String[] args, PrincipalExt userPrincipal) {
        // JSSQ#roleId#menuInfo  例子 JSSQ# 520b7d52b49e439383ee75863cfa69fa#   [{"02300000":"add,menuLook,update,menuLook","03000000":"menuLook"}]
        return Response.ok().build();
    }

    public Response cancelScheduleCar(String[] args, PrincipalExt userPrincipal) {
        // 11.5 派车作废(作废驾驶员和车辆)   PCZF#派车单id#取消类型#取消原因
        if (args.length == 4 && null != args[1]) {
            scheduleRepository.cancelScheduleCar4Sync(args[1], args[2], args[3], userPrincipal);
            return Response.ok().build();
        }
        throw new InvalidParameterException();
    }

    public Response setUniteDispatch(String[] args, PrincipalExt userPrincipal) {
        // 11.6  车队统一调度设置            TDTYDDSZ#orgid#model
        if (args.length == 3) {
            busBusinessRelationRepository.setUniteDispatch(args[1], args[2], userPrincipal);
            return Response.ok().build();
        }
        throw new InvalidParameterException();
    }

    public Response setBusinessRelation(String[] args, PrincipalExt userPrincipal) {
        // 11.7  业务关系统一调度设置        YWGXDDSZ#userId#model
        if (args.length == 3) {
            SyncBusinessRelation businessRelation = new SyncBusinessRelation();
            businessRelation.setUserId(args[1]);
            businessRelation.setModel(args[2]);
            return updateBusinessRelation(businessRelation, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    public Response updateBusinessRelation(String[] args, PrincipalExt userPrincipal) {
        // 11.8  业务单位设置联系            YWDWSZLX#userId#orgId#bussnessids
        // bussnessids格式例如 00209-00219-00223
        if (args.length == 4) {
            SyncBusinessRelation businessRelation = new SyncBusinessRelation();
            businessRelation.setUserId(args[1]);
            businessRelation.setOrgId(args[2]);
            String[] businessOrgIdArray = args[3].split("-");
            businessRelation.setBusinessOrgIds(Arrays.asList(businessOrgIdArray));
            return updateBusinessRelation(businessRelation, userPrincipal);
        }
        throw new InvalidParameterException();
    }

    public Response updateBusinessRelation(SyncBusinessRelation businessRelation, PrincipalExt userPrincipal) {
        busBusinessRelationRepository.updateBusinessRelation(SyncEntityConvert.toEntityList(businessRelation), userPrincipal);
        return Response.ok().build();
    }

    public enum RequestType {
        // 部门信息变更
        BMBG,       // 部门信息变更
        BMXZ,       // 部门信息新增
        BMSC,       // 部门信息删除

        // 车辆信息变更
        CLBG,       // 车辆信息变更(移交到外部 + 内部移交)
        CLBGWBJJ,   // 车辆交接 (从外部移交进来)

        // 人员信息变更
        RYBG,       // 驾驶员信息变更
        RYSC,       // 驾驶员信息删除
        RYXZ,       // 驾驶员新增

        // 车辆维修状态
        CLWX,       // 车辆维修状态 (进厂 + 出厂 + 报修)
        CLWXQX,     // 进厂取消

        // 车辆违章信息
        CLWZ,       // 车辆违章信息

        // 车辆调度模块
        CLDDTJSQ,       // 车辆调度-提交调度申请
        CLDDYCSH,       // 车辆调度-车辆调度用车审核
        CLDD,           // 车辆调度
        CLDDAPPLYCANCEL,    // 取消已经调度的申请单
        CLDDSQSC,       // 车辆调度-申请删除
        CLDDCAR,        // 修改调度单 车辆
        CLDDJSY,        // 修改调度单 驾驶员
        CLDDZDXT,       // 车辆调度-车辆调度总调协调（提交给总调）
        CLDDDDXT,       // 车辆调度-车辆调度调度协调（变更车队）
        DDQX,           // 车辆调度取消
        DDZF,           // 车辆调度作废
        CLDDXTTH,       // 车辆调度-协调退回
        CLDDDGQRFH,     // 驾驶员确认返回
        CLDDXCJL,       // 车辆调度-行车记录
        CLDDDDTH,       // 车辆调度-调度退回
        CLDDYCPJ,       // 车辆调度-添加评价接口
        PCZF,           // 派车作废(作废驾驶员和车辆)
        CLDDSQXG,       // 更新申请单

        TDTYDDSZ,       // 车队统一调度设置
        YWGXDDSZ,       // 业务关系同意调度设置
        YWDWSZLX,       // 业务单位设置联系

        // 驾驶员请假状态
        RYQJ,       // 驾驶员请假状态  (日常考勤)

        // 用车联系人
        LXGG,       // 用车联系人更改
        LXXZ,       // 添加用车人信息
        LXSC,       // 删除用车人信息

        // 修改user用户
        UIGG,       // 用户信息修改
        XZYH,       // 新增用户
        YHSC,       // 用户删除
        JSBG,       // 角色变更

        JSXZ,       // 新增角色
        JSXG,       // 修改角色
        JSSC,       // 删除角色
        JSSQ,       // 角色授权

        GPS,        // GPS安装情况更新
    }
}
