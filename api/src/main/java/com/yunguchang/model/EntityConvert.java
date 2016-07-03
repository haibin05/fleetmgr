package com.yunguchang.model;

import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.*;
import com.yunguchang.model.sync.SyncResult;
import com.yunguchang.sharetome.ShareTomeEvent;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;

import static com.yunguchang.sharetome.ShareTomeMessage.*;

/**
 * Created by 禕 on 2015/9/11.
 */
public class EntityConvert {

    public static final HanyuPinyinOutputFormat HANYU_PINYIN_OUTPUT_FORMAT = new HanyuPinyinOutputFormat();

    public static Car fromEntity(TAzCarinfoEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }
        Car car = new Car();
        car.setId(entity.getId());
        car.setBadge(entity.getCphm());
        car.setCapacity(entity.getHdzk());
        car.setFleet(fromEntity(entity.getSysOrg()));

        car.setModel(entity.getModel());
        car.setCarState(getCarStatus(entity));

        car.setLicenseCarType(entity.getXszcx());

        car.setDriver(fromEntity(entity.getDriver()));
        car.setVolition(entity.getViolationTimes() == null ? 0 : entity.getViolationTimes());

        TEmbeddedDepot embeddedDepot = entity.getDepot();
        if (embeddedDepot != null) {
            Depot depot = new Depot();
            depot.setName(embeddedDepot.getName());
            depot.setLat(embeddedDepot.getLatitude());
            depot.setLng(embeddedDepot.getLongitude());
            car.setDepot(depot);
        }
        return car;
    }

    public static CarState getCarStatus(TAzCarinfoEntity entity) {
        CarState carState = CarState.IDLE;
        if (entity.getRepairingState() != null && entity.getRepairingState() != RepairingState.NONE.id()) {
            if (entity.getRepairingState() == RepairingState.REQUESTED.id()) {
                carState = CarState.AWAITING_REPAIRE;
            } else if (entity.getRepairingState() == RepairingState.ONGOING.id()) {
                carState = CarState.REPAIRING;
            }
        } else if (Boolean.TRUE.equals(entity.getInusing())) {
            carState = CarState.INUSE;
        }
        return carState;
    }

    public static Fleet fromEntity(TSysOrgEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }
        Fleet fleet = new Fleet();
        fleet.setId(entity.getOrgid());
        fleet.setName(entity.getOrgname());

        return fleet;
    }

    public static AlarmEvent fromEntity(TBusAlarmEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }

        AlarmEvent alarmEvent = new AlarmEvent();
        alarmEvent.setId(entity.getId());
        Car car = fromEntity(entity.getCar());
        alarmEvent.setCar(car);
        alarmEvent.setAlarm(Alarm.valueOf(entity.getAlarm()));
        alarmEvent.setStart(entity.getStart());
        alarmEvent.setEnd(entity.getEnd());

        GpsPoint gpsPoint = new GpsPoint();
        gpsPoint.setCar(car);

        if (entity.getGpsLat() != null && entity.getGpsLng() != null && entity.getGpsSpeed() != null) {
            gpsPoint.setSpeed(entity.getGpsSpeed());
            gpsPoint.setLat(entity.getGpsLat());
            gpsPoint.setLng(entity.getGpsLng());
            gpsPoint.setSampleTime(entity.getGpsSampleTime());
            alarmEvent.setGpsPoint(gpsPoint);
        }

        return alarmEvent;
    }

    public static TSysOrgEntity toEntity(Fleet obj) {
        if (obj == null) {
            return null;
        }
        if(!Hibernate.isInitialized(obj)){
            Hibernate.initialize(obj);
        }

        TSysOrgEntity entity = new TSysOrgEntity();
        entity.setOrgid(obj.getId());
        entity.setOrgname(obj.getName());

        return entity;
    }

    public static TBusApplyinfoEntity toEntity(Application obj, String... outId) {
        if (obj == null) {
            return null;
        }
        TBusApplyinfoEntity entity = new TBusApplyinfoEntity();
        if (obj.getId() != null) {
            entity.setUuid(obj.getId());
        }
        entity.setCoordinator(toEntity(obj.getCoordinator()));
        entity.setPassenger(toMainUserEntity(obj.getPassenger()));
        entity.setSenduser(toEntity(obj.getDispatcher()));
        entity.setApplyno(obj.getApplyNo());
        TSysOrgEntity department = new TSysOrgEntity();
        department.setOrgid(obj.getOrgId());
        entity.setDepartment(department);
        entity.setSchedule(toEntity(obj.getSchedule()));
        entity.setStartpoint(obj.getOrigin());
        entity.setWays(obj.getDestination());
        entity.setPeoplenum(obj.getPassengers());
        if(obj.getReason() != null) {
            entity.setReasonCode(obj.getReason().getKey());
            entity.setReasonText(obj.getReason().getValue());
            entity.setReason(obj.getReason().getKey());
        }
        entity.setReasonms(obj.getReasonMs());
        //新创建申请单时,没有状态字段.状态有服务器端维护
        if (obj.getStatus() != null && null != obj.getStatus().getKey()) {
            entity.setStatusCode(obj.getStatus().getKey());
            entity.setStatusText(obj.getStatus().getValue());
            entity.setStatus(ApplyStatus.valueOf(Integer.valueOf(obj.getStatus().getKey())).toStringValue());
        }
        entity.setCargodes(obj.getCargoes());
        entity.setRemark(obj.getComment());
        entity.setBegintime(obj.getStart());
        entity.setEndtime(obj.getEnd());
        entity.setIssend(obj.getIsSend());
        entity.setSfgf((obj.isRegular() != null && obj.isRegular()) ? "1" : "0");
        entity.setBgfxx(obj.getIrregularReason());
        entity.setUsetype(obj.getUserType());
        if (outId != null && outId.length == 1 && StringUtils.isNotEmpty(outId[0])) {
            entity.setOutid(outId[0]);
        }
        return entity;
    }

    public static TSysUserEntity toEntity(User obj) {
        if (obj == null) {
            return null;
        }
        if(!Hibernate.isInitialized(obj)){
            Hibernate.initialize(obj);
        }
        TSysUserEntity entity = new TSysUserEntity();
        entity.setUserid(obj.getEid() == null ? obj.getUserId() : obj.getEid());
        entity.setUserno(obj.getEid() == null ? obj.getUserId() : obj.getEid());
        entity.setMobile(obj.getMobile());
        entity.setUsername(obj.getUserName());
        TSysOrgEntity orgEntity = new TSysOrgEntity();
        orgEntity.setOrgid(obj.getOrgId());
        entity.setDepartment(orgEntity);
        return entity;
    }

    public static TBusMainUserInfoEntity toMainUserEntity(User obj) {
        if (obj == null) {
            return null;
        }
        if(!Hibernate.isInitialized(obj)){
            Hibernate.initialize(obj);
        }
        TBusMainUserInfoEntity entity = new TBusMainUserInfoEntity();
        entity.setUuid(obj.getEid() == null ? obj.getUserId() : obj.getEid());
        entity.setMobile(obj.getMobile());
        entity.setName(obj.getUserName());
        TSysOrgEntity orgEntity = new TSysOrgEntity();
        orgEntity.setOrgid(obj.getOrgId());
        entity.setSysOrg(orgEntity);
        return entity;
    }

    public static TBusScheduleRelaEntity toEntity(Schedule obj) {
        if (obj == null) {
            return null;
        }
        TBusScheduleRelaEntity entity = new TBusScheduleRelaEntity();
        // ....
        entity.setUuid(obj.getId());
        entity.setStarttime(obj.getStart());
        entity.setEndtime(obj.getEnd());
        entity.setStartpoint(obj.getStartPoint());
        entity.setWays(obj.getWayPoint());
        entity.setStatus(obj.getStatus());
        entity.setSenduser(toEntity(obj.getSender()));
        entity.setReciveuser(toEntity(obj.getReceiver()));
        return entity;
    }

    public static ScheduleCar fromEntity(TBusScheduleCarEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }
        ScheduleCar scheduleCar = new ScheduleCar();
        scheduleCar.setCar(fromEntity(entity.getCar()));
        scheduleCar.setDriver(fromEntity(entity.getDriver()));
        scheduleCar.setId(entity.getUuid());
        scheduleCar.setStatus(ScheduleStatus.valueOf(entity.getStatus()));
        scheduleCar.setSchedule(fromEntityWithoutScheduleCars(entity.getSchedule()));

        if (Hibernate.isInitialized(entity.getRecord()) && entity.getRecord() != null) {
            Double startMile = entity.getRecord().getStartmile();
            Double endMile = entity.getRecord().getEndmile();
            if(startMile != null) {
                scheduleCar.setStartMiles(startMile);
            } else {
                startMile = 0d;
            }
            if (endMile != null) {
                scheduleCar.setKm(endMile - startMile);
                scheduleCar.setEndMiles(endMile);
            }
        }

        return scheduleCar;
    }

    /**
     * 这个函数不会返回嵌套的Apply
     *
     * @param entity
     * @return
     */
    public static Schedule fromEntityWithoutApply(TBusScheduleRelaEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }

        Schedule schedule = new Schedule();
        schedule.setStart(entity.getStarttime());
        schedule.setEnd(entity.getEndtime());
        schedule.setStartPoint(entity.getStartpoint());
        schedule.setWayPoint(entity.getWays());
        schedule.setId(entity.getUuid());
        schedule.setSender(fromEntity(entity.getSenduser()));
        schedule.setReceiver(fromEntity(entity.getReciveuser()));
        schedule.setStatus(entity.getStatus());
        return schedule;

    }
    /**
     * 这个函数不会返回嵌套的ScheduleCar
     *
     * @param entity
     * @return
     */
    public static Schedule fromEntityWithoutScheduleCars(TBusScheduleRelaEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }

        Schedule schedule = new Schedule();
        schedule.setStart(entity.getStarttime());
        schedule.setEnd(entity.getEndtime());
        schedule.setStartPoint(entity.getStartpoint());
        schedule.setWayPoint(entity.getWays());
        schedule.setId(entity.getUuid());
        if (Hibernate.isInitialized(entity.getApplications())) {
            List<Application> applications = new ArrayList<>();
            for (TBusApplyinfoEntity applyinfoEntity : entity.getApplications()) {
                Application application = fromEntity(applyinfoEntity);
                if (application != null) {
                    applications.add(application);
                }
                schedule.setApplications(applications.toArray(new Application[]{}));
            }
        }
        schedule.setSender(fromEntity(entity.getSenduser()));
        schedule.setReceiver(fromEntity(entity.getReciveuser()));
        schedule.setStatus(entity.getStatus());
        return schedule;

    }

    /**
     * 这个函数同时返回回嵌套的ScheduleCar
     *
     * @param entity
     * @return
     */
    public static Schedule fromEntityWithScheduleCars(TBusScheduleRelaEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }

        Schedule schedule = fromEntityWithoutScheduleCars(entity);
        List<ScheduleCar> scheduleCars = new ArrayList<>();
        if (entity.getScheduleCars() != null) {
            for (TBusScheduleCarEntity scheduleCarEntity : entity.getScheduleCars()) {
                if (scheduleCarEntity.getStatus().equals(ScheduleStatus.CANCELED.id()) || scheduleCarEntity.getStatus().equals(ScheduleStatus.NOT_IN_EFFECT.id())) {
                    continue;
                }
                //we can't set schedule to the car again.
                //it causes an infinite recursion excpetion serialize as json
                ScheduleCar scheduleCar = fromEntity(scheduleCarEntity);
                scheduleCars.add(scheduleCar);
            }
        }
        schedule.setScheduleCars(scheduleCars.toArray(new ScheduleCar[]{}));
        if (entity.getApplications() != null && Hibernate.isInitialized(entity.getApplications())) {
            List<Application> applicationList = new ArrayList<>();

            for (TBusApplyinfoEntity applyinfoEntity : entity.getApplications()) {
                //we can't set schedule to the car again.
                //it causes an infinite recursion excpetion serialize as json
                Application application = fromEntity(applyinfoEntity);
                applicationList.add(application);
            }
            schedule.setApplications(applicationList.toArray(new Application[]{}));

        }
        schedule.setId(entity.getUuid());
        return schedule;

    }

    public static ReturningDepotEvent fromEntity(TBusReturningDepotEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }

        ReturningDepotEvent returningDepotEvent = new ReturningDepotEvent();
        returningDepotEvent.setCar(fromEntity(entity.getCar()));
        returningDepotEvent.getCar().setDriver(fromEntity(entity.getCar().getDriver()));
        returningDepotEvent.setEventTime(entity.getReturnTime());
        return returningDepotEvent;
    }

    public static Driver fromEntity(TRsDriverinfoEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }
        Driver driver = new Driver();
        driver.setId(entity.getUuid());
        driver.setMobile(entity.getMobile());
        driver.setPhone(entity.getPhone());
        String drivername = entity.getDrivername();
        driver.setName(drivername);
        driver.setNamePinyin(getPinYin(drivername));
        driver.setLicenseClass(entity.getLicenseClass());          // 驾照类别
        driver.setInternalLicenseClassCode(entity.getDrivecartype()); //准驾车型内部编码

        if (entity.getLeaveStatus() == null || OnWorkStateEnum.onWorkState.contains(entity.getLeaveStatus())) {
            if (entity.isOnWorking()) {
                driver.setDriverStatus(DriverStatus.WORK);
            } else {
                driver.setDriverStatus(DriverStatus.IDLE);
            }

        } else {
            driver.setDriverStatus(DriverStatus.LEAVE);
        }

        driver.setFleet(fromEntity(entity.getDepartment()));

        return driver;
    }

    public static LicenseVehicleMapping fromEntity(TAzJzRelaEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }
        LicenseVehicleMapping licenseVehicleMapping = new LicenseVehicleMapping();
        licenseVehicleMapping.setLicenseClass(entity.getJzyq());
        licenseVehicleMapping.setVehicleType(entity.getXszcx());
        return licenseVehicleMapping;
    }

    public static Depot fromEntity(TEmbeddedDepot embeddedDepot) {
        if (embeddedDepot == null) {
            return null;
        }
        if(!Hibernate.isInitialized(embeddedDepot)){
            Hibernate.initialize(embeddedDepot);
        }
        Depot depot = new Depot();
        depot.setLng(embeddedDepot.getLongitude());
        depot.setLat(embeddedDepot.getLatitude());
        depot.setName(embeddedDepot.getName());
        return depot;
    }

    public static KeyValue fromEntity(TSysDicEntity dicEntity) {
        if (dicEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(dicEntity)){
            Hibernate.initialize(dicEntity);
        }
        KeyValue keyValue = new KeyValue();
        keyValue.setKey(dicEntity.getDatacode());
        keyValue.setValue(dicEntity.getDatavalue());
        return keyValue;
    }

    public static Application fromEntity(TBusApplyinfoEntity applyinfoEntity) {
        if (applyinfoEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(applyinfoEntity)){
            Hibernate.initialize(applyinfoEntity);
        }
        Application application = new Application();
        application.setId(applyinfoEntity.getUuid());
        if (applyinfoEntity.getReasonCode() != null && applyinfoEntity.getReasonText() != null) {
            KeyValue keyValue = new KeyValue();
            keyValue.setKey(applyinfoEntity.getReasonCode());
            keyValue.setValue(applyinfoEntity.getReasonText());

            application.setReason(keyValue);

        }

        if (applyinfoEntity.getStatusCode() != null && applyinfoEntity.getStatusText() != null) {
            KeyValue keyValue = new KeyValue();
            keyValue.setKey(applyinfoEntity.getStatusCode());
            keyValue.setValue(applyinfoEntity.getStatusText());
            application.setStatus(keyValue);

        }

        application.setCargoes(applyinfoEntity.getCargodes());
        application.setComment(applyinfoEntity.getRemark());
        application.setPassenger(fromEntity(applyinfoEntity.getPassenger()));
        application.setStart(applyinfoEntity.getBegintime());
        application.setEnd(applyinfoEntity.getEndtime());
        application.setOrigin(applyinfoEntity.getStartpoint());
        application.setDestination(applyinfoEntity.getWays());
        application.setCoordinator(fromEntity(applyinfoEntity.getCoordinator()));
        application.setCreationTime(applyinfoEntity.getApplydate());
        application.setPassengers(applyinfoEntity.getPeoplenum());
        String isRegularString = applyinfoEntity.getSfgf();
        if (isRegularString.equals("1")) {
            application.setRegular(true);
        } else {
            application.setRegular(false);
            application.setIrregularReason(applyinfoEntity.getBgfxx());
        }
        application.setSchedule(fromEntityWithoutApply(applyinfoEntity.getSchedule()));

        return application;

    }

    public static User fromEntity(TBusMainUserInfoEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(userEntity)){
            Hibernate.initialize(userEntity);
        }
        User user = new User(User.UserType.PASSENGER);
        user.setUserId(userEntity.getUuid());
        String name = userEntity.getName().trim();

        user.setUserName(name);
        user.setNamePinyin(getPinYin(name));
        user.setMobile(userEntity.getMobile());
        return user;
    }

    private static String[] getPinYin(String name) {
        List<String> pinyin = new ArrayList<>(name.length());
        for (char c : name.toCharArray()) {
            String[] pinyinArray = null;
            try {
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, HANYU_PINYIN_OUTPUT_FORMAT);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {

            }
            if (pinyinArray == null) {
                pinyin.add(String.valueOf(c));
            } else {
                pinyin.add(pinyinArray[0]);
            }
        }
        return pinyin.toArray(new String[]{});
    }

    public static User fromEntity(TSysUserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(userEntity)){
            Hibernate.initialize(userEntity);
        }

        User user = new User(User.UserType.DISPATCHER);
        user.setUserId(userEntity.getUserid());
        String name = userEntity.getUsername().trim();
        user.setUserName(name);
        user.setNamePinyin(getPinYin(name));
        user.setMobile(userEntity.getMobile());
        user.setEid(userEntity.getUserno());
        return user;
    }

    public static User toSnsUser(TSysUserEntity userEntity, User.UserType newType) {
        if (userEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(userEntity)){
            Hibernate.initialize(userEntity);
        }

        User user = new User(newType);
        user.setUserId(userEntity.getUserno());
        if(userEntity.getUsername() != null) {
            String name = userEntity.getUsername().trim();
            user.setUserName(name);
            user.setNamePinyin(getPinYin(name));
        }
        user.setMobile(userEntity.getMobile());
        user.setEid(userEntity.getUserno());
        return user;
    }

    public static User toSnsUser(TRsDriverinfoEntity driverEntity) {
        if (driverEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(driverEntity)){
            Hibernate.initialize(driverEntity);
        }

        User user = new User(User.UserType.DRIVER);
        user.setUserId(driverEntity.getMobile());
        user.setEid(driverEntity.getUuid());
        String name = driverEntity.getDrivername().trim();
        user.setUserName(name);
        user.setNamePinyin(getPinYin(name));
        user.setMobile(driverEntity.getMobile());
        return user;
    }

    public static User fromEntityWithRoles(TSysUserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(userEntity)){
            Hibernate.initialize(userEntity);
        }

        User user = new User(User.UserType.DISPATCHER);
        user.setUserId(userEntity.getUserid());
        String name = userEntity.getUsername().trim();
        user.setUserName(name);
        user.setMobile(userEntity.getMobile());
        user.setEid(userEntity.getUserno());
        List<String> roles = new ArrayList<>();
        if(userEntity.getDepartment() != null) {
            user.setOrgId(userEntity.getDepartment().getOrgid());
        }
        for (TSysRoleEntity roleEntity : userEntity.getRoles()) {
//            if(roleEntity.getDeptid().equals(user.getOrgId()))
            roles.add(Roles.getSystemRole(roleEntity.getRolename()));
        }
        user.setRoles(roles.toArray(new String[]{}));
        return user;
    }

    public static Record fromEntity(TBusRecordinfoEntity recordEntity) {
        if (recordEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(recordEntity)){
            Hibernate.initialize(recordEntity);
        }
        Record record = new Record();
        record.setId(recordEntity.getUuid());
        record.setStart(recordEntity.getStarttime());
        record.setEnd(recordEntity.getEndtime());
        record.setStartMile(recordEntity.getStartmile());
        record.setEndMile(recordEntity.getEndmile());
        record.setFuel(recordEntity.getRefuelcost());
        record.setLodging(recordEntity.getStaycost());
        record.setParking(recordEntity.getStopcost());
        record.setTransport(recordEntity.getTrancost());
        record.setComment(recordEntity.getRemark());
        return record;
    }

    public static RateOfDriver fromEntityToRateOfDriver(TBusEvaluateinfoEntity rateEntity) {
        if (rateEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(rateEntity)){
            Hibernate.initialize(rateEntity);
        }
        RateOfDriver rateOfDriver = new RateOfDriver();
        rateOfDriver.setCarRate(rateEntity.getCarscore());
        rateOfDriver.setCarConcern(rateEntity.getCarreason());
        rateOfDriver.setDriverRate(rateEntity.getDriverscore());
        rateOfDriver.setDriverConcern(rateEntity.getDriverreason());
        rateOfDriver.setFleetRate(rateEntity.getTeamscore());
        rateOfDriver.setFleetConcern(rateEntity.getTeamreason());
        return rateOfDriver;
    }

    public static RateOfPassenger fromEntityToRateOfPassenger(TBusEvaluateinfoEntity rateEntity) {
        if (rateEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(rateEntity)){
            Hibernate.initialize(rateEntity);
        }
        RateOfPassenger rateOfPassenger = new RateOfPassenger();
        rateOfPassenger.setPassengerRate(rateEntity.getPersonscore());
        rateOfPassenger.setPassengerConcern(rateEntity.getPersonreason());
        rateOfPassenger.setApplicationRate(rateEntity.getApplyscore());
        rateOfPassenger.setApplicationConcern(rateEntity.getApplyreason());
        return rateOfPassenger;
    }

    public static User fromEntityWithRoles(TRsDriverinfoEntity driverEntity) {
        if (driverEntity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(driverEntity)){
            Hibernate.initialize(driverEntity);
        }

        User user = new User(User.UserType.DRIVER);
        user.setUserId(driverEntity.getUuid());
        String name = driverEntity.getDrivername().trim();
        user.setUserName(name);
        user.setMobile(driverEntity.getMobile());
        user.setEid(driverEntity.getUuid());
        List<String> roles = new ArrayList<>();
        roles.add(Roles.DRIVER);
        user.setRoles(roles.toArray(new String[]{}));
        return user;
    }

    public static SyncResult fromEntity(TSyncDataStatusEntity entity) {
        if (entity == null) {
            return null;
        }
        if(!Hibernate.isInitialized(entity)){
            Hibernate.initialize(entity);
        }
        SyncResult result = new SyncResult();
        result.setResult("!!!" + entity.getUuid() + "#@" + (entity.getState().equals("1") ? true : false));
        return result;
    }

    public static List<String> fromEntity(List<TSyncDataStatusEntity> entityList) {
        List<String> resultList = new ArrayList<>();
        for (TSyncDataStatusEntity entity : entityList) {
            resultList.add("!!!" + entity.getUuid() + "#@" + entity.getState());
        }
        return resultList;
    }


    public static ApplyInfo toSnsApplyInfo(TBusApplyinfoEntity applyEntity){
        ApplyInfo applyInfo = new ApplyInfo();
        applyInfo.setApplyId(applyEntity.getUuid());
        applyInfo.setStartPoint(applyEntity.getStartpoint());
        applyInfo.setEndPoint(applyEntity.getWays());
        applyInfo.setStartTime(applyEntity.getBegintime().toDate());
        applyInfo.setEndTime(applyEntity.getEndtime().toDate());
        applyInfo.setMainUser(applyEntity.getPassenger().getName());
        applyInfo.setMainUserPhone(applyEntity.getPassenger().getMobile());
        applyInfo.setPeopleNum(applyEntity.getPeoplenum());
        applyInfo.setCoordinator(toSnsUser(applyEntity.getCoordinator(), User.UserType.COORDINATOR));
        applyInfo.setReason(ReasonType.typeOf(applyEntity.getReason()));
        if ("0".equals(applyEntity.getSfgf())) {
            applyInfo.setIrregular(false);
            applyInfo.setIrregularReson(applyEntity.getBgfxx());
        } else {
            applyInfo.setIrregular(true);
        }
        return applyInfo;
    }
    public static ScheduleInfo toSnsScheduleInfo(TBusScheduleRelaEntity scheduleEntity){
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setScheduleId(scheduleEntity.getUuid());
        scheduleInfo.setStartPoint(scheduleEntity.getStartpoint());
        scheduleInfo.setStartTime(scheduleEntity.getStarttime().toDate());
        scheduleInfo.setEndPoint(scheduleEntity.getWays());
        scheduleInfo.setEndTime(scheduleEntity.getEndtime().toDate());
        List<ApplyInfo> applyInfoList = new ArrayList<>();
        for(TBusApplyinfoEntity applyEntity : scheduleEntity.getApplications()){
            applyInfoList.add(toSnsApplyInfo(applyEntity));
        }
        scheduleInfo.setApplyInfoList(applyInfoList);
        List<ScheduleCarInfo> carInfoList = new ArrayList<>();
        for (TBusScheduleCarEntity scheduleCarEntity : scheduleEntity.getScheduleCars()) {
            if(scheduleCarEntity.getCanceltype() == null) {
                carInfoList.add(toSnsScheduleCarInfo(scheduleCarEntity));
            }
        }
        scheduleInfo.setScheduleCarList(carInfoList);
        return scheduleInfo;
    }
    public static ScheduleCarInfo toSnsScheduleCarInfo(TBusScheduleCarEntity scheduleCarEntity){
        ScheduleCarInfo scheduleCarInfo = new ScheduleCarInfo();
        scheduleCarInfo.setScheduleCarId(scheduleCarEntity.getUuid());
        scheduleCarInfo.setCarId(scheduleCarEntity.getCar().getId());
        scheduleCarInfo.setBadge(scheduleCarEntity.getCar().getCphm());
        scheduleCarInfo.setDriverId(scheduleCarEntity.getDriver().getUuid());
        scheduleCarInfo.setDriverName(scheduleCarEntity.getDriver().getDrivername());  // 驾驶员
        scheduleCarInfo.setDriverPhone(scheduleCarEntity.getDriver().getPhone());  // 驾驶员
        scheduleCarInfo.setDriverMobile(scheduleCarEntity.getDriver().getMobile());  // 驾驶员
        if (scheduleCarEntity.getRecord() != null) {
            if(scheduleCarEntity.getRecord().getStarttime() != null) {
                scheduleCarInfo.setStartMile(scheduleCarEntity.getRecord().getStartmile());
                scheduleCarInfo.setStartTime(scheduleCarEntity.getRecord().getStarttime().toDate());
            }
            if(scheduleCarEntity.getRecord().getEndtime() != null) {
                scheduleCarInfo.setEndMile(scheduleCarEntity.getRecord().getEndmile());
                scheduleCarInfo.setEndTime(scheduleCarEntity.getRecord().getEndtime().toDate());
            }
        }
        return scheduleCarInfo;
    }
    public static EvaluateInfo toSnsEvaluateInfo(TBusEvaluateinfoEntity evaluateEntity){
        EvaluateInfo evaluateInfo = new EvaluateInfo();
        evaluateInfo.setApplyId(evaluateEntity.getApplication().getUuid());
        evaluateInfo.setScheduleId(evaluateEntity.getSchedule().getUuid());
        evaluateInfo.setCphm(evaluateEntity.getCar().getCphm());
        evaluateInfo.setCarId(evaluateEntity.getCar().getId());
        evaluateInfo.setFleetName(evaluateEntity.getCar().getSysOrg().getOrgname());
        evaluateInfo.setDriverId(evaluateEntity.getDriver().getUuid());
        evaluateInfo.setDriverName(evaluateEntity.getDriver().getDrivername());
        evaluateInfo.setPassengerName(evaluateEntity.getApplication().getPassenger().getName());

        evaluateInfo.setCarScore(evaluateEntity.getCarscore());  // 评分
        evaluateInfo.setDriverScore(evaluateEntity.getDriverscore());   // 评分
        evaluateInfo.setPassengerScore(evaluateEntity.getPersonscore());// 评分
        evaluateInfo.setFleetScore(evaluateEntity.getTeamscore());// 评分

        evaluateInfo.setCarReason(evaluateEntity.getCarreason());  // 评价
        evaluateInfo.setDriverReason(evaluateEntity.getDriverreason());   // 评价
        evaluateInfo.setPassengerReason(evaluateEntity.getPersonreason());// 评价
        evaluateInfo.setFleetReason(evaluateEntity.getTeamreason());// 评价
        return evaluateInfo;
    }

    public static DispatcherPostContent toPostContent(TBusApplyinfoEntity applyEntity) {
        if (applyEntity == null) {
            return null;
        }
        DispatcherPostContent postContent = new DispatcherPostContent();
        postContent.setApply(toSnsApplyInfo(applyEntity));

        if(applyEntity.getSchedule() != null) {
            postContent.setSchedule(toSnsScheduleInfo(applyEntity.getSchedule()));
        }

        return postContent;
    }
    public static DispatcherPostContent toPostContent(TBusScheduleRelaEntity scheduleEntity) {
        if (scheduleEntity == null && scheduleEntity.getApplications() == null) {
            return null;
        }
        DispatcherPostContent postContent = new DispatcherPostContent();
        postContent.setSchedule(toSnsScheduleInfo(scheduleEntity));
        return postContent;
    }
    public static DispatcherPostContent toPostContent(TBusScheduleCarEntity carEntity) {
        if (carEntity == null && carEntity.getSchedule() == null) {
            return null;
        }
        DispatcherPostContent postContent = new DispatcherPostContent();
        if(carEntity.getSchedule() != null) {
            postContent.setSchedule(toSnsScheduleInfo(carEntity.getSchedule()));
            boolean finished = true;
            for(TBusScheduleCarEntity scheduleCarEntity : carEntity.getSchedule().getScheduleCars()) {
                if(scheduleCarEntity.getStatus() != ScheduleStatus.FINISHED.id()) {
                    finished = false;
                    break;
                }
            }
            postContent.setFinished(finished);
        }
        postContent.setScheduleCar(toSnsScheduleCarInfo(carEntity));
        return postContent;
    }
    public static DispatcherPostContent toPostContent(TBusRecordinfoEntity recordEntity) {
        if (recordEntity == null && recordEntity.getScheduleCar() == null) {
            return null;
        }
        return toPostContent(recordEntity.getScheduleCar());
    }
    public static DispatcherPostContent toPostContent(TBusEvaluateinfoEntity evaluateEntity) {
        if (evaluateEntity == null && (evaluateEntity.getApplication() == null || evaluateEntity.getSchedule() == null)) {
            return null;
        }
        DispatcherPostContent postContent = new DispatcherPostContent();
        postContent.setApply(toSnsApplyInfo(evaluateEntity.getApplication()));
//        postContent.setSchedule(toSnsScheduleInfo(evaluateEntity.getSchedule()));
        for(TBusScheduleCarEntity carEntity : evaluateEntity.getSchedule().getScheduleCars()) {
            if(carEntity.getCar().getId().equals(evaluateEntity.getCar().getId())) {
                postContent.setScheduleCar(toSnsScheduleCarInfo(carEntity));
            }
        }
        postContent.setEvaluateInfo(toSnsEvaluateInfo(evaluateEntity));
        return postContent;
    }

    public static ShareTomeEvent.MessageCategory convertStatus(String applyStatus) {
        ShareTomeEvent.MessageCategory messageCategory = ShareTomeEvent.MessageCategory.OTHER;
        if ("2".equals(applyStatus)) {               // 申请调度
            messageCategory = ShareTomeEvent.MessageCategory.APPLICATION_CREATE;
        } else if ("3".equals(applyStatus)) {     // 部门审批
            messageCategory = ShareTomeEvent.MessageCategory.APPLICATION_APPROVE;
        } else if ("4".equals(applyStatus)) {     // 调度成功
            messageCategory = ShareTomeEvent.MessageCategory.APPLICATION_DISPATCH;
        } else if ("5".equals(applyStatus)) {     // 审批退回
            messageCategory = ShareTomeEvent.MessageCategory.APPLICATION_REJECT;
        } else if ("6".equals(applyStatus)) {     // 调度退回
            messageCategory = ShareTomeEvent.MessageCategory.SCHEDULE_RETREAT;
        } else if ("7".equals(applyStatus)) {     // 调度作废
            messageCategory = ShareTomeEvent.MessageCategory.SCHEDULE_DELETE;
        } else if ("8".equals(applyStatus)) {     // 取消申请
            messageCategory = ShareTomeEvent.MessageCategory.APPLICATION_CANCEL;
        }
        return messageCategory;
    }
}
