package com.yunguchang.model;

import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.*;
import com.yunguchang.model.sync.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 禕 on 2015/9/11.
 */
public class SyncEntityConvert {

    public static DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter shortDateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    public static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static String fullDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    // SyncFleet -> TSysOrgEntity
    public static TSysOrgEntity toEntity(SyncFleet fleet) {
        if (fleet == null) {
            return null;
        }
        TSysOrgEntity entity = new TSysOrgEntity();
        entity.setOrgid(fleet.getId());
        entity.setOrgname(fleet.getName());

        entity.setOrgtype(fleet.getOrgType());
        entity.setGsid(fleet.getGsid());
        TSysOrgEntity parent = new TSysOrgEntity();
        parent.setOrgid(fleet.getParentid());
        entity.setParent(parent);
        entity.setEnabled(fleet.getEnable());
        entity.setDdtj(fleet.getDdtj());
        if (StringUtils.isNotBlank(fleet.getSortNo())) {
            entity.setSortno(Integer.valueOf(fleet.getSortNo()));
        }
        entity.setLeaf("1");    // 默认叶子节点
        entity.setModel("0");    // 业务模式-是否统一调度，1：是；0：否
        entity.setRemark(fleet.getRemark());

        return entity;
    }

    // SyncCar -> TAzCarinfoEntity
    public static TAzCarinfoEntity toEntity(SyncCar car) {
        if (car == null) {
            return null;
        }
        // 车队信息
        TSysOrgEntity sscd = new TSysOrgEntity();
        sscd.setOrgid(car.getSscd());
        // 驾驶员信息
        TRsDriverinfoEntity driver = new TRsDriverinfoEntity();
        driver.setUuid(car.getDriver());

        TAzCarinfoEntity entity = new TAzCarinfoEntity();
        entity.setCphm(car.getCphm());      // 车牌号
        entity.setSysOrg(sscd);             //所属车队
        entity.setDriver(driver);           //驾驶员编号
//        entity.setGpsazqk(car.getGps());    //是否安装GPS 1-安装
        // 02 未安装 01 安装         ==>>   GPS安装情况-   1：是；0：否
        // 02	未安装 04	缓装 01	已安装  05	其他   03	不安装
        if (null != car.getGps()) {
            if ("01".equals(car.getGps()) || "1".equals(car.getGps())) {
                entity.setGpsazqk("1");
            } else {
                entity.setGpsazqk("0");
            }
        }

        entity.setCllx(car.getCarType());   //  “05”

        return entity;
    }

    // SyncCar -> TAzCarinfoEntity
    public static TAzCarinfoEntity toEntity(CarJJForSync car) {
        if (car == null) {
            return null;
        }
        // 车队信息
        TSysOrgEntity sscd = new TSysOrgEntity();
        sscd.setOrgid(car.getSscd());
        // 驾驶员信息
        TRsDriverinfoEntity driver = new TRsDriverinfoEntity();
        driver.setUuid(car.getJsy());

        TAzCarinfoEntity entity = new TAzCarinfoEntity();
        entity.setId(car.getId());
        entity.setCphm(car.getCphm());      // 车牌号
        entity.setSysOrg(sscd);             //所属车队
        entity.setDriver(driver);           //驾驶员编号
        entity.setCllx(car.getCllx());   //  “05”
//        private Date clccrq;        // 车辆出厂日期-
//        entity.setGpsazqk(car.getGpsazqk());    //是否安装GPS 1-安装

        // 02 未安装 01 安装         ==>>   GPS安装情况-   1：是；0：否
        // 02	未安装 04	缓装 01	已安装  05	其他   03	不安装
        if (null != car.getGpsazqk()) {
            if ("01".equals(car.getGpsazqk()) || "1".equals(car.getGpsazqk())) {
                entity.setGpsazqk("1");
            } else {
                entity.setGpsazqk("0");
            }
        }
        entity.setStatus(car.getStatus());  // 车辆状态 默认为1，逻辑删除时修改为0

        return entity;
    }

    // SyncDriver -> TRsDriverInfoEntity
    public static TRsDriverinfoEntity toEntity(SyncDriver driver) {
        if (driver == null) {
            return null;
        }
        // 驾驶员信息
        TRsDriverinfoEntity driverEntity = new TRsDriverinfoEntity();
        // 驾驶员的id
        driverEntity.setUuid(driver.getEid());
        // 驾驶员工号  长度有变更 设置截取 20
        if (driver.getEid() != null) {
            driverEntity.setDriverno(driver.getEid().length() < 21 ? driver.getEid() : driver.getEid().substring(0, 20));
        }
        // 姓名
        driverEntity.setDrivername(driver.getName());
        // 驾驶员的性别
        driverEntity.setSex(driver.getSex());
        // 驾驶员的类型
        driverEntity.setDrivertype(driver.getDriverType());
        // 所属部门信息
        TSysOrgEntity orgEntity = new TSysOrgEntity();
        orgEntity.setOrgid(driver.getOrgid());
        // 所属部门
        driverEntity.setDepartment(orgEntity);
        // 手机号码
        driverEntity.setMobile(driver.getMobile());
        // 短号
        driverEntity.setPhone(driver.getPhone());
        // 职位
        driverEntity.setZw(driver.getPosition());
        // 驾照类型
        driverEntity.setDrivecartype(driver.getDrivecartype());

        return driverEntity;
    }

    // SyncUser -> TSysUserEntity
    public static TSysUserEntity toEntity(SyncUser user) {
        if (user == null) {
            return null;
        }
        // 所属部门信息
        TSysOrgEntity orgEntity = new TSysOrgEntity();
        orgEntity.setOrgid(user.getDeptid());

        // 用户信息
        TSysUserEntity userEntity = new TSysUserEntity();
        userEntity.setUserid(user.getId());          // UUID
        userEntity.setUserno(user.getUserNo());      // 用户编号
        userEntity.setPassword(user.getPassword());      // 密码
        userEntity.setUsername(user.getName());     // 姓名
        userEntity.setDepartment(orgEntity);        // 部门
        userEntity.setSex(user.getSex());         // 性别(0:未知;1:男;2:女)
        userEntity.setUsertype(user.getUserType());   // 人员类型(0:管理员;1:公司领导;2:公司父总司;3:绩效管理员；4:公司员工;5:)
        userEntity.setMobile(user.getMobile());      // 手机
        userEntity.setPhone(user.getPhone());       // 短号
        userEntity.setEmail(user.getEmail());       // 邮箱
        userEntity.setEnabled(user.getEnabled());     // 启用状态(1:启用;0:停用)
        userEntity.setRemark(user.getRemark());      // 备注

        userEntity.setLocked("0");  // 未被锁住

        return userEntity;
    }

    // SyncEvaluateInfo -> TBusEvaluateinfoEntity
    public static TBusEvaluateinfoEntity toEntity(SyncEvaluateInfo evaluateInfo) {
        if (evaluateInfo == null) {
            return null;
        }
        TBusEvaluateinfoEntity evaluateinfoEntity = new TBusEvaluateinfoEntity();
        evaluateinfoEntity.setUuid(evaluateInfo.getUuId());
        TSysUserEntity user = new TSysUserEntity();
        user.setUserid(evaluateInfo.getUserId());
        evaluateinfoEntity.setUser(user);
        TBusApplyinfoEntity application = new TBusApplyinfoEntity();
        application.setApplyno(evaluateInfo.getApplyNo());
        evaluateinfoEntity.setApplication(application);
        TAzCarinfoEntity carinfoEntity = new TAzCarinfoEntity();
        carinfoEntity.setId(evaluateInfo.getCarId());
        evaluateinfoEntity.setCar(carinfoEntity);
        TRsDriverinfoEntity driver = new TRsDriverinfoEntity();
        driver.setUuid(evaluateInfo.getDriverId());
        evaluateinfoEntity.setDriver(driver);
        evaluateinfoEntity.setCarscore(evaluateInfo.getCarScore());
        evaluateinfoEntity.setDriverscore(evaluateInfo.getDriverScore());
        evaluateinfoEntity.setTeamscore(evaluateInfo.getTeamscore());
        evaluateinfoEntity.setEvalstatus(evaluateInfo.getEvalStatus());
        evaluateinfoEntity.setEvaldate(new DateTime(evaluateInfo.getEvalDate().getTime()));
        evaluateinfoEntity.setRemark(evaluateInfo.getRemark());
        evaluateinfoEntity.setTeamreason(evaluateInfo.getTeamreason());
        evaluateinfoEntity.setDriverreason(evaluateInfo.getDriverreason());
        evaluateinfoEntity.setCarreason(evaluateInfo.getCarreason());
        evaluateinfoEntity.setType(evaluateInfo.getType());
        TBusScheduleRelaEntity schedule = new TBusScheduleRelaEntity();
        schedule.setUuid(evaluateInfo.getRelano());
        evaluateinfoEntity.setSchedule(schedule);
        evaluateinfoEntity.setResponseinfo(evaluateInfo.getResponseinfo());

        evaluateinfoEntity.setGdzt(evaluateInfo.getGdzt());//归档状态
        return evaluateinfoEntity;
    }

    // TBusEvaluateinfoEntity -> SyncEvaluateInfo
    public static List<SyncEvaluateInfo> fromEvaluateEntity(List<TBusEvaluateinfoEntity> evaluateInfoEntityList) {
        if (evaluateInfoEntityList == null) {
            return null;
        }
        List<SyncEvaluateInfo> evaluateInfoList = new ArrayList<>();
        for (TBusEvaluateinfoEntity entity : evaluateInfoEntityList) {
            evaluateInfoList.add(fromEntity(entity));
        }
        return evaluateInfoList;
    }

    // TBusEvaluateinfoEntity -> SyncEvaluateInfo
    public static SyncEvaluateInfo fromEntity(TBusEvaluateinfoEntity evaluateInfoEntity) {
        if (evaluateInfoEntity == null) {
            return null;
        }
        SyncEvaluateInfo evaluateInfo = new SyncEvaluateInfo();
        evaluateInfo.setUuId(evaluateInfoEntity.getUuid());
        if (evaluateInfoEntity.getUser() != null) {
            evaluateInfo.setUserId(evaluateInfoEntity.getUser().getUserid());
        }
        if (evaluateInfoEntity.getApplication() != null) {
            evaluateInfo.setApplyNo(evaluateInfoEntity.getApplication().getApplyno());
        }
        if (evaluateInfoEntity.getCar() != null) {
            evaluateInfo.setCarId(evaluateInfoEntity.getCar().getId());
        }
        if (evaluateInfoEntity.getDriver() != null) {
            evaluateInfo.setDriverId(evaluateInfoEntity.getDriver().getUuid());
        }
        evaluateInfo.setCarScore(evaluateInfoEntity.getCarscore());
        evaluateInfo.setDriverScore(evaluateInfoEntity.getDriverscore());
        evaluateInfo.setTeamscore(evaluateInfoEntity.getTeamscore());
        evaluateInfo.setEvalStatus(evaluateInfoEntity.getEvalstatus());
        evaluateInfo.setEvalDate(DateFormatUtils.format(evaluateInfoEntity.getEvaldate().getMillis(), fullDateFormat));
        evaluateInfo.setRemark(evaluateInfoEntity.getRemark());
        evaluateInfo.setTeamreason(evaluateInfoEntity.getTeamreason());
        evaluateInfo.setDriverreason(evaluateInfoEntity.getDriverreason());
        evaluateInfo.setCarreason(evaluateInfoEntity.getCarreason());
        evaluateInfo.setType(evaluateInfoEntity.getType());
        if (evaluateInfoEntity.getSchedule() != null) {
            evaluateInfo.setRelano(evaluateInfoEntity.getSchedule().getUuid());
        }
        evaluateInfo.setResponseinfo(evaluateInfoEntity.getResponseinfo());

        evaluateInfo.setGdzt(evaluateInfoEntity.getGdzt());//归档状态
        return evaluateInfo;
    }

    // ScheduleApplyInfoForSync -> Application
    public static Application toApplyEntity(ScheduleApplyInfoForSync apply) {
        if (apply == null) {
            return null;
        }

        Application application = new Application();
        application.setId(apply.getUuId());
        application.setApplyNo(apply.getApplyNo());
        application.setOrgId(apply.getOrgId());
        application.setRelaNo(apply.getRelaNo());

        // 申请人
        User coordinator = new User();
        coordinator.setEid(apply.getUserId());
        coordinator.setUserId(apply.getUserId());
        application.setCoordinator(coordinator);
        // 调度员
        User dispatcher = new User();
        dispatcher.setEid(apply.getSendUser());
        dispatcher.setUserId(apply.getSendUser());
        application.setDispatcher(dispatcher);
        // 起点
        application.setOrigin(apply.getStartPoint());
        // 终点
        application.setDestination(apply.getWays());
        // 开始时间
        application.setStart(new DateTime(apply.getBeginTime()));
        // 结束时间
        application.setEnd(new DateTime(apply.getEndTime()));
        User passenger = new User();
        passenger.setEid(apply.getPersonId());
        application.setPassenger(passenger);
        // 用车人数量
        application.setPassengers(apply.getPeopleNum());
        // 原因 未指定
        if (ReasonType.typeOf(apply.getReason()).equals(ReasonType.NULL)) {
            application.setReasonMs(apply.getReason());
        } else {
            KeyValue reason = new KeyValue();
            reason.setKey(apply.getReason());
            reason.setValue(ReasonType.typeOf(apply.getReason()).name());
            application.setReason(reason);
        }

        // 申请单状态
        KeyValue status = new KeyValue();
        status.setKey(apply.getStatus());
        application.setStatus(status);

        application.setCargoes(apply.getCargodes());
        application.setComment(apply.getRemark());
        application.setCreationTime(new DateTime(apply.getApplyDate()));
        application.setUserType(apply.getUsetype());

        return application;
    }

    // ApproveSugForSync -> TBusApproveSugEntity
    public static TBusApproveSugEntity toApproveEntity(ApproveSugForSync applyInfo) {
        if (applyInfo == null) {
            return null;
        }

        TBusApproveSugEntity approveSugEntity = new TBusApproveSugEntity();
        approveSugEntity.setUuid(applyInfo.getUuid());
        if (applyInfo.getApplyNo() != null) {
            TBusApplyinfoEntity application = new TBusApplyinfoEntity();
            application.setApplyno(applyInfo.getApplyNo());
            approveSugEntity.setApplication(application);
        }
        if (applyInfo.getUserId() != null) {
            TSysUserEntity user = new TSysUserEntity();
            user.setUserid(applyInfo.getUserId());
            approveSugEntity.setUser(user);
        }
        approveSugEntity.setSuggest(applyInfo.getSuggest());
        approveSugEntity.setRemark(applyInfo.getRemark());
        approveSugEntity.setOperatedate(new Timestamp(applyInfo.getOperateDate().getTime()));

        return approveSugEntity;
    }

    public static TBusScheduleCarEntity getScheduleCarInfo(ScheduleCarForSync scheduleCar, List<TBusScheduleRelaEntity> scheduleRelaList) {
        TBusScheduleCarEntity entity = new TBusScheduleCarEntity();
        entity.setUuid(scheduleCar.getUuId());
        if (scheduleRelaList == null || scheduleRelaList.isEmpty()) {
            TBusScheduleRelaEntity scheduleEntity = new TBusScheduleRelaEntity();
            scheduleEntity.setUuid(scheduleCar.getRelaNo());
            entity.setSchedule(scheduleEntity);
        } else {
            for (TBusScheduleRelaEntity scheduleEntity : scheduleRelaList) {
                if (scheduleEntity.getUuid().equals(scheduleCar.getRelaNo())) {
                    entity.setSchedule(scheduleEntity);
                    break;
                }
            }
        }
        TAzCarinfoEntity car = new TAzCarinfoEntity();
        car.setId(scheduleCar.getCarId());
        entity.setCar(car);
        TRsDriverinfoEntity driver = new TRsDriverinfoEntity();
        driver.setUuid(scheduleCar.getDriverId());
        entity.setDriver(driver);

        entity.setSchcarno(scheduleCar.getSchCarNo());
        entity.setStatus(ScheduleStatus.valueOf(Integer.valueOf(scheduleCar.getStatus())).id());

        return entity;
    }

    // SchedulerelaForSync -> TBusScheduleRelaEntity
    public static TBusScheduleRelaEntity toCommonBean(SchedulerelaForSync schedule, List<TBusApplyinfoEntity> applyInfoEntityList) {
        if (schedule == null) {
            return null;
        }

        TBusScheduleRelaEntity scheduleRelaEntity = new TBusScheduleRelaEntity();
        scheduleRelaEntity.setUuid(schedule.getUuId());
        scheduleRelaEntity.setStatus(schedule.getStatus());
        scheduleRelaEntity.setStarttime(new DateTime(schedule.getStartTime()));
        scheduleRelaEntity.setEndtime(new DateTime(schedule.getEndTime()));
        scheduleRelaEntity.setStartpoint(schedule.getStartPoint());
        scheduleRelaEntity.setWays(schedule.getWays());
        scheduleRelaEntity.setPeoplenum(schedule.getPeoplenum());
        scheduleRelaEntity.setPeoplenum(schedule.getPeoplenum());
        TSysOrgEntity fleet = new TSysOrgEntity();
        fleet.setOrgid(schedule.getSscd());
        scheduleRelaEntity.setFleet(fleet);
        scheduleRelaEntity.setApplications(applyInfoEntityList);
        TSysUserEntity sender = new TSysUserEntity();
        sender.setUserid(schedule.getSenduser());
        scheduleRelaEntity.setSenduser(sender);
        TSysUserEntity reciveuser = new TSysUserEntity();
        reciveuser.setUserid(schedule.getReciveuser());
        scheduleRelaEntity.setReciveuser(reciveuser);

        return scheduleRelaEntity;
    }

    // SyncScheduleCar -> TBusScheduleCarEntity
    public static TBusScheduleCarEntity toEntity(SyncScheduleCar syncScheduleCar) {
        TBusScheduleCarEntity scheduleCarEntity = new TBusScheduleCarEntity();
        scheduleCarEntity.setUuid(syncScheduleCar.getUuid());
        TAzCarinfoEntity carinfoEntity = new TAzCarinfoEntity();
        carinfoEntity.setId(syncScheduleCar.getCarId());
        scheduleCarEntity.setCar(carinfoEntity);
        // 驾驶员
        TRsDriverinfoEntity driverinfoEntity = new TRsDriverinfoEntity();
        driverinfoEntity.setDriverno(syncScheduleCar.getDriverId());
        driverinfoEntity.setUuid(syncScheduleCar.getDriverId());
        scheduleCarEntity.setDriver(driverinfoEntity);

        return scheduleCarEntity;
    }

    // TBusScheduleCarEntity -> SyncScheduleCar
    public static SyncScheduleCar fromEntity(TBusScheduleCarEntity scheduleCarEntity) {
        SyncScheduleCar syncScheduleCar = new SyncScheduleCar();
        syncScheduleCar.setUuid(scheduleCarEntity.getUuid());
        if (scheduleCarEntity.getCar() != null) {
            syncScheduleCar.setCarId(scheduleCarEntity.getCar().getId());
        }
        // 驾驶员
        if (scheduleCarEntity.getDriver() != null) {
            syncScheduleCar.setDriverId(scheduleCarEntity.getDriver().getUuid());
        }

        return syncScheduleCar;
    }

    // TBusScheduleRelaEntity -> SchedulerelaForSync
    public static SchedulerelaForSync fromRelaEntity(TBusScheduleRelaEntity scheduleRelaEntity) {
        SchedulerelaForSync schedulerelaForSync = new SchedulerelaForSync();
        schedulerelaForSync.setUuId(scheduleRelaEntity.getUuid());  // 主键ID
        if (scheduleRelaEntity.getFleet() != null) {
            schedulerelaForSync.setSscd(scheduleRelaEntity.getFleet().getOrgid());        // 所属车队
        }
        if (scheduleRelaEntity.getStarttime() != null) {
            schedulerelaForSync.setStartTime(scheduleRelaEntity.getStarttime().toDate());     // 派车时间
        }
        if (scheduleRelaEntity.getEndtime() != null) {
            schedulerelaForSync.setEndTime(scheduleRelaEntity.getEndtime().toDate());       // 派车预计结束时间
        }
        if (scheduleRelaEntity.getSenduser() != null) {
            schedulerelaForSync.setSenduser(scheduleRelaEntity.getSenduser().getUserid());    // 发调人
        }
        if (scheduleRelaEntity.getReciveuser() != null) {
            schedulerelaForSync.setReciveuser(scheduleRelaEntity.getReciveuser().getUserid()); // 收调人
        }
        schedulerelaForSync.setStatus(scheduleRelaEntity.getStatus());     // 当前状态   0:未生效 1:待出车  2:返回    3: 取消调度
        schedulerelaForSync.setStartPoint(scheduleRelaEntity.getStartpoint());  //出发地点
        schedulerelaForSync.setWays(scheduleRelaEntity.getWays());         //行车路线
        schedulerelaForSync.setPeoplenum(scheduleRelaEntity.getPeoplenum());  //乘车人数
        return schedulerelaForSync;
    }

    // TBusScheduleCarEntity -> ScheduleCarForSync
    public static ScheduleCarForSync fromCarEntity(TBusScheduleCarEntity carEntity) {
        ScheduleCarForSync scheduleCarForSync = new ScheduleCarForSync();
        scheduleCarForSync.setUuId(carEntity.getUuid());        // UUID
        if (carEntity.getSchedule() != null) {
            scheduleCarForSync.setRelaNo(carEntity.getSchedule().getUuid());      // 调度分配编号
        }
        if (carEntity.getCar() != null) {
            scheduleCarForSync.setCarId(carEntity.getCar().getId());       // 车辆ID
            scheduleCarForSync.setCphm(carEntity.getCar().getCphm());
        }
        if (carEntity.getDriver() != null) {
            scheduleCarForSync.setDriverId(carEntity.getDriver().getUuid());    // 驾驶员ID
            scheduleCarForSync.setDriverName(carEntity.getDriver().getDrivername());    // 驾驶员ID
            scheduleCarForSync.setDriverMobile(carEntity.getDriver().getMobile());    // 驾驶员ID
        }
        scheduleCarForSync.setSchCarNo(carEntity.getSchcarno());    // 派车单编号
        scheduleCarForSync.setStatus(carEntity.getStatus());     // 当前状态
        return scheduleCarForSync;
    }

    // scheduleRelaEntity -> List<ScheduleCarForSync>
    public static List<ScheduleCarForSync> fromCarEntity(TBusScheduleRelaEntity scheduleRelaEntity) {
        List<ScheduleCarForSync> scheduleCarForSyncList = new ArrayList<>();
        for (TBusScheduleCarEntity carEntity : scheduleRelaEntity.getScheduleCars()) {
            scheduleCarForSyncList.add(fromCarEntity(carEntity));
        }
        return scheduleCarForSyncList;
    }

    public static List<SyncSchedule> toScheduleInfo(List<TBusScheduleRelaEntity> scheduleRelaEntityList) {
        if(scheduleRelaEntityList == null) {
            return null;
        }
        List<SyncSchedule> syncScheduleList = new ArrayList<>();
        for(TBusScheduleRelaEntity scheduleRelaEntity : scheduleRelaEntityList) {
            syncScheduleList.add(toScheduleInfo(scheduleRelaEntity));
        }
        return syncScheduleList;
    }
    public static SyncSchedule toScheduleInfo(TBusScheduleRelaEntity scheduleRelaEntity) {
        SyncSchedule syncSchedule = new SyncSchedule();
        if(scheduleRelaEntity.getApplications() != null) {
            syncSchedule.setApply(fromApplyEntity(scheduleRelaEntity.getApplications()));
        }
        syncSchedule.addRela(fromRelaEntity(scheduleRelaEntity));
        if(scheduleRelaEntity.getScheduleCars() != null) {
            syncSchedule.setCar(fromCarEntity(scheduleRelaEntity));
        }
        return syncSchedule;
    }

    public static SyncSchedule fromEntity(TBusApplyinfoEntity applyinfoEntity) {
        SyncSchedule syncSchedule = new SyncSchedule();
        List<ScheduleApplyInfoForSync> applyInfoList = new ArrayList();
        applyInfoList.add(fromApplyEntity(applyinfoEntity));
        syncSchedule.setApply(applyInfoList);
        if (applyinfoEntity.getSchedule() != null) {
            syncSchedule.addRela(fromRelaEntity(applyinfoEntity.getSchedule()));
            syncSchedule.setCar(fromCarEntity(applyinfoEntity.getSchedule()));
        }
        return syncSchedule;
    }

    // TBusApplyinfoEntity -> ScheduleApplyInfoForSync
    public static List<ScheduleApplyInfoForSync> fromApplyEntity(List<TBusApplyinfoEntity> applyinfoEntityList) {
        List<ScheduleApplyInfoForSync> applyInfoForSyncList = new ArrayList<>();
        for (TBusApplyinfoEntity applyinfoEntity : applyinfoEntityList) {
            applyInfoForSyncList.add(fromApplyEntity(applyinfoEntity));
        }
        return applyInfoForSyncList;
    }

    // TBusApplyinfoEntity -> ScheduleApplyInfoForSync
    public static ScheduleApplyInfoForSync fromApplyEntity(TBusApplyinfoEntity applyinfoEntity) {
        ScheduleApplyInfoForSync applyInfoForSync = new ScheduleApplyInfoForSync();
        applyInfoForSync.setUuId(applyinfoEntity.getUuid());
        applyInfoForSync.setApplyNo(applyinfoEntity.getApplyno());            // 申请单NO
        if (applyinfoEntity.getCoordinator() != null) {
            applyInfoForSync.setUserId(applyinfoEntity.getCoordinator().getUserid());             // 申请者
        }
        if (applyinfoEntity.getDepartment() != null) {
            applyInfoForSync.setOrgId(applyinfoEntity.getDepartment().getOrgid());               // 部门
        }

        applyInfoForSync.setStartPoint(applyinfoEntity.getStartpoint());          // 起始点
        applyInfoForSync.setWays(applyinfoEntity.getWays());                // 终止点
        applyInfoForSync.setCargodes(applyinfoEntity.getCargodes());           // 货物描述
        applyInfoForSync.setReason(applyinfoEntity.getReason());              // 原因-事宜
        applyInfoForSync.setRemark(applyinfoEntity.getRemark());              // 备注
        if (applyinfoEntity.getBegintime() != null) {
            applyInfoForSync.setBeginTime(applyinfoEntity.getBegintime().toDate());             // 计划开始日期
        }
        if (applyinfoEntity.getEndtime() != null) {
            applyInfoForSync.setEndTime(applyinfoEntity.getEndtime().toDate());               // 计划结束日期
        }
        if (applyinfoEntity.getApplydate() != null) {
            applyInfoForSync.setApplyDate(applyinfoEntity.getApplydate().toDate());             // 申请日期
        }
        applyInfoForSync.setIsGoods(applyinfoEntity.getIsgoods());             // 是否有货
        if (applyinfoEntity.getSenduser() != null) {
            applyInfoForSync.setSendUser(applyinfoEntity.getSenduser().getUserid());            // 车队长/片队
        }
        if (applyinfoEntity.getFleet() != null) {
            applyInfoForSync.setSscd(applyinfoEntity.getFleet().getOrgid());            // 车队长/片队
        }
        if (applyinfoEntity.getSchedule() != null) {
            applyInfoForSync.setRelaNo(applyinfoEntity.getSchedule().getUuid());              // 分配编号
        }
        applyInfoForSync.setPeopleNum(applyinfoEntity.getPeoplenum());          // 人数
        if (applyinfoEntity.getPassenger() != null) {
            applyInfoForSync.setPersonId(applyinfoEntity.getPassenger().getUuid());            // 用车人
            applyInfoForSync.setPersonName(applyinfoEntity.getPassenger().getName());            // 用车人
            applyInfoForSync.setPersonMobile(applyinfoEntity.getPassenger().getMobile());            // 用车人
            applyInfoForSync.setPhone(applyinfoEntity.getPassenger().getPhone());            // 用车人
            if (applyinfoEntity.getPassenger().getSysOrg() != null) {
                applyInfoForSync.setOrgName(applyinfoEntity.getPassenger().getSysOrg().getOrgname());            // 用车人
            }
        }
        applyInfoForSync.setStatus(applyinfoEntity.getStatus());              // 申请单状态
        applyInfoForSync.setRemark(applyinfoEntity.getRemark());
        applyInfoForSync.setUsetype(applyinfoEntity.getUsetype());
        return applyInfoForSync;
    }

    // SyncLeave -> TRsKqglEntity
    public static TRsKqglEntity toKqEntity(SyncLeave syncLeave) {
        TRsKqglEntity kqglEntity = new TRsKqglEntity();
        int date = syncLeave.getDate().getDayOfMonth();

        String dateStr = ("0" + date);
        dateStr = dateStr.substring(dateStr.length() - 2);
        String setterName = "setKQ" + dateStr;
//        invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"0"});

        String monthStr = ("0" + syncLeave.getDate().getMonthOfYear());
        monthStr = monthStr.substring(monthStr.length() - 2);
        // 考勤月份
        kqglEntity.setKqDate(String.valueOf(syncLeave.getDate().getYear()) + monthStr);
        if ("病假".equals(syncLeave.getLeaveType().name())) {
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"1"});
        } else if ("事假".equals(syncLeave.getLeaveType().name())) {
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"2"});
        } else if ("年假".equals(syncLeave.getLeaveType().name())) {
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"3"});
        } else if ("加班".equals(syncLeave.getLeaveType().name())) {
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"4"});
        } else if ("拖班".equals(syncLeave.getLeaveType().name())) {
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"5"});
        } else if ("抢修".equals(syncLeave.getLeaveType().name())) {
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"6"});
        } else if ("公休".equals(syncLeave.getLeaveType().name())) {
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"7"});
        } else if ("调修".equals(syncLeave.getLeaveType().name())) {
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"8"});
        } else if ("出差".equals(syncLeave.getLeaveType().name())) {
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"9"});
        } else if ("通宵班".equals(syncLeave.getLeaveType().name())) {
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"a"});
        } else {    // 默认为出勤
            invoke(kqglEntity, setterName, new Class[]{String.class}, new String[]{"0"});
        }
        return kqglEntity;
    }

    // SyncMainUser -> TBusMainUserInfoEntity
    public static TBusMainUserInfoEntity toEntity(SyncMainUser mainUser) {
        if (mainUser == null) {
            return null;
        }
        TBusMainUserInfoEntity mainUserInfoEntity = new TBusMainUserInfoEntity();
        mainUserInfoEntity.setUuid(mainUser.getMainUserId());
        mainUserInfoEntity.setName(mainUser.getName());
        mainUserInfoEntity.setMobile(mainUser.getMobile());
        mainUserInfoEntity.setPhone(mainUser.getPhone());
        mainUserInfoEntity.setBelongGroup(mainUser.getBelongGroup());
        if (mainUser.getBmid() != null) {
            TSysOrgEntity sscd = new TSysOrgEntity();
            sscd.setOrgid(mainUser.getBmid());
            mainUserInfoEntity.setSysOrg(sscd);
        }

        return mainUserInfoEntity;
    }

    public static Depot fromEntity(TEmbeddedDepot embeddedDepot) {
        if (embeddedDepot == null) {
            return null;
        }
        Depot depot = new Depot();
        depot.setLng(embeddedDepot.getLongitude());
        depot.setLat(embeddedDepot.getLatitude());
        depot.setName(embeddedDepot.getName());
        return depot;
    }


    // 调用 反射的形式进行设置数据
    private static void invoke(Object instance, String methodName, Class[] parameterTypes, Object[] parameterValues) {
        try {
            Method method = instance.getClass().getMethod(methodName, parameterTypes);
            method.invoke(instance, parameterValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<SyncRecord> fromEntity(List<TBusRecordinfoEntity> recordinfoEntityList) {
        List<SyncRecord> recordList = new ArrayList<>();
        if (recordinfoEntityList != null) {
            for (TBusRecordinfoEntity recordinfoEntity : recordinfoEntityList) {
                recordList.add(fromEntity(recordinfoEntity));
            }
        }
        return recordList;
    }

    public static SyncRecord fromEntity(TBusRecordinfoEntity recordinfoEntity) {
        SyncRecord record = new SyncRecord();
        record.setId(recordinfoEntity.getUuid());
        if (recordinfoEntity.getScheduleCar() != null) {
            record.setSchedule(recordinfoEntity.getScheduleCar().getUuid());
        }
        if (recordinfoEntity.getCar() != null) {
            record.setCar(recordinfoEntity.getCar().getId());
        }
        if (recordinfoEntity.getDriver() != null) {
            record.setDriver(recordinfoEntity.getDriver().getUuid());
        }
        record.setStarttime(recordinfoEntity.getStarttime());
        record.setEndtime(recordinfoEntity.getEndtime());
        record.setStartmile(recordinfoEntity.getStartmile());
        record.setEndmile(recordinfoEntity.getEndmile());
        record.setMileage(recordinfoEntity.getMileage());
        record.setTrancost(recordinfoEntity.getTrancost());
        record.setStaycost(recordinfoEntity.getStaycost());
        record.setStopcost(recordinfoEntity.getStopcost());
        record.setRefuelcost(recordinfoEntity.getRefuelcost());
        record.setRemark(recordinfoEntity.getRemark());
//        if (recordinfoEntity.getOperateuser() != null) {      operateuser is driver
//            record.setOperateuser(recordinfoEntity.getOperateuser().getUserid());
//        }
        record.setOperatedate(recordinfoEntity.getOperatedate());
        record.setUpdatedate(recordinfoEntity.getUpdatedate());
        if (recordinfoEntity.getSchedule() != null) {
            record.setRelano(recordinfoEntity.getSchedule().getUuid());
        }
        record.setCostsum(recordinfoEntity.getCostsum());
        return record;
    }

    public static ApproveSugForSync toApproveInfo(TBusApproveSugEntity entity) {
        ApproveSugForSync approveSugForSync = new ApproveSugForSync();
        approveSugForSync.setUuid(entity.getUuid());
        if (entity.getApplication() != null) {
            approveSugForSync.setApplyNo(entity.getApplication().getApplyno());
        }
        if (entity.getUser() != null) {
            approveSugForSync.setUserId(entity.getUser().getUserid());
        }
        approveSugForSync.setSuggest(entity.getSuggest());
        approveSugForSync.setRemark(entity.getRemark());
        approveSugForSync.setOperateDate(entity.getOperatedate());
        return approveSugForSync;
    }

    public static List<TBusBusinessRelaEntity> toEntityList(SyncBusinessRelation syncBusinessRelation) {
        if (syncBusinessRelation == null) {
            return null;
        }
        List<TBusBusinessRelaEntity> entityList = new ArrayList<>();
        if (syncBusinessRelation.getBusinessOrgIds() != null) {
            for (String businessOrgId : syncBusinessRelation.getBusinessOrgIds()) {
                TBusBusinessRelaEntity entity = new TBusBusinessRelaEntity();
                if (syncBusinessRelation.getUuid() != null) {
                    entity.setUuid(syncBusinessRelation.getUuid());
                }
                TSysOrgEntity busOrg = new TSysOrgEntity();
                busOrg.setOrgid(businessOrgId);
                entity.setBusOrg(busOrg);
                TSysOrgEntity fleet = new TSysOrgEntity();
                fleet.setOrgid(syncBusinessRelation.getOrgId());
                entity.setFleet(fleet);
                TSysUserEntity supervisor = new TSysUserEntity();
                supervisor.setUserid(syncBusinessRelation.getUserId());
                entity.setSupervisor(supervisor);
                if (StringUtils.isNotBlank(syncBusinessRelation.getModel())) {
                    entity.setMagmodel(syncBusinessRelation.getModel());
                } else {    // 默认为 1   1：统一调度；2：自行管理；
                    entity.setMagmodel("1");
                }
                entityList.add(entity);
            }
        } else {
            TBusBusinessRelaEntity entity = new TBusBusinessRelaEntity();
            TSysUserEntity supervisor = new TSysUserEntity();
            supervisor.setUserid(syncBusinessRelation.getUserId());
            entity.setSupervisor(supervisor);
            if (StringUtils.isNotBlank(syncBusinessRelation.getModel())) {
                entity.setMagmodel(syncBusinessRelation.getModel());
            } else {    // 默认为 1   1：统一调度；2：自行管理；
                entity.setMagmodel("1");
            }
            entityList.add(entity);
        }
        return entityList;
    }

    public static List<String> fromDataEntity(List<TSyncDataStatusEntity> entityList) {
        List<String> resultList = new ArrayList<>();
        for (TSyncDataStatusEntity entity : entityList) {
            resultList.add(entity.getDataValue());
        }
        return resultList;
    }
}
