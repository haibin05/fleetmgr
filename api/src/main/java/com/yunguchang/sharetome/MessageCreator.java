package com.yunguchang.sharetome;

import com.yunguchang.data.ApplicationRepository;
import com.yunguchang.data.BusBusinessRelationRepository;
import com.yunguchang.data.DriverRepository;
import com.yunguchang.data.UserRepository;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.ScheduleStatus;
import com.yunguchang.model.common.User;
import com.yunguchang.model.persistence.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;

import static com.yunguchang.sharetome.ShareTomeEvent.*;

/**
 * Created by gongy on 2016/1/15.
 */
@Stateless
public class MessageCreator {
    @Inject
    private DriverRepository driverRepository;
    @Inject
    private UserRepository userRepository;

    @Inject
    private ApplicationRepository applicationRepository;
    @Inject
    private BusBusinessRelationRepository busBusinessRelationRepository;

    // 申请 审核 消息
    public Builder addNewMember(TBusApplyinfoEntity applyEntity, List<TSysUserEntity> newMemberList, MessageCategory messageCategory) {
        Builder shareTomeEventBuilder = builder()
                .setContent(EntityConvert.toPostContent(applyEntity))
                .setMessageCategory(messageCategory);

        if (applyEntity.getPostId4Dispatcher() != null) {
            for(TSysUserEntity newMember : newMemberList) {
                shareTomeEventBuilder.addRecipient(EntityConvert.toSnsUser(newMember, User.UserType.DISPATCHER));
            }
        }

        return shareTomeEventBuilder;
    }
    // 申请 审核 消息
    public Builder sendPostMessageToSnsByApplicationEntity(TBusApplyinfoEntity applyEntity, MessageCategory messageCategory, String status) {
        if (status != null) {
            messageCategory = EntityConvert.convertStatus(status);
        }

        Builder shareTomeEventBuilder = builder()
                .setContent(EntityConvert.toPostContent(applyEntity))
                .setMessageCategory(messageCategory);

        // 添加申请人
        if (applyEntity.getCoordinator() != null) {
            TSysUserEntity coordinator = userRepository.getUserById(applyEntity.getCoordinator().getUserid());
            shareTomeEventBuilder.addRecipient(EntityConvert.toSnsUser(coordinator, User.UserType.COORDINATOR));
        }

        // 添加审核人
        // 评价申请单，不给审核人发送消息
        if (!MessageCategory.APPLICATION_RATE_APPLY.equals(messageCategory)) {
            shareTomeEventBuilder.addRecipient(EntityConvert.toSnsUser(getAuditor(applyEntity), User.UserType.AUDITOR));
        }

        // 审核通过消息，不给调度员发送消息
        if (!MessageCategory.APPLICATION_APPROVE.equals(messageCategory)) {
            // 添加调度员
            List<TSysUserEntity> dispatcherList = getDispatcher(applyEntity);
            for (TSysUserEntity dispatcher : dispatcherList) {
                shareTomeEventBuilder.addRecipient(EntityConvert.toSnsUser(dispatcher, User.UserType.DISPATCHER));
            }
        }

        return shareTomeEventBuilder;
    }

    // 调度 消息
    public List<Builder> sendPostMessageToSnsByScheduleEntity(TBusScheduleRelaEntity scheduleEntity, MessageCategory messageCategory) {
        List<Builder> builderList = new ArrayList<>();
        for (TBusApplyinfoEntity applyEntity : scheduleEntity.getApplications()) {
            if (applyEntity.getCoordinator() != null) {
                Builder coordinatorBuilder = builder()
                        .setContent(EntityConvert.toPostContent(applyEntity))
                        .setMessageCategory(messageCategory);

                // 添加申请人
                TSysUserEntity coordinator = userRepository.getUserById(applyEntity.getCoordinator().getUserid());
                coordinatorBuilder.addRecipient(EntityConvert.toSnsUser(coordinator, User.UserType.COORDINATOR));

                // 添加审核人
                coordinatorBuilder.addRecipient(EntityConvert.toSnsUser(getAuditor(applyEntity), User.UserType.AUDITOR));
                builderList.add(coordinatorBuilder);
            }
        }

        // 添加调度员
        Builder dispatcherBuilder = builder()
                .setContent(EntityConvert.toPostContent(scheduleEntity))
                .setMessageCategory(messageCategory);

        dispatcherBuilder.addRecipient(EntityConvert.toSnsUser(scheduleEntity.getSenduser(), User.UserType.DISPATCHER));
        builderList.add(dispatcherBuilder);

        // 添加驾驶员
        for (TBusScheduleCarEntity carEntity : scheduleEntity.getScheduleCars()) {
            if (carEntity.getDriver() != null) {
                Builder driverBuilder = builder()
                        .setContent(EntityConvert.toPostContent(carEntity))
                        .setMessageCategory(messageCategory);

                TRsDriverinfoEntity driver = driverRepository.loadDriverById(carEntity.getDriver().getUuid(), null);
                driverBuilder.addRecipient(EntityConvert.toSnsUser(driver));
                builderList.add(driverBuilder);
            }
        }
        return builderList;
    }

    // 调度 消息
    public List<Builder> sendPostMessageToSnsByOldScheduleEntity(TBusScheduleRelaEntity scheduleEntity) {
        List<Builder> builderList = new ArrayList<>();

        for (TBusApplyinfoEntity applyEntity : scheduleEntity.getApplications()) {
            if (applyEntity.getCoordinator() != null) {
                Builder applyBuilder = builder().
                        setContent(EntityConvert.toPostContent(applyEntity))
                        .setMessageCategory(MessageCategory.SCHEDULE_DELETE);

                // 添加申请人
                TSysUserEntity coordinator = userRepository.getUserById(applyEntity.getCoordinator().getUserid());
                applyBuilder.addRecipient(EntityConvert.toSnsUser(coordinator, User.UserType.COORDINATOR));

                // 添加审核人
                applyBuilder.addRecipient(EntityConvert.toSnsUser(getAuditor(applyEntity), User.UserType.AUDITOR));

                // 添加调度员
                applyBuilder.addRecipient(EntityConvert.toSnsUser(scheduleEntity.getSenduser(), User.UserType.DISPATCHER));

                builderList.add(applyBuilder);
            }
        }

        // 添加驾驶员
        for (TBusScheduleCarEntity carEntity : scheduleEntity.getScheduleCars()) {
            if (carEntity.getDriver() != null) {
                Builder driverBuilder = builder().
                        setContent(EntityConvert.toPostContent(carEntity))
                        .setMessageCategory(MessageCategory.SCHEDULE_DELETE);

                TRsDriverinfoEntity driver = driverRepository.loadDriverById(carEntity.getDriver().getUuid(), null);
                driverBuilder.addRecipient(EntityConvert.toSnsUser(driver));
                builderList.add(driverBuilder);
            }
        }
        return builderList;
    }

    // 调度 消息  修改调度单之后发送的消息
    public List<Builder> sendPostMessageToSnsByMergeScheduleEntity(TBusScheduleRelaEntity oldScheduleEntity, TBusScheduleRelaEntity newScheduleEntity) {
        List<Builder> builderList = new ArrayList<>();

        int countCarUpdated = 0;
        // 调度出车单 - 检查 旧调度单
        List<TBusScheduleCarEntity> noChangeCarList = new ArrayList<>();
        List<TBusScheduleCarEntity> newScheduleCarList = new ArrayList<>(newScheduleEntity.getScheduleCars());
        for (TBusScheduleCarEntity oldScheduleCar : oldScheduleEntity.getScheduleCars()) {
            boolean deleteFlg = true;
            for (TBusScheduleCarEntity newScheduleCar : newScheduleEntity.getScheduleCars()) {
                if (oldScheduleCar.getCar().getId().equals(newScheduleCar.getCar().getId()) && !oldScheduleCar.getDriver().getUuid().equals(newScheduleCar.getDriver().getUuid())) {
                    // 更换驾驶员 - old driver and new driver
                    builderList.add(getBuilderForDriver(oldScheduleEntity, newScheduleCar, MessageCategory.SCHEDULE_CHANGE_DELETE_CAR, oldScheduleCar.getDriver().getUuid()));
                    countCarUpdated++;
                } else if (oldScheduleCar.getDriver().getUuid().equals(newScheduleCar.getDriver().getUuid()) && !oldScheduleCar.getCar().getId().equals(newScheduleCar.getCar().getId())) {
                    // 更换车辆 - old driver
                    if (!oldScheduleCar.getStatus().equals(ScheduleStatus.CANCELED.id())) {
                        builderList.add(getBuilderForDriver(oldScheduleEntity, newScheduleCar, MessageCategory.APPLICATION_DISPATCH_UPDATE));
                        countCarUpdated++;
                    }
                } else if (oldScheduleCar.getCar().getId().equals(newScheduleCar.getCar().getId()) && oldScheduleCar.getDriver().getUuid().equals(newScheduleCar.getDriver().getUuid())) {
                    if (!oldScheduleCar.getStatus().equals(ScheduleStatus.CANCELED.id())) {
                        noChangeCarList.add(newScheduleCar);
                    }
                } else {
                    // 继续检查
                    continue;
                }
                deleteFlg = false;
                newScheduleCarList.remove(newScheduleCar);
                break;
            }
            if (deleteFlg) {
                countCarUpdated++;
                // 删除车辆驾驶员
                builderList.add(getBuilderForDriver(oldScheduleEntity, oldScheduleCar, MessageCategory.SCHEDULE_CHANGE_DELETE_SCHEDULE_CAR));
            }
        }
        // 调度出车单 - 检查 新调度单 新加 任务单
        for (TBusScheduleCarEntity newScheduleCar : newScheduleCarList) {
            countCarUpdated++;
            builderList.add(getBuilderForDriver(newScheduleEntity, newScheduleCar, MessageCategory.APPLICATION_DISPATCH));
        }

        // 申请单
        List<TBusApplyinfoEntity> oldApplyList = new ArrayList<>(oldScheduleEntity.getApplications());
        List<TBusApplyinfoEntity> newApplyList = new ArrayList<>(newScheduleEntity.getApplications());
        Map<String, TBusApplyinfoEntity> newApplyMap = new HashMap<>();
        for (TBusApplyinfoEntity applyEntity : newApplyList) {
            newApplyMap.put(applyEntity.getUuid(), applyEntity);
        }
        List<TBusApplyinfoEntity> noChangeApplyList = new ArrayList<>();
        for (TBusApplyinfoEntity applyEntity : oldScheduleEntity.getApplications()) {
            if (newApplyMap.containsKey(applyEntity.getUuid())) {   // 没有变化的申请单不需要发送消息
                oldApplyList.remove(applyEntity);
                noChangeApplyList.add(newApplyMap.get(applyEntity.getUuid()));
            }
            removeApplyById(newApplyList, applyEntity.getUuid());
        }
        int countApplyUpdated = 0;
        // 被拒绝的申请单
        for (TBusApplyinfoEntity applyEntity : oldApplyList) {
            countApplyUpdated++;
            builderList.add(getBuilderForApply(applyEntity, MessageCategory.SCHEDULE_CHANGE_DELETE_APPLY));
        }
        // 新加的申请单
        for (TBusApplyinfoEntity applyEntity : newApplyList) {
            countApplyUpdated++;
            builderList.add(getBuilderForApply(applyEntity, MessageCategory.SCHEDULE_CHANGE_ADD_APPLY));
        }
        if (countCarUpdated > 0) {
            for (TBusApplyinfoEntity applyEntity : noChangeApplyList) {
                builderList.add(getBuilderForApplyWithCar(applyEntity, newScheduleEntity, MessageCategory.APPLICATION_DISPATCH_UPDATE));
            }
        }
        if (countApplyUpdated > 0) {
            for (TBusScheduleCarEntity scheduleCarEntity : noChangeCarList) {
                builderList.add(getBuilderForDriver(newScheduleEntity, scheduleCarEntity, MessageCategory.SCHEDULE_CHANGE_ADD_APPLY));
            }
        }

        if(builderList.size() > 0) {    // 对于没有更新的操作不发送消息
            // 添加调度员
            Builder builder = builder()
                    .setContent(EntityConvert.toPostContent(newScheduleEntity))
                    .setMessageCategory(MessageCategory.APPLICATION_DISPATCH_UPDATE);

            builder.addRecipient(EntityConvert.toSnsUser(newScheduleEntity.getSenduser(), User.UserType.DISPATCHER));
            builderList.add(builder);
        }

        return builderList;
    }

    public Builder getBuilderForApplyWithCar(TBusApplyinfoEntity applyEntity, TBusScheduleRelaEntity scheduleRelaEntity, MessageCategory messageCategory) {
        Builder applyBuilder = builder()
                .setContent(EntityConvert.toPostContent(scheduleRelaEntity))
                .setContent(EntityConvert.toPostContent(applyEntity))
                .setMessageCategory(messageCategory);

        // 添加申请人
        TSysUserEntity coordinator = userRepository.getUserById(applyEntity.getCoordinator().getUserid());
        applyBuilder.addRecipient(EntityConvert.toSnsUser(coordinator, User.UserType.COORDINATOR));

        // 添加审核人
        applyBuilder.addRecipient(EntityConvert.toSnsUser(getAuditor(applyEntity), User.UserType.AUDITOR));
        return applyBuilder;
    }
    public Builder getBuilderForApply(TBusApplyinfoEntity applyEntity, MessageCategory messageCategory) {
        Builder applyBuilder = builder()
                .setContent(EntityConvert.toPostContent(applyEntity))
                .setMessageCategory(messageCategory);

        // 添加申请人
        TSysUserEntity coordinator = userRepository.getUserById(applyEntity.getCoordinator().getUserid());
        applyBuilder.addRecipient(EntityConvert.toSnsUser(coordinator, User.UserType.COORDINATOR));

        // 添加审核人
        applyBuilder.addRecipient(EntityConvert.toSnsUser(getAuditor(applyEntity), User.UserType.AUDITOR));
        return applyBuilder;
    }
    public Builder getBuilderForSchedule(TBusScheduleRelaEntity scheduleRelaEntity, MessageCategory messageCategory) {
        // 添加调度员
        Builder builder = builder()
                .setContent(EntityConvert.toPostContent(scheduleRelaEntity))
                .setMessageCategory(messageCategory);

        builder.addRecipient(EntityConvert.toSnsUser(scheduleRelaEntity.getSenduser(), User.UserType.DISPATCHER));
        return builder;
    }

    public Builder getBuilderForDriver(TBusScheduleRelaEntity scheduleRelaEntity, TBusScheduleCarEntity carEntity, MessageCategory messageCategory, String... driverIdArray) {
        Builder driverBuilder = builder()
                .setContent(EntityConvert.toPostContent(scheduleRelaEntity))
                .setContent(EntityConvert.toPostContent(carEntity))
                .setMessageCategory(messageCategory);

        // 添加驾驶员
        String driverId;
        if (driverIdArray == null || driverIdArray.length == 0) {
            driverId = carEntity.getDriver().getUuid();
        } else {
            driverId = driverIdArray[0];
        }
        TRsDriverinfoEntity driver = driverRepository.loadDriverById(driverId, null);
        driverBuilder.addRecipient(EntityConvert.toSnsUser(driver));
        return driverBuilder;
    }

    private List<TBusApplyinfoEntity> removeApplyById(List<TBusApplyinfoEntity> applyList, String applyId) {
        for (int i = 0, size = applyList.size(); i < size; i++) {
            if (applyList.get(i).getUuid().equals(applyId)) {
                applyList.remove(i--);
                size--;
            }
        }
        return applyList;
    }

    private List<TBusScheduleCarEntity> removeCarById(List<TBusScheduleCarEntity> scheduleCarList, String schedukeCarId) {
        for (int i = 0, size = scheduleCarList.size(); i < size; i++) {
            if (scheduleCarList.get(i).getUuid().equals(schedukeCarId)) {
                scheduleCarList.remove(i);
            }
        }
        return scheduleCarList;
    }

    // 出厂 回厂 消息
    public List<Builder> sendPostMessageToSnsByRecordEntity(TBusRecordinfoEntity recordEntity, MessageCategory messageCategory) {
        List<Builder> builderList = new ArrayList<>();

        Set<String> userIdSet = new HashSet<>();
        if (recordEntity.getScheduleCar() != null) {
            Builder shareTomeEventBuilder = builder()
                    .setContent(EntityConvert.toPostContent(recordEntity))
                    .setMessageCategory(messageCategory);

            // 添加调度员
            shareTomeEventBuilder.addRecipient(EntityConvert.toSnsUser(recordEntity.getSchedule().getSenduser(), User.UserType.DISPATCHER));
            userIdSet.add(recordEntity.getSchedule().getSenduser().getUserid());

            // 添加驾驶员
            shareTomeEventBuilder.addRecipient(EntityConvert.toSnsUser(recordEntity.getScheduleCar().getDriver()));
            builderList.add(shareTomeEventBuilder);
        }

        for (TBusApplyinfoEntity applyEntity : recordEntity.getSchedule().getApplications()) {
            if (applyEntity.getCoordinator() != null && !userIdSet.contains(applyEntity.getCoordinator().getUserid())) {
                Builder coordinatorBuilder = builder()
                        .setContent(EntityConvert.toPostContent(applyEntity))
                        .setContent(EntityConvert.toPostContent(recordEntity))
                        .setMessageCategory(messageCategory);

                // 添加申请人
                TSysUserEntity coordinator = userRepository.getUserById(applyEntity.getCoordinator().getUserid());
                coordinatorBuilder.addRecipient(EntityConvert.toSnsUser(coordinator, User.UserType.COORDINATOR));

//                // 添加审核人
//                coordinatorBuilder.addRecipient(EntityConvert.toSnsUser(getAuditor(applyEntity), User.UserType.AUDITOR));

                builderList.add(coordinatorBuilder);
            }
        }

        return builderList;
    }

    // 评价消息
    public Builder sendPostMessageToSnsByEvaluateEntity(TBusEvaluateinfoEntity evaluateEntity, MessageCategory messageCategory) {
        Builder shareTomeEventBuilder = builder()
                .setContent(EntityConvert.toPostContent(evaluateEntity))
                .setMessageCategory(messageCategory);

        if (evaluateEntity.getApplication() != null && evaluateEntity.getApplication().getCoordinator() != null) {
            TSysUserEntity coordinator = userRepository.getUserById(evaluateEntity.getApplication().getCoordinator().getUserid());
            // 添加申请人
            shareTomeEventBuilder.addRecipient(EntityConvert.toSnsUser(coordinator, User.UserType.COORDINATOR));
//            // 添加审核人
//            shareTomeEventBuilder.addRecipient(EntityConvert.toSnsUser(getAuditor(evaluateEntity.getApplication()), User.UserType.AUDITOR));
        }

        // 添加调度员
        if (evaluateEntity.getApplication() != null && evaluateEntity.getApplication().getSenduser() != null) {
            TSysUserEntity dispatcher = evaluateEntity.getApplication().getSenduser();
            shareTomeEventBuilder.addRecipient(EntityConvert.toSnsUser(dispatcher, User.UserType.DISPATCHER));
        }

        // 驾驶员
        if (MessageCategory.APPLICATION_DISPATCH.equals(messageCategory)
                || MessageCategory.APPLICATION_RATE_DRIVER.equals(messageCategory)
                || MessageCategory.APPLICATION_RATE_PASSENGER.equals(messageCategory)) {
            TRsDriverinfoEntity driver = evaluateEntity.getDriver();
            shareTomeEventBuilder.addRecipient(EntityConvert.toSnsUser(driver));
        }
        return shareTomeEventBuilder;
    }

    private TSysUserEntity getAuditor(TBusApplyinfoEntity applyEntity) {
        String passengerId = applyEntity.getPassenger().getUuid();
        TSysUserEntity auditor;
        try {
            auditor = busBusinessRelationRepository.getAuditorOfPassenger(passengerId);
        }catch (Exception e) {
            auditor = null;
        }
        return auditor;
    }

    private List<TSysUserEntity> getDispatcher(TBusApplyinfoEntity applyEntity) {
        List<TSysUserEntity> dispatcherList = new ArrayList<>();
        if (applyEntity.getSenduser() == null) {
            String passengerId = applyEntity.getPassenger().getUuid();
            dispatcherList.addAll(busBusinessRelationRepository.getDispatcherOfPassenger(passengerId));
        } else {
            dispatcherList.add(applyEntity.getSenduser());
        }
        return dispatcherList;
    }

}
