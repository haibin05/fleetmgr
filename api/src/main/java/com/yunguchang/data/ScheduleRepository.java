package com.yunguchang.data;

import com.google.common.collect.Iterables;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.SyncEntityConvert;
import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.*;
import com.yunguchang.sam.PrincipalExt;
import com.yunguchang.utils.tools.DateTimeUtil;
import org.apache.commons.collections.map.HashedMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.util.*;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static com.yunguchang.model.persistence.TSyncDataStatusEntity.SyncDataType;

/**
 * Created by gongy on 9/27/2015.
 */
@Stateless
public class ScheduleRepository extends GenericRepository {
    @Inject
    private Logger logger;
    @Inject
    private UserRepository userRepository;
    @Inject
    private DriverRepository driverRepository;
    @Inject
    private FleetRepository fleetRepository;
    @Inject
    private ApplicationRepository applicationRepository;
    @Inject
    private SyncDataStatusRepository syncDataStatusRepository;

    public TBusScheduleCarEntity getScheduleCarByCarIdAndTime(String carId, DateTime dateTime, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleCarEntity> cq = cb.createQuery(TBusScheduleCarEntity.class);
        Root<TBusScheduleCarEntity> scheduleCar = cq.from(TBusScheduleCarEntity.class);
        cq.select(scheduleCar);
        scheduleCar.fetch(TBusScheduleCarEntity_.car);
        scheduleCar.fetch(TBusScheduleCarEntity_.driver);
        scheduleCar.fetch(TBusScheduleCarEntity_.schedule);
        cq.where(
                cb.and(
                        cb.equal(scheduleCar.join(TBusScheduleCarEntity_.car).get(TAzCarinfoEntity_.id), carId),
                        scheduleCar.get(TBusScheduleCarEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id()),       // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
                        cb.lessThanOrEqualTo(scheduleCar.join(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.starttime), dateTime),
                        cb.greaterThanOrEqualTo(scheduleCar.join(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.endtime), dateTime)
                )
        );
        applySecurityFilter("schedule_car", principalExt);

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    public TBusScheduleCarEntity getScheduleCarByDriverIdAndTime(String driverId, DateTime dateTime, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleCarEntity> cq = cb.createQuery(TBusScheduleCarEntity.class);
        Root<TBusScheduleCarEntity> scheduleCar = cq.from(TBusScheduleCarEntity.class);
        cq.select(scheduleCar);
        scheduleCar.fetch(TBusScheduleCarEntity_.car);
        scheduleCar.fetch(TBusScheduleCarEntity_.driver);
        Fetch<TBusScheduleCarEntity, TBusScheduleRelaEntity> scheduleFetch = scheduleCar.fetch(TBusScheduleCarEntity_.schedule);
        Join<TBusScheduleCarEntity, TBusScheduleRelaEntity> scheduleJoin = (Join<TBusScheduleCarEntity, TBusScheduleRelaEntity>) scheduleFetch;
        cq.where(
                cb.and(
                        cb.equal(scheduleCar.join(TBusScheduleCarEntity_.driver).get(TRsDriverinfoEntity_.uuid), driverId),
                        cb.lessThanOrEqualTo(scheduleJoin.get(TBusScheduleRelaEntity_.starttime), dateTime),
                        cb.greaterThanOrEqualTo(scheduleJoin.get(TBusScheduleRelaEntity_.endtime), dateTime),
                        scheduleJoin.get(TBusScheduleRelaEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id()),       // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
                        scheduleCar.get(TBusScheduleCarEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id())       // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
                )
        );

        applySecurityFilter("schedule_car", principalExt);

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    public List<TBusScheduleCarEntity> getSchedulesByDriverIdAndStartAndEnd(String driverId, DateTime start, DateTime end, PrincipalExt principalExt) {
        if (driverId == null) {
            return Collections.EMPTY_LIST;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleCarEntity> cq = cb.createQuery(TBusScheduleCarEntity.class);
        Root<TBusScheduleCarEntity> scheduleCar = cq.from(TBusScheduleCarEntity.class);
        Fetch<TBusScheduleCarEntity, TRsDriverinfoEntity> fetchDriver = scheduleCar.fetch(TBusScheduleCarEntity_.driver);
        scheduleCar.fetch(TBusScheduleCarEntity_.car);
        scheduleCar.fetch(TBusScheduleCarEntity_.record, JoinType.LEFT);
        Fetch<TBusScheduleCarEntity, TBusScheduleRelaEntity> fetchSchedule = scheduleCar.fetch(TBusScheduleCarEntity_.schedule);
        Join<TBusScheduleCarEntity, TBusScheduleRelaEntity> joinSchedule = (Join<TBusScheduleCarEntity, TBusScheduleRelaEntity>) fetchSchedule;


        Join<TBusScheduleCarEntity, TRsDriverinfoEntity> joinDriver = (Join<TBusScheduleCarEntity, TRsDriverinfoEntity>) fetchDriver;
        cq.select(scheduleCar);


        Predicate predicate = cb.and(
                cb.equal(joinDriver.get(TRsDriverinfoEntity_.uuid), driverId),
                joinSchedule.get(TBusScheduleRelaEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id()),       // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
                scheduleCar.get(TBusScheduleCarEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id())       // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
        );


        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(joinSchedule.get(TBusScheduleRelaEntity_.endtime), start));
        }
        if (end != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(joinSchedule.get(TBusScheduleRelaEntity_.starttime), end));
        }
        cq.where(predicate);
        cq.orderBy(cb.asc(joinSchedule.get(TBusScheduleRelaEntity_.starttime)));
        applySecurityFilter("schedule", principalExt);


        return em.createQuery(cq).getResultList();
    }

    public List<TBusScheduleRelaEntity> getSchedulesByCarIdAndStartAndEnd(String carId, DateTime start, DateTime end, PrincipalExt principalExt) {
        if (carId == null) {
            return Collections.EMPTY_LIST;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleRelaEntity> cq = cb.createQuery(TBusScheduleRelaEntity.class);
        Root<TBusScheduleRelaEntity> schedule = cq.from(TBusScheduleRelaEntity.class);
        Fetch<TBusScheduleRelaEntity, TBusScheduleCarEntity> fetchScheduleCar = schedule.fetch(TBusScheduleRelaEntity_.scheduleCars);
        Join<TBusScheduleRelaEntity, TBusScheduleCarEntity> scheduleCarJoin = (Join<TBusScheduleRelaEntity, TBusScheduleCarEntity>) fetchScheduleCar;
        fetchScheduleCar.fetch(TBusScheduleCarEntity_.driver);
        Fetch<TBusScheduleCarEntity, TAzCarinfoEntity> fetchCar = fetchScheduleCar.fetch(TBusScheduleCarEntity_.car);
        fetchScheduleCar.fetch(TBusScheduleCarEntity_.record, JoinType.LEFT);


        Join<TBusScheduleCarEntity, TAzCarinfoEntity> joinCar = (Join<TBusScheduleCarEntity, TAzCarinfoEntity>) fetchCar;

        cq.select(schedule);

        Predicate predicate = cb.and(
                cb.equal(joinCar.get(TAzCarinfoEntity_.id), carId),
                schedule.get(TBusScheduleRelaEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id()),       // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
                scheduleCarJoin.get(TBusScheduleCarEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id())       // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
        );

        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(schedule.get(TBusScheduleRelaEntity_.endtime), start));
        }
        if (end != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(schedule.get(TBusScheduleRelaEntity_.starttime), end));
        }
        cq.where(predicate);
        cq.orderBy(cb.asc(schedule.get(TBusScheduleRelaEntity_.starttime)));
        applySecurityFilter("schedule", principalExt);


        return em.createQuery(cq).getResultList();
    }

    public List<TBusScheduleRelaEntity> getSchedulesByStartAndEnd(DateTime start, DateTime end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleRelaEntity> criteriaQuery = cb.createQuery(TBusScheduleRelaEntity.class);
        Root<TBusScheduleRelaEntity> schedule = criteriaQuery.from(TBusScheduleRelaEntity.class);
        //schedule.fetch(TBusScheduleRelaEntity_.applications);
        Fetch<TBusScheduleRelaEntity, TBusScheduleCarEntity> scheduleCars = schedule.fetch(TBusScheduleRelaEntity_.scheduleCars);
        Join<TBusScheduleRelaEntity, TBusScheduleCarEntity> scheduleCarJoin = (Join<TBusScheduleRelaEntity, TBusScheduleCarEntity>) scheduleCars;
        scheduleCars.fetch(TBusScheduleCarEntity_.car);
        criteriaQuery.select(schedule);

        Predicate predicate = cb.conjunction();

        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(schedule.get(TBusScheduleRelaEntity_.endtime), start));
        }
        if (end != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(schedule.get(TBusScheduleRelaEntity_.starttime), end));
        }

        criteriaQuery.where(predicate);

        criteriaQuery.orderBy(cb.desc(schedule.get(TBusScheduleRelaEntity_.starttime)));
        return em.createQuery(criteriaQuery).getResultList();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusScheduleRelaEntity createSchedule(Schedule schedule, PrincipalExt principalExt) {
        DateTime start = null;
        DateTime end = null;
        String origin = null;
        String destination = null;
        int passengers = 0;

        if (schedule.getApplications() != null) {
            for (Application application : schedule.getApplications()) {
                TBusApplyinfoEntity applicationEntity = applicationRepository.getApplicationById(application.getId(), principalExt);
                // 状态非【申请调度】或者【等待审核】
                if (!ApplyStatus.APPLY.toStringValue().equals(applicationEntity.getStatus()) && !ApplyStatus.DEP_APPROVE.toStringValue().equals(applicationEntity.getStatus())) {
                    throw logger.invalidOperation("Create schedule info");
                }
                if (start == null || start.isAfter(applicationEntity.getBegintime())) {
                    start = applicationEntity.getBegintime();
                    origin = applicationEntity.getStartpoint();
                }
                if (end == null || end.isBefore(applicationEntity.getEndtime())) {
                    end = applicationEntity.getEndtime();
                    destination = applicationEntity.getWays();
                }
                passengers += applicationEntity.getPeoplenum() == null ? 0 : applicationEntity.getPeoplenum();
            }
        }

        TSysOrgEntity fleetEntity = null;
        for (ScheduleCar scheduleCar : schedule.getScheduleCars()) {
            fleetEntity = fleetRepository.getFleetByCarId(scheduleCar.getCar().getId());
            if (fleetEntity != null) {
                break;
            }
        }


        TBusScheduleRelaEntity busScheduleRelaEntity = new TBusScheduleRelaEntity();
        busScheduleRelaEntity.setStatus(ScheduleStatus.AWAITING.id());
        busScheduleRelaEntity.setStarttime(start);
        busScheduleRelaEntity.setEndtime(end);
        busScheduleRelaEntity.setStartpoint(origin);
        busScheduleRelaEntity.setWays(destination);
        busScheduleRelaEntity.setPeoplenum(passengers);
        busScheduleRelaEntity.setFleet(fleetEntity);

        TSysUserEntity user = em.getReference(TSysUserEntity.class, principalExt.getUserIdOrNull());
        busScheduleRelaEntity.setSenduser(user);
        busScheduleRelaEntity.setReciveuser(user);
        // 数据同步时，需要将ID保存到数据库中
        if (schedule.getId() != null) {
            busScheduleRelaEntity.setUuid(schedule.getId());
        }
        busScheduleRelaEntity.setUpdatedate(new DateTime());
        busScheduleRelaEntity = em.merge(busScheduleRelaEntity);

        List<TBusApplyinfoEntity> applyEntityList = new ArrayList<>();
        if (schedule.getApplications() != null) {
            for (Application application : schedule.getApplications()) {
                TBusApplyinfoEntity apply = em.find(TBusApplyinfoEntity.class, application.getId());
                apply.setSchedule(busScheduleRelaEntity);
                apply.setFleet(fleetEntity);
                apply.setSenduser(user);
                apply.setStatus(ApplyStatus.DISPATCH_SUCCESS.toStringValue());
                applyEntityList.add(apply);
            }
        }
        busScheduleRelaEntity.setApplications(applyEntityList);

        if (schedule.getScheduleCars() != null) {
            List<TBusScheduleCarEntity> scheduleCarEntityList = new ArrayList<>();
            int canLoadMaxSize = 0;
            int schcarNo = 1;
            for (ScheduleCar scheduleCar : schedule.getScheduleCars()) {
                TBusScheduleCarEntity scheduleCarEntity = new TBusScheduleCarEntity();
                TAzCarinfoEntity car = em.getReference(TAzCarinfoEntity.class, scheduleCar.getCar().getId());
                scheduleCarEntity.setCar(car);
                canLoadMaxSize += car.getHdzk().intValue();
                scheduleCarEntity.setSchedule(busScheduleRelaEntity);

                scheduleCarEntity.setSchcarno((DateTimeFormat.forPattern("yyyyMMddHHmmssSSS")).print(DateTime.now()) + "_" + schcarNo++);
                if (scheduleCar.getDriver() != null) {
                    scheduleCarEntity.setDriver(em.getReference(TRsDriverinfoEntity.class, scheduleCar.getDriver().getId()));
                }
                scheduleCarEntity.setStatus(ScheduleStatus.AWAITING.id()); // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
                scheduleCarEntity = em.merge(scheduleCarEntity);
                scheduleCarEntityList.add(scheduleCarEntity);
            }
            if (canLoadMaxSize < passengers) {
                logger.invalidOperation("Create schedule");
            }
            busScheduleRelaEntity.setScheduleCars(scheduleCarEntityList);
        }
        em.flush();

        syncDataStatusRepository.saveLocalSyncDataStatus(TSyncDataStatusEntity.SyncDataType.SCHEDULE, SyncEntityConvert.toScheduleInfo(busScheduleRelaEntity));
        //reload all data and links
        return busScheduleRelaEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusScheduleRelaEntity createSchedule(TBusScheduleRelaEntity schedule, PrincipalExt principalExt) {
        TBusScheduleRelaEntity busScheduleRelaEntity = new TBusScheduleRelaEntity();
        busScheduleRelaEntity.setStatus(schedule.getStatus());
        busScheduleRelaEntity.setStarttime(schedule.getStarttime());
        busScheduleRelaEntity.setEndtime(schedule.getEndtime());
        busScheduleRelaEntity.setStartpoint(schedule.getStartpoint());
        busScheduleRelaEntity.setWays(schedule.getWays());
        busScheduleRelaEntity.setPeoplenum(schedule.getPeoplenum());
        busScheduleRelaEntity.setInsertdate(DateTime.now());
        busScheduleRelaEntity.setUpdatedate(DateTime.now());
        if (schedule.getFleet() != null) {
            busScheduleRelaEntity.setFleet(em.getReference(TSysOrgEntity.class, schedule.getFleet().getOrgid()));
        }
        if (schedule.getSenduser() != null) {
            busScheduleRelaEntity.setSenduser(em.getReference(TSysUserEntity.class, schedule.getSenduser().getUserid()));
        }
        if (schedule.getReciveuser() != null) {
            busScheduleRelaEntity.setReciveuser(em.getReference(TSysUserEntity.class, schedule.getReciveuser().getUserid()));
        }
//        List<TBusScheduleCarEntity> carEntityList = new ArrayList<>();
//        for(TBusScheduleCarEntity carEntity : schedule.getScheduleCars()) {
//            carEntityList.add(em.getReference(TBusScheduleCarEntity.class, carEntity.getUuid()));
//        }
//        busScheduleRelaEntity.setScheduleCars(carEntityList);

        if (schedule.getUuid() == null) {
            em.persist(busScheduleRelaEntity);
        } else {   // 数据同步时，需要将ID保存到数据库中
            busScheduleRelaEntity.setUuid(schedule.getUuid());
            busScheduleRelaEntity = em.merge(busScheduleRelaEntity);
        }

        List<TBusApplyinfoEntity> applyinfoEntityList = new ArrayList<>();
        for (TBusApplyinfoEntity applyinfoEntity : schedule.getApplications()) {
            TBusApplyinfoEntity applyEntity = em.find(TBusApplyinfoEntity.class, applyinfoEntity.getUuid());
            if (applyEntity == null) {
                throw new EntityNotFoundException("Apply info is not exist!");
            }
            TBusApproveSugEntity approveSugEntity = applicationRepository.getApplyApproveInfoByApplyNo(applyEntity.getApplyno());
            if (approveSugEntity == null) {
                throw new EntityNotFoundException("Apply approve info is not exist!");
            }
            applyEntity.setStatus(ApplyStatus.DISPATCH_SUCCESS.toStringValue());     // 调度成功
            applyEntity.setUpdatedate(DateTime.now());
            applyEntity.setSchedule(busScheduleRelaEntity);
        }
        busScheduleRelaEntity.setApplications(applyinfoEntityList);

        if (schedule.getScheduleCars() != null) {
            List<TBusScheduleCarEntity> scheduleCarEntityList = new ArrayList<>();
            for (TBusScheduleCarEntity scheduleCarEntity : schedule.getScheduleCars()) {
                scheduleCarEntity.setCar(em.getReference(TAzCarinfoEntity.class, scheduleCarEntity.getCar().getId()));
                scheduleCarEntity.setSchedule(busScheduleRelaEntity);
                if (scheduleCarEntity.getDriver() != null) {
                    scheduleCarEntity.setDriver(em.getReference(TRsDriverinfoEntity.class, scheduleCarEntity.getDriver().getUuid()));
                }
                scheduleCarEntity.setStatus(ScheduleStatus.AWAITING.id()); // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
                scheduleCarEntity = em.merge(scheduleCarEntity);
                scheduleCarEntityList.add(scheduleCarEntity);
            }
            busScheduleRelaEntity.setScheduleCars(scheduleCarEntityList);
        }

        // 调度待出车
        busScheduleRelaEntity.setStatus(ScheduleStatus.AWAITING.id());
        //reload all data and links
        return busScheduleRelaEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusScheduleRelaEntity updateSchedules(String scheduleId, Schedule schedule, PrincipalExt principalExt) {
        if (scheduleId == null || schedule == null) {
            throw new EntityNotFoundException("Schedule or scheduleInfo is not correct!");
        }

        TBusScheduleRelaEntity scheduleRelaEntity = getScheduleById(scheduleId, principalExt);
        if (scheduleRelaEntity == null) {
            throw logger.entityNotFound(TBusScheduleRelaEntity.class, scheduleId);
        }
        // 车队
        TSysOrgEntity fleetEntity = null;
        if (scheduleRelaEntity.getFleet() == null) {
            for (ScheduleCar scheduleCar : schedule.getScheduleCars()) {
                fleetEntity = fleetRepository.getFleetByCarId(scheduleCar.getCar().getId());
                if (fleetEntity != null) {
                    scheduleRelaEntity.setFleet(fleetEntity);
                    break;
                }
            }
        }

        // 申请单
        Map<String, Boolean> needChangeApplyIdMap = new HashMap<>();
        for (TBusApplyinfoEntity applyEntity : scheduleRelaEntity.getApplications()) {
            needChangeApplyIdMap.put(applyEntity.getUuid(), Boolean.FALSE);
        }
        if (schedule.getApplications() != null) {
            for (Application application : schedule.getApplications()) {
                if (!needChangeApplyIdMap.containsKey(application.getId())) {
                    needChangeApplyIdMap.put(application.getId(), Boolean.TRUE);
                } else {
                    needChangeApplyIdMap.put(application.getId(), null);
                }
            }
        }
        for (String applyId : needChangeApplyIdMap.keySet()) {
            if (Boolean.TRUE.equals(needChangeApplyIdMap.get(applyId))) {     // 增加
                TBusApplyinfoEntity apply = em.find(TBusApplyinfoEntity.class, applyId);
                apply.setSchedule(scheduleRelaEntity);
                apply.setFleet(fleetEntity);
                apply.setStatus(ApplyStatus.DISPATCH_SUCCESS.toStringValue());
                apply.setUpdatedate(DateTime.now());
                apply.setUpdateuser(principalExt.getUserIdOrNull());
                scheduleRelaEntity.getApplications().add(apply);

                // SCHEDULE_ADD_APPLY#scheduleId#applyId
                syncDataStatusRepository.saveLocalSyncDataStatus(SyncDataType.SCHEDULE_ADD_APPLY, "SCHEDULE_ADD_APPLY#" + scheduleRelaEntity.getUuid() + "#" + applyId);
            } else if (Boolean.FALSE.equals(needChangeApplyIdMap.get(applyId))) {     // 删除
                TBusApplyinfoEntity apply = em.find(TBusApplyinfoEntity.class, applyId);   // 对于更新中不包含 申请单 的，视为 申请单 作废
                scheduleRelaEntity.getApplications().remove(apply);
                apply.setSchedule(null);
                apply.setCanceltype("0");  // 0 用户原因 1 车队原因
                apply.setCancelseason("用户原因");
                apply.setStatus(ApplyStatus.DISPATCH_CANCEL.toStringValue());
                apply.setUpdatedate(DateTime.now());
                apply.setUpdateuser(principalExt.getUserIdOrNull());

                // APPLY_DELETE#applyId#cancelType#cancelReason#userId
                syncDataStatusRepository.saveLocalSyncDataStatus(SyncDataType.APPLY_DELETE, "APPLY_DELETE#" + applyId + "#" + apply.getCanceltype() + "#" + apply.getCancelseason() + "#" + apply.getUpdateuser());
            }
        }

        // 调度出车单
        Map<String, CarAndDriverInfo> oldCarAndDriverInfoMap = new HashedMap();
        for (TBusScheduleCarEntity oldScheduleCar : scheduleRelaEntity.getScheduleCars()) {
            CarAndDriverInfo carAndDriverInfo = new CarAndDriverInfo();
            carAndDriverInfo.scheduleCarId = oldScheduleCar.getUuid();
            carAndDriverInfo.carId = oldScheduleCar.getCar().getId();
            carAndDriverInfo.driverId = oldScheduleCar.getDriver().getUuid();
            oldCarAndDriverInfoMap.put(oldScheduleCar.getUuid(), carAndDriverInfo);
        }
        Map<String, CarAndDriverInfo> newCarAndDriverInfoMap = new HashedMap();
        for (ScheduleCar newScheduleCar : schedule.getScheduleCars()) {
            CarAndDriverInfo carAndDriverInfo = new CarAndDriverInfo();
            carAndDriverInfo.scheduleCarId = newScheduleCar.getId();
            carAndDriverInfo.carId = newScheduleCar.getCar().getId();
            carAndDriverInfo.driverId = newScheduleCar.getDriver().getId();
            newCarAndDriverInfoMap.put(newScheduleCar.getId(), carAndDriverInfo);
        }

        // 将 新列表中包含 老列表 中的数据进行处理
        for (String scheduleCarId : oldCarAndDriverInfoMap.keySet()) {
            if (null == newCarAndDriverInfoMap.get(scheduleCarId)) {     // 新列表中没有，则为删除
                TBusScheduleCarEntity carEntity = em.find(TBusScheduleCarEntity.class, scheduleCarId);
                if (carEntity != null && !carEntity.getStatus().equals(ScheduleStatus.CANCELED.id())) {
                    scheduleRelaEntity.getScheduleCars().remove(carEntity);
//                    em.remove(carEntity);
                    carEntity.setStatus(ScheduleStatus.CANCELED.id());
                    carEntity.setCanceltype("1");  // 0 用户原因 1 车队原因
                    carEntity.setCancelseason("车队原因");
                    carEntity.setCanceltime(new Timestamp(new DateTime().getMillis()));

                    // [SCHEDULE_CAR#scheduleCarId#cancelType#cancelSeason#userId]
                    syncDataStatusRepository.saveLocalSyncDataStatus(SyncDataType.DELETE_CAR, "SCHEDULE_DELETE_CAR#" + scheduleCarId + "#" + carEntity.getCanceltype() + "#" + carEntity.getCancelseason() + "#" + principalExt.getUserIdOrNull());
                }
                continue;
            }
            // 更新数据
            if (!oldCarAndDriverInfoMap.get(scheduleCarId).equals(newCarAndDriverInfoMap.get(scheduleCarId))) {
                TBusScheduleCarEntity scheduleCarEntity = em.find(TBusScheduleCarEntity.class, scheduleCarId);
                CarAndDriverInfo newScheduleCarInfo = newCarAndDriverInfoMap.get(scheduleCarId);
                CarAndDriverInfo oldScheduleCarInfo = oldCarAndDriverInfoMap.get(scheduleCarId);
                if (!oldScheduleCarInfo.carId.equals(newScheduleCarInfo.carId)) {
                    TAzCarinfoEntity car = em.find(TAzCarinfoEntity.class, newScheduleCarInfo.carId);
                    // 车辆
                    if (car == null) {
                        throw new EntityNotFoundException("Car is not exist!");
                    } else {
                        scheduleCarEntity.setCar(car);
                    }
                }
                if (!oldScheduleCarInfo.driverId.equals(newScheduleCarInfo.driverId)) {
                    // 驾驶员
                    TRsDriverinfoEntity driver = em.find(TRsDriverinfoEntity.class, newScheduleCarInfo.driverId);
                    if (driver == null) {
                        throw new EntityNotFoundException("Driver is not exist!");
                    } else {
                        scheduleCarEntity.setDriver(driver);
                    }
                }

                // [SCHEDULE_CAR#scheduleCarId#newCarid#newDriverId#userId]
                syncDataStatusRepository.saveLocalSyncDataStatus(SyncDataType.UPDATED, "SCHEDULE_CAR#" + scheduleCarId + "#" + newScheduleCarInfo.carId + "#" + newScheduleCarInfo.driverId + "#" + principalExt.getUserIdOrNull());
                scheduleCarEntity.setStatus(ScheduleStatus.AWAITING.id());
            }
            newCarAndDriverInfoMap.remove(scheduleCarId);
        }

        // 将 新列表中不包含 老列表 中的数据进行处理
        for (String scheduleCarId : newCarAndDriverInfoMap.keySet()) {
            TBusScheduleCarEntity scheduleCarEntity = new TBusScheduleCarEntity();
            TAzCarinfoEntity car = em.find(TAzCarinfoEntity.class, newCarAndDriverInfoMap.get(scheduleCarId).carId);
            // 车辆
            if (car == null) {
                throw new EntityNotFoundException("Car is not exist!");
            } else {
                scheduleCarEntity.setCar(car);
            }
            // 驾驶员
            TRsDriverinfoEntity driver = em.find(TRsDriverinfoEntity.class, newCarAndDriverInfoMap.get(scheduleCarId).driverId);
            if (driver == null) {
                throw new EntityNotFoundException("Driver is not exist!");
            } else {
                scheduleCarEntity.setDriver(driver);
            }
            scheduleCarEntity.setSchedule(scheduleRelaEntity);
            scheduleCarEntity.setStatus(ScheduleStatus.AWAITING.id());
            em.merge(scheduleCarEntity);
            scheduleRelaEntity.getScheduleCars().add(scheduleCarEntity);
        }

        scheduleRelaEntity.setStatus(ScheduleStatus.AWAITING.id());
        scheduleRelaEntity.setUpdatedate(new DateTime());
        em.flush();
        return scheduleRelaEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusScheduleRelaEntity updateSchedules(String scheduleId, TBusScheduleRelaEntity schedule, PrincipalExt principalExt) {
        if (scheduleId == null || schedule == null) {
            throw new EntityNotFoundException("Schedule or scheduleInfo is not correct!");
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleRelaEntity> criteriaQuery = cb.createQuery(TBusScheduleRelaEntity.class);
        Root<TBusScheduleRelaEntity> scheduleRoot = criteriaQuery.from(TBusScheduleRelaEntity.class);
        criteriaQuery.select(scheduleRoot);
        criteriaQuery.where(cb.equal(scheduleRoot.get(TBusScheduleRelaEntity_.uuid), scheduleId));

        List<TBusScheduleRelaEntity> scheduleRelaEntities = em.createQuery(criteriaQuery).getResultList();

        if (scheduleRelaEntities.size() != 1) {
            throw logger.entityNotFound(TBusScheduleRelaEntity.class, scheduleId);
        }

        schedule.setUpdatedate(new DateTime());
        schedule = em.merge(schedule);

        for (TBusApplyinfoEntity applyinfoEntity : schedule.getApplications()) {
            applyinfoEntity = em.find(TBusApplyinfoEntity.class, applyinfoEntity.getUuid());
            applyinfoEntity.setSchedule(schedule);
        }
        return schedule;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusScheduleRelaEntity deleteSchedule(String scheduleId, PrincipalExt principalExt) {
        TBusScheduleRelaEntity schedule = em.find(TBusScheduleRelaEntity.class, scheduleId);
        if (schedule == null) {
            throw logger.entityNotFound(TBusScheduleRelaEntity.class, scheduleId);
        }
        schedule.setStatus(ScheduleStatus.CANCELED.id());
        schedule.setCanceltime(DateTime.now());
        schedule.setCanceltype("0");    // 0 :用户原因  1:车队原因
        schedule.setCancelseason("用户原因");    // 0 :用户原因  1:车队原因
        if (schedule.getApplications() != null) {
            for (TBusApplyinfoEntity applyinfoEntity : schedule.getApplications()) {
                applyinfoEntity.setSchedule(null);
                applyinfoEntity.setStatus(ApplyStatus.DISPATCH_CANCEL.toStringValue());
                applyinfoEntity.setUpdatedate(DateTime.now());
                applyinfoEntity.setUpdateuser(principalExt.getUserIdOrNull());
            }
        }
        if (schedule.getScheduleCars() != null) {
            for (TBusScheduleCarEntity carEntity : schedule.getScheduleCars()) {
                carEntity.setCanceltime(new Timestamp(DateTime.now().getMillis()));
                carEntity.setCanceluser(principalExt.getUserIdOrNull());
                carEntity.setCanceltype("0");    // 0 :用户原因  1:车队原因
                carEntity.setCancelseason("用户原因");    // 0 :用户原因  1:车队原因
                carEntity.setStatus(ScheduleStatus.CANCELED.id());     // 0:未生效  1:待出车   2:返回   3: 调度作废
            }
        }
        return schedule;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusScheduleCarEntity updateScheduleCar4PostId(String scheduleCarId, TBusScheduleCarEntity scheduleCar) {
        if (scheduleCarId == null || scheduleCar == null) {
            throw new EntityNotFoundException("Schedule or scheduleInfo is not correct!");
        }
        TBusScheduleCarEntity scheduleCarEntity = em.find(TBusScheduleCarEntity.class, scheduleCarId);

        if (scheduleCar.getPostId4Driver() != null) {
            scheduleCarEntity.setPostId4Driver(scheduleCar.getPostId4Driver());
        }
        if (scheduleCar.getPostId4Manager() != null) {
            scheduleCarEntity.setPostId4Manager(scheduleCar.getPostId4Manager());
        }

        em.flush();
        return scheduleCarEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusRecordinfoEntity updateScheduleCarRecordWith(String scheduleCarId, Record record, PrincipalExt principalExt) {
        TBusScheduleCarEntity scheduleCar = getScheduleCar(scheduleCarId, principalExt);
        if (scheduleCar == null) {
            throw logger.entityNotFound(TBusScheduleCarEntity.class, scheduleCarId);
        }

        TBusRecordinfoEntity recordEntity = getScheduleCarRecordByScheduleCarIdWithPermission(scheduleCarId, principalExt);
        if (recordEntity == null) {
            recordEntity = new TBusRecordinfoEntity();
        } else {
            // 重复提交
            if (record.getEndMile() == null && record.getStartMile() != null && recordEntity.getStartmile() != null) {   // 填写开始里程
                throw logger.invalidOperation("update start mile");
            }
            if (record.getEndMile() != null && recordEntity.getEndmile() != null) {                                 // 填写结束里程
                throw logger.invalidOperation("update end mile");
            }
            if (record.getId() != recordEntity.getUuid()) {
                TBusRecordinfoEntity toDeleteEntity = em.find(TBusRecordinfoEntity.class, recordEntity.getUuid());
                em.remove(toDeleteEntity);
                em.flush();
                em.detach(recordEntity);
            }
        }
        recordEntity.setOperatedate(DateTime.now());
        recordEntity.setUpdatedate(DateTime.now());

        TBusScheduleRelaEntity schedule = scheduleCar.getSchedule();
        recordEntity.setSchedule(schedule);
        recordEntity.setCar(scheduleCar.getCar());
        recordEntity.setDriver(scheduleCar.getDriver());
        if (record.getStart() != null) {
            recordEntity.setStarttime(record.getStart());
        } else {
            recordEntity.setStarttime(DateTime.now());
        }
        if (record.getEndMile() != null && record.getEndMile() > 0) {
            if (record.getEnd() != null) {
                recordEntity.setEndtime(record.getEnd());
            } else {
                recordEntity.setEndtime(DateTime.now());
            }
        }
        if(recordEntity.getEndtime() == null) {
            recordEntity.setEndtime(record.getEnd());
        }
        if (record.getStartMile() != null) {
            recordEntity.setStartmile(record.getStartMile());
        }
        if (record.getEndMile() != null) {
            recordEntity.setEndmile(record.getEndMile());
        }
        recordEntity.setScheduleCar(scheduleCar);
        scheduleCar.setRecord(recordEntity);

        if (record.getStartMile() != null && record.getEndMile() != null) {
            recordEntity.setMileage(record.getEndMile() - record.getStartMile());
        }
        recordEntity.setStaycost(record.getLodging());
        recordEntity.setStopcost(record.getParking());
        recordEntity.setRefuelcost(record.getFuel());
        recordEntity.setTrancost(record.getTransport());
        recordEntity.setRemark(record.getComment());
        if(recordEntity.getEndtime() != null) {
            Double costsum = 0d;
            if(record.getTransport() != null) {
                costsum += record.getTransport();
            }
            if(record.getParking() != null) {
                costsum += record.getParking();
            }
            if(record.getLodging() != null) {
                costsum += record.getLodging();
            }
            if(record.getFuel() != null) {
                costsum += record.getFuel();
            }
            recordEntity.setCostsum(costsum);
        }
        if (record.getId() != null) {
            recordEntity.setUuid(record.getId());
        }
        recordEntity = em.merge(recordEntity);

        if (record.getEndMile() != null && record.getEndMile() > 0) {
            scheduleCar.setStatus(ScheduleStatus.FINISHED.id());
            schedule.setStatus(ScheduleStatus.FINISHED.id());
        }

        em.flush();

        if (scheduleCar.getCar() != null) {
            TAzCarinfoEntity car = em.find(TAzCarinfoEntity.class, scheduleCar.getCar().getId());
            car.setInusing(false);
        }

        return recordEntity;
    }


    @TransactionAttribute(REQUIRES_NEW)
    public TBusScheduleCarEntity createScheduleCar4Sync(TBusScheduleCarEntity scheduleCar, PrincipalExt principalExt) {
        prepareScheduleCarInfo(scheduleCar, scheduleCar);
        scheduleCar.setStatus(ScheduleStatus.AWAITING.id());
        if (scheduleCar.getUuid() == null) {
            em.persist(scheduleCar);
        } else {
            scheduleCar = em.merge(scheduleCar);
        }
        return scheduleCar;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusScheduleCarEntity updateScheduleCar4Sync(String scheduleCarId, TBusScheduleCarEntity scheduleCar, PrincipalExt principalExt) {
        TBusScheduleCarEntity scheduleCarEntity = em.getReference(TBusScheduleCarEntity.class, scheduleCarId);
        if (scheduleCarEntity == null) {
            throw new EntityNotFoundException("Schedule car is not found");
        }
        prepareScheduleCarInfo(scheduleCar, scheduleCarEntity);

        if (scheduleCar.getStatus() == null) {
            scheduleCarEntity.setStatus(ScheduleStatus.AWAITING.id());
        } else {
            scheduleCarEntity.setStatus(ScheduleStatus.valueOf(Integer.valueOf(scheduleCar.getStatus())).id());
        }
        scheduleCarEntity = em.merge(scheduleCarEntity);
        return scheduleCarEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusScheduleCarEntity cancelScheduleCar4Sync(String scheduleCarId, String cancelType, String reasonMessage, PrincipalExt principalExt) {
        TBusScheduleCarEntity scheduleCarEntity = em.getReference(TBusScheduleCarEntity.class, scheduleCarId);
        if (scheduleCarEntity == null) {
            throw new EntityNotFoundException("Schedule car is not found");
        }

        scheduleCarEntity.setCanceltype(cancelType);
        scheduleCarEntity.setCancelseason(reasonMessage);
        scheduleCarEntity.setCanceltime(new Timestamp(DateTime.now().getMillis()));
        scheduleCarEntity.setStatus(ScheduleStatus.CANCELED.id());

        return scheduleCarEntity;
    }

    public TBusScheduleRelaEntity getScheduleById(String id, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleRelaEntity> cq = cb.createQuery(TBusScheduleRelaEntity.class);
        Root<TBusScheduleRelaEntity> scheduleRoot = cq.from(TBusScheduleRelaEntity.class);
        scheduleRoot.fetch(TBusScheduleRelaEntity_.applications, JoinType.LEFT);
        cq.where(
                cb.equal(scheduleRoot.get(TBusScheduleRelaEntity_.uuid), id)
        );
        applySecurityFilter("schedule", principalExt);
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    public List<TBusScheduleCarEntity> getAllScheduleCars(DateTime start, DateTime end, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleCarEntity> cq = cb.createQuery(TBusScheduleCarEntity.class);
        Root<TBusScheduleCarEntity> scheduleCar = cq.from(TBusScheduleCarEntity.class);

        scheduleCar.fetch(TBusScheduleCarEntity_.car).fetch(TAzCarinfoEntity_.driver);
        scheduleCar.fetch(TBusScheduleCarEntity_.driver);
        scheduleCar.fetch(TBusScheduleCarEntity_.schedule);

        Predicate predicate = cb.conjunction();
        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(scheduleCar.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.insertdate), start));
        }
        if (end != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(scheduleCar.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.insertdate), end));
        }

        applySecurityFilter("schedule_car", principalExt);

        cq.where(predicate).orderBy(cb.desc(scheduleCar.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.insertdate)));

        return em.createQuery(cq).getResultList();
    }

    public List<TBusScheduleCarEntity> getAllScheduleCarsInfo(DateTime start, DateTime end, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleCarEntity> cq = cb.createQuery(TBusScheduleCarEntity.class);
        Root<TBusScheduleCarEntity> scheduleCar = cq.from(TBusScheduleCarEntity.class);

        scheduleCar.fetch(TBusScheduleCarEntity_.car);
        scheduleCar.fetch(TBusScheduleCarEntity_.schedule);

        Predicate predicate = cb.conjunction();
        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(scheduleCar.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.updatedate), start));
        }
        if (end != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(scheduleCar.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.updatedate), end));
        }

        applySecurityFilter("schedule_car", principalExt);

        cq.where(predicate).orderBy(cb.desc(scheduleCar.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.insertdate)));

        return em.createQuery(cq).getResultList();
    }

    public TBusScheduleCarEntity getScheduleCar(String id, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleCarEntity> cq = cb.createQuery(TBusScheduleCarEntity.class);
        Root<TBusScheduleCarEntity> scheduleRoot = cq.from(TBusScheduleCarEntity.class);
        scheduleRoot.fetch(TBusScheduleCarEntity_.car);
        scheduleRoot.fetch(TBusScheduleCarEntity_.driver);
        Fetch<TBusScheduleCarEntity, TBusScheduleRelaEntity> scheduleFetch = scheduleRoot.fetch(TBusScheduleCarEntity_.schedule);
        scheduleFetch.fetch(TBusScheduleRelaEntity_.applications, JoinType.LEFT);
        cq.where(
                cb.equal(scheduleRoot.get(TBusScheduleCarEntity_.uuid), id)
        );
        applySecurityFilter("schedule_car", principalExt);

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    public TBusRecordinfoEntity getScheduleCarRecordByScheduleCarIdWithPermission(String scheduleCarId, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusRecordinfoEntity> cq = cb.createQuery(TBusRecordinfoEntity.class);
        Root<TBusRecordinfoEntity> scheduleRoot = cq.from(TBusRecordinfoEntity.class);
        scheduleRoot.fetch(TBusRecordinfoEntity_.scheduleCar);
        cq.select(scheduleRoot);
        cq.where(
                cb.equal(scheduleRoot.get(TBusRecordinfoEntity_.scheduleCar).get(TBusScheduleCarEntity_.uuid), scheduleCarId
                ));

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    public TBusScheduleCarEntity getScheduleCarByApplicationIdAndCarId(String applicationId, String carId, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleCarEntity> cq = cb.createQuery(TBusScheduleCarEntity.class);
        Root<TBusScheduleCarEntity> scheduleCarRoot = cq.from(TBusScheduleCarEntity.class);
        Root<TBusApplyinfoEntity> appRoot = cq.from(TBusApplyinfoEntity.class);
        Root<TBusScheduleRelaEntity> scheduleRoot = cq.from(TBusScheduleRelaEntity.class);
        cq.select(scheduleCarRoot);
        cq.where(
                cb.equal(appRoot.get(TBusApplyinfoEntity_.schedule), scheduleCarRoot.get(TBusScheduleCarEntity_.schedule)),
                cb.equal(appRoot.get(TBusApplyinfoEntity_.uuid), applicationId),
                cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.car).get(TAzCarinfoEntity_.id), carId),
                cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.schedule), scheduleRoot)
        );

        applySecurityFilter("schedule_car", principalExt);

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    public List<TBusScheduleRelaEntity> getAllSchedules(String driverId, String carId, String scheduleCarId, Integer offset, Integer limit, OrderByParam orderByParam, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleRelaEntity> cq = cb.createQuery(TBusScheduleRelaEntity.class);
        Root<TBusScheduleRelaEntity> scheduleRoot = cq.from(TBusScheduleRelaEntity.class);
        Fetch<TBusScheduleRelaEntity, TBusScheduleCarEntity> fetchScheduleCars = scheduleRoot.fetch(TBusScheduleRelaEntity_.scheduleCars);
        fetchScheduleCars.fetch(TBusScheduleCarEntity_.car);
        fetchScheduleCars.fetch(TBusScheduleCarEntity_.driver);
        scheduleRoot.fetch(TBusScheduleRelaEntity_.senduser, JoinType.LEFT);
        scheduleRoot.fetch(TBusScheduleRelaEntity_.reciveuser, JoinType.LEFT);

        cq.select(scheduleRoot);
        Predicate predicate = cb.conjunction();
        if (driverId != null) {
            Subquery<TBusScheduleCarEntity> scheduleCarSubQuery = cq.subquery(TBusScheduleCarEntity.class);
            Root<TBusScheduleCarEntity> scheduleCarRoot = scheduleCarSubQuery.from(TBusScheduleCarEntity.class);
            scheduleCarSubQuery.select(scheduleCarRoot);
            scheduleCarSubQuery.where(
                    cb.and(
                            cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.schedule), scheduleRoot),
                            cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.driver).get(TRsDriverinfoEntity_.uuid), driverId)
                    )
            );

            predicate = cb.and(
                    predicate,
                    cb.exists(scheduleCarSubQuery)
            );
        }

        if (carId != null) {
            Subquery<TBusScheduleCarEntity> scheduleCarSubQuery = cq.subquery(TBusScheduleCarEntity.class);
            Root<TBusScheduleCarEntity> scheduleCarRoot = scheduleCarSubQuery.from(TBusScheduleCarEntity.class);
            scheduleCarSubQuery.select(scheduleCarRoot);
            scheduleCarSubQuery.where(
                    cb.and(
                            cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.schedule), scheduleRoot),
                            cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.car).get(TAzCarinfoEntity_.id), carId)
                    )
            );

            predicate = cb.and(
                    predicate,
                    cb.exists(scheduleCarSubQuery)
            );
        }

        if (scheduleCarId != null) {
            Subquery<TBusScheduleCarEntity> scheduleCarSubQuery = cq.subquery(TBusScheduleCarEntity.class);
            Root<TBusScheduleCarEntity> scheduleCarRoot = scheduleCarSubQuery.from(TBusScheduleCarEntity.class);
            scheduleCarSubQuery.select(scheduleCarRoot);
            scheduleCarSubQuery.where(
                    cb.and(
                            cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.schedule), scheduleRoot),
                            cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.uuid), scheduleCarId)
                    )
            );

            predicate = cb.and(
                    predicate,
                    cb.exists(scheduleCarSubQuery)
            );
        }

        predicate = cb.and(
                predicate,
                scheduleRoot.get(TBusScheduleRelaEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id())
        );

        List<Order> orders = new ArrayList<>();
        if (orderByParam != null && orderByParam.getOrderBies().length > 0) {
            for (OrderByParam.OrderBy orderBy : orderByParam.getOrderBies()) {
                if (orderBy.getFiled().toLowerCase().equals("start".toLowerCase())) {
                    Order order = cb.asc(scheduleRoot.get(TBusScheduleRelaEntity_.starttime));
                    if (!orderBy.isAsc()) {
                        order = order.reverse();
                    }
                    orders.add(order);
                }
            }
        } else {
            orders.add(cb.asc(scheduleRoot.get(TBusScheduleRelaEntity_.starttime)));
        }
        cq.where(predicate);
        cq.orderBy(orders);

        TypedQuery<TBusScheduleRelaEntity> query = em.createQuery(cq);
        if (offset != null) {
            query.setFirstResult(offset);
        }
        if (limit != null) {
            query.setMaxResults(limit);
        }

        applySecurityFilter("schedule", principalExt);
        return query.getResultList();
    }

    public List<TBusEvaluateinfoEntity> getApplyEvaluateInfoListByTime(DateTime startTime, DateTime endTime, PrincipalExt principalExt) {
        if (startTime == null) {
            startTime = DateTimeUtil.appStartTime;
        }
        if (endTime == null) {
            endTime = DateTimeUtil.appEndTime;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusEvaluateinfoEntity> cq = cb.createQuery(TBusEvaluateinfoEntity.class);
        Root<TBusEvaluateinfoEntity> recordRoot = cq.from(TBusEvaluateinfoEntity.class);
        Predicate predicate = cb.conjunction();
        predicate = cb.and(
                predicate,
                cb.between(recordRoot.get(TBusEvaluateinfoEntity_.evaldate), startTime, endTime),
                cb.or(
                        cb.isNull(recordRoot.get(TBusEvaluateinfoEntity_.updateBySync)),
                        cb.isFalse(recordRoot.get(TBusEvaluateinfoEntity_.updateBySync))
                )
        );
        return em.createQuery(cq.where(predicate)).getResultList();
    }

    public List<TBusRecordinfoEntity> getAllScheduleCarRecordByTime(DateTime startTime, DateTime endTime, PrincipalExt principalExt) {
        if (startTime == null) {
            startTime = DateTimeUtil.appStartTime;
        }
        if (endTime == null) {
            endTime = DateTimeUtil.appEndTime;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusRecordinfoEntity> cq = cb.createQuery(TBusRecordinfoEntity.class);
        Root<TBusRecordinfoEntity> recordRoot = cq.from(TBusRecordinfoEntity.class);
        cq.where(cb.between(recordRoot.get(TBusRecordinfoEntity_.updatedate), startTime, endTime));
        return em.createQuery(cq).getResultList();
    }

    public List<TBusScheduleRelaEntity> getAllSchedulesForSync(DateTime startTime, DateTime endTime, PrincipalExt principalExt) {
        if (startTime == null) {
            startTime = DateTimeUtil.appStartTime;
        }
        if (endTime == null) {
            endTime = DateTimeUtil.appEndTime;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleRelaEntity> cq = cb.createQuery(TBusScheduleRelaEntity.class);
        Root<TBusScheduleRelaEntity> scheduleRoot = cq.from(TBusScheduleRelaEntity.class);
        cq.select(scheduleRoot);

        Predicate predicate = cb.conjunction();

        predicate = cb.and(
                predicate,
                cb.or(
                        cb.between(scheduleRoot.get(TBusScheduleRelaEntity_.insertdate), startTime, endTime),
                        cb.between(scheduleRoot.get(TBusScheduleRelaEntity_.updatedate), startTime, endTime)
                )
        );

        cq.where(predicate);
        return em.createQuery(cq).getResultList();
    }

    public int countAllScheduleWithTime(DateTime startTime, DateTime endTime, String driverId, String carId, Integer scheduleStatus, PrincipalExt principalExt) {
        if (startTime == null) {
            startTime = DateTime.now().withTimeAtStartOfDay();
        }
        if (endTime == null) {
            endTime = DateTime.now();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TBusScheduleRelaEntity> scheduleRoot = cq.from(TBusScheduleRelaEntity.class);

        cq.select(cb.countDistinct(scheduleRoot)).distinct(true);

        Predicate predicate = cb.and(
                cb.lessThanOrEqualTo(scheduleRoot.get(TBusScheduleRelaEntity_.starttime), endTime),
                cb.greaterThanOrEqualTo(scheduleRoot.get(TBusScheduleRelaEntity_.endtime), startTime)
        );

        if (driverId != null || carId != null) {
            Subquery<TBusScheduleCarEntity> scheduleCarSubQuery = prepareScheduleCarSubQuery(cb, scheduleRoot, cq, driverId, carId);
            predicate = cb.and(
                    predicate,
                    cb.exists(scheduleCarSubQuery)
            );
        }

        // null 全部
        if (scheduleStatus != null) {
            // -1 有效的调度单
            if (-1 == scheduleStatus) {
                predicate = cb.and(
                        predicate,
                        scheduleRoot.get(TBusScheduleRelaEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id())
                );
            } else {
                predicate = cb.and(
                        predicate,
                        cb.equal(scheduleRoot.get(TBusScheduleRelaEntity_.status), ScheduleStatus.valueOf(scheduleStatus))
                );
            }
        }

        Subquery<TBusScheduleCarEntity> scheduleCarSubQuery = cq.subquery(TBusScheduleCarEntity.class);
        Root<TBusScheduleCarEntity> scheduleCarRoot = scheduleCarSubQuery.from(TBusScheduleCarEntity.class);
        scheduleCarSubQuery.select(scheduleCarRoot);
        scheduleCarSubQuery.where(
                cb.and(
                        cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.schedule), scheduleRoot),
                        cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.car).get(TAzCarinfoEntity_.gpsazqk), "1")
                )
        );
        predicate = cb.and(
                predicate,
                cb.exists(scheduleCarSubQuery)
        );
        cq.where(predicate);

        applySecurityFilter("schedule_car", principalExt);
        return em.createQuery(cq).getSingleResult().intValue();
    }

    public int countAllScheduleCarWithTime(DateTime startTime, DateTime endTime, PrincipalExt principalExt) {
        if (startTime == null) {
            startTime = DateTime.now().withTimeAtStartOfDay();
        }
        if (endTime == null) {
            endTime = DateTime.now();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TBusScheduleCarEntity> scheduleCarRoot = cq.from(TBusScheduleCarEntity.class);

        cq.select(cb.countDistinct(scheduleCarRoot.get(TBusScheduleCarEntity_.car))).distinct(true);

        Predicate predicate = cb.and(
                cb.lessThanOrEqualTo(scheduleCarRoot.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.starttime), endTime),
                cb.greaterThanOrEqualTo(scheduleCarRoot.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.endtime), startTime),
                scheduleCarRoot.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id()),
                scheduleCarRoot.get(TBusScheduleCarEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id()),       // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
                cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.car).get(TAzCarinfoEntity_.gpsazqk), "1")
        );

        cq.where(predicate);

        applySecurityFilter("schedule", principalExt);
        return em.createQuery(cq).getSingleResult().intValue();
    }

    public TBusScheduleCarEntity getScheduleCarById(String scheduleCarId) {
        return em.find(TBusScheduleCarEntity.class, scheduleCarId);
    }

    private static Subquery<TBusScheduleCarEntity> prepareScheduleCarSubQuery(CriteriaBuilder cb, Root<TBusScheduleRelaEntity> scheduleRoot, CriteriaQuery criteriaQuery, String driverId, String carId) {

        Subquery<TBusScheduleCarEntity> scheduleCarSubQuery = criteriaQuery.subquery(TBusScheduleCarEntity.class);
        Root<TBusScheduleCarEntity> scheduleCarRoot = scheduleCarSubQuery.from(TBusScheduleCarEntity.class);
        scheduleCarSubQuery.select(scheduleCarRoot);
        Predicate predicate = cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.schedule), scheduleRoot);
        if (driverId != null) {
            scheduleCarSubQuery.where(
                    cb.and(
                            predicate,
                            cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.driver).get(TRsDriverinfoEntity_.uuid), driverId)
                    )
            );
        }
        if (carId != null) {
            scheduleCarSubQuery.where(
                    cb.and(
                            predicate,
                            cb.equal(scheduleCarRoot.get(TBusScheduleCarEntity_.car).get(TAzCarinfoEntity_.id), carId)
                    )
            );
        }
        return scheduleCarSubQuery;
    }

    private void prepareScheduleCarInfo(TBusScheduleCarEntity sourceScheduleCar, TBusScheduleCarEntity targetScheduleCarEntity) {
        if (sourceScheduleCar.getCar() != null) {
            targetScheduleCarEntity.setCar(em.getReference(TAzCarinfoEntity.class, sourceScheduleCar.getCar().getId()));
        }
        if (sourceScheduleCar.getDriver() != null) {
            targetScheduleCarEntity.setDriver(em.getReference(TRsDriverinfoEntity.class, sourceScheduleCar.getDriver().getUuid()));
        }
        targetScheduleCarEntity.setSchcarno(sourceScheduleCar.getSchcarno());
        if (sourceScheduleCar.getSchedule() != null) {
            if (sourceScheduleCar.getSchedule().getStartpoint() == null) {
                targetScheduleCarEntity.setSchedule(sourceScheduleCar.getSchedule());
            } else {
                TBusScheduleRelaEntity scheduleRelaEntity = em.getReference(TBusScheduleRelaEntity.class, sourceScheduleCar.getSchedule().getUuid());
                targetScheduleCarEntity.setSchedule(scheduleRelaEntity);
            }
        }
    }

    private static class CarAndDriverInfo {
        String scheduleCarId;
        String carId;
        String driverId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CarAndDriverInfo that = (CarAndDriverInfo) o;

            if (scheduleCarId != null ? !scheduleCarId.equals(that.scheduleCarId) : that.scheduleCarId != null) return false;
            if (carId != null ? !carId.equals(that.carId) : that.carId != null) return false;
            return driverId != null ? driverId.equals(that.driverId) : that.driverId == null;

        }

        @Override
        public int hashCode() {
            int result = scheduleCarId != null ? scheduleCarId.hashCode() : 0;
            result = 31 * result + (carId != null ? carId.hashCode() : 0);
            result = 31 * result + (driverId != null ? driverId.hashCode() : 0);
            return result;
        }
    }
}
