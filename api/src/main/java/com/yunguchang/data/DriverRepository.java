package com.yunguchang.data;

import com.google.common.collect.Iterables;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.common.DriverStatus;
import com.yunguchang.model.common.OnWorkStateEnum;
import com.yunguchang.model.common.ScheduleStatus;
import com.yunguchang.model.persistence.*;
import com.yunguchang.sam.PrincipalExt;
import com.yunguchang.utils.tools.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by WHB on 9/18/2015.
 */
@Stateless
public class DriverRepository extends GenericRepository {


    private static final int pageSize = 20;

    @Inject
    private Logger logger;

    /**
     * 按照查询条件进行查询 （驾驶员状态,姓名,手机号）
     *
     * @return
     */
    public List<TRsDriverinfoEntity> queryDrivers(DriverStatus status, String keyword, String fleetId, Integer offset, Integer limit, PrincipalExt principalExt) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TRsDriverinfoEntity> criteriaQuery = cb.createQuery(TRsDriverinfoEntity.class);

        Root<TRsDriverinfoEntity> driverRoot = criteriaQuery.from(TRsDriverinfoEntity.class);
        Fetch<TRsDriverinfoEntity, TSysOrgEntity> fetchFleet = driverRoot.fetch(TRsDriverinfoEntity_.department);
        Join<TRsDriverinfoEntity, TSysOrgEntity> joinFleet = (Join<TRsDriverinfoEntity, TSysOrgEntity>) fetchFleet;
        criteriaQuery.select(driverRoot);

        Predicate predicate = cb.equal(driverRoot.get(TRsDriverinfoEntity_.status), "1");
        if (keyword != null) {
            predicate = cb.and(predicate, cb.or(
                    cb.like(driverRoot.get(TRsDriverinfoEntity_.driverno), "%" + keyword + "%"),
                    cb.like(driverRoot.get(TRsDriverinfoEntity_.drivername), "%" + keyword + "%"),
                    cb.like(driverRoot.get(TRsDriverinfoEntity_.phone), "%" + keyword + "%"),
                    cb.like(driverRoot.get(TRsDriverinfoEntity_.mobile), "%" + keyword + "%")));
        }

        if (fleetId != null) {
            predicate = cb.and(predicate,
                    cb.equal(joinFleet.get(TSysOrgEntity_.orgid), fleetId));
        }

        //按状态判断
        if (status != null) {

            //查找考勤表
            //select kqXX from T_RS_KQGL where kqdate='xxxx' and DRIVERID=<uuid>
            Subquery<String> dutyQuery = criteriaQuery.subquery(String.class);
            Root<TRsKqglEntity> duty = dutyQuery.from(TRsKqglEntity.class);
            DateTime now = DateTime.now();
            String fieldName = String.format("KQ%02d", now.getDayOfMonth());
            String yearAndMonth = String.format("%04d%02d", now.getYear(), now.getMonthOfYear());
            dutyQuery.select(duty.get(fieldName).as(String.class));
            dutyQuery.where(
                    cb.and(
                            cb.equal(duty.get(TRsKqglEntity_.kqDate), yearAndMonth),
                            cb.equal(duty.get(TRsKqglEntity_.driver).get(TRsDriverinfoEntity_.uuid), driverRoot.get(TRsDriverinfoEntity_.uuid))
                    )


            );
            if (status.equals(DriverStatus.IDLE) || status.equals(DriverStatus.WORK)) {

                //出勤状态
                predicate = cb.and(predicate,
                        cb.or(
                                cb.isNull(dutyQuery),
                                cb.equal(dutyQuery, "0"),   // 出勤
                                cb.equal(dutyQuery, "4"),   // 加班
                                cb.equal(dutyQuery, "5"),   // 拖班
                                cb.equal(dutyQuery, "6"),   // 抢修
                                cb.equal(dutyQuery, "9"),   // 出差
                                cb.equal(dutyQuery, "a")   // 通宵班
                        )
                );

                //车辆调度表
                Subquery<TBusScheduleRelaEntity> scheduleQuery = criteriaQuery.subquery(TBusScheduleRelaEntity.class);
                Root<TBusScheduleRelaEntity> schedule = scheduleQuery.from(TBusScheduleRelaEntity.class);
                scheduleQuery.select(schedule);
                ListJoin<TBusScheduleRelaEntity, TBusScheduleCarEntity> joinScheduleCar = schedule.join(TBusScheduleRelaEntity_.scheduleCars);
                scheduleQuery.where(
                        cb.and(
                                cb.equal(
                                        schedule.get(TBusScheduleRelaEntity_.status), ScheduleStatus.AWAITING.id()
                                ),
                                cb.equal(joinScheduleCar
                                                .join(TBusScheduleCarEntity_.driver)
                                                .get(TRsDriverinfoEntity_.uuid),
                                        driverRoot.get(TRsDriverinfoEntity_.uuid)),
                                cb.equal(joinScheduleCar.get(TBusScheduleCarEntity_.status), ScheduleStatus.AWAITING.id()),
                                cb.lessThanOrEqualTo(schedule.get(TBusScheduleRelaEntity_.starttime), now),
                                cb.greaterThanOrEqualTo(schedule.get(TBusScheduleRelaEntity_.endtime), now)
                        )

                );

                Predicate scheduleExistsPredicate = cb.exists(scheduleQuery);
                if (status.equals(DriverStatus.IDLE)) {
                    predicate = cb.and(predicate,
                            cb.not(scheduleExistsPredicate)
                    );
                } else if (status.equals(DriverStatus.WORK)) {
                    predicate = cb.and(predicate,
                            scheduleExistsPredicate
                    );
                }


            } else if (status.equals(DriverStatus.LEAVE)) {
                predicate = cb.and(predicate,
//                        1	病假
//                        2	事假
//                        3	年假
//                        7	公休
//                        8	调修
                        dutyQuery.in("1", "2", "3", "7", "8")

                );
            }
        }

        criteriaQuery.where(predicate);


        criteriaQuery.orderBy(cb.asc(cb.function("casttogbk", String.class,
                cb.trim(driverRoot.get(TRsDriverinfoEntity_.drivername))

        )));

        applySecurityFilter("drivers", principalExt);


        TypedQuery<TRsDriverinfoEntity> query = em.createQuery(criteriaQuery);
        if (offset != null) {
            query.setFirstResult(offset);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }


    /**
     * 查看驾驶员的详细信息
     *
     * @param driverId
     * @param principalExt
     * @return
     */
    public TRsDriverinfoEntity loadDriverById(String driverId, PrincipalExt principalExt) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TRsDriverinfoEntity> criteriaQuery = criteriaBuilder.createQuery(TRsDriverinfoEntity.class);

        // where driver left join scheduleCar on ... left join scheduleRel on ... left join car
        Root<TRsDriverinfoEntity> driverRoot = criteriaQuery.from(TRsDriverinfoEntity.class);
        driverRoot.fetch(TRsDriverinfoEntity_.department);

        criteriaQuery.where(
                criteriaBuilder.equal(
                        driverRoot.get(TRsDriverinfoEntity_.uuid),
                        driverId
                )
        );
        criteriaQuery.select(driverRoot);
        applySecurityFilter("drivers", principalExt);

        return Iterables.getFirst(em.createQuery(criteriaQuery).getResultList(), null);
    }


    public boolean isOnWorkingByDriverIdAndDate(String driverId, DateTime dateTime, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<TRsKqglEntity> attendanceRoot = cq.from(TRsKqglEntity.class);
        String fieldName = String.format("KQ%02d", dateTime.getDayOfMonth());
        String yearAndMonth = String.format("%04d%02d", dateTime.getYear(), dateTime.getMonthOfYear());
        cq.select(
                attendanceRoot.get(fieldName).as(String.class)
        );
        cq.where(
                cb.and(
                        cb.equal(
                                attendanceRoot.get(TRsKqglEntity_.driver).get(TRsDriverinfoEntity_.uuid), driverId
                        ),
                        cb.equal(
                                attendanceRoot.get(TRsKqglEntity_.kqDate), yearAndMonth
                        )
                )

        );

        applySecurityFilter("attendance", principalExt);
        String result = Iterables.getFirst(em.createQuery(cq).getResultList(), null);
        if (result == null) {
            return true;
        } else {
            return OnWorkStateEnum.onWorkState.contains(result);
        }

    }

    public List<TBusScheduleRelaEntity> getDriverSchedules(String driverId, DateTime startTime, DateTime endTime, PrincipalExt principalExt) {
        if (driverId == null) {
            return Collections.EMPTY_LIST;
        }
        if (startTime == null) {
            startTime = DateTimeUtil.appStartTime;
        }
        if (endTime == null) {
            endTime = DateTimeUtil.appEndTime;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleRelaEntity> cq = cb.createQuery(TBusScheduleRelaEntity.class);
        Root<TBusScheduleCarEntity> scheduleCar = cq.from(TBusScheduleCarEntity.class);
        Join<TBusScheduleCarEntity, TBusScheduleRelaEntity> schedule = scheduleCar.join(TBusScheduleCarEntity_.schedule);
        cq.select(scheduleCar.get(TBusScheduleCarEntity_.schedule));
        Predicate predicate = cb.equal(scheduleCar.join(TBusScheduleCarEntity_.driver).get(TRsDriverinfoEntity_.uuid), driverId);

        predicate = cb.and(
                predicate,
                schedule.get(TBusScheduleRelaEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id()),       // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
                scheduleCar.get(TBusScheduleCarEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id()),       // 当前状态   0:未生效  1:待出车   2:返回   3: 调度作废
                cb.greaterThanOrEqualTo(schedule.get(TBusScheduleRelaEntity_.endtime), startTime),
                cb.lessThanOrEqualTo(schedule.get(TBusScheduleRelaEntity_.starttime), endTime)
        );

        cq.where(predicate);
        cq.orderBy(cb.asc(schedule.get(TBusScheduleRelaEntity_.starttime)));
        applySecurityFilter("schedule", principalExt);

        return em.createQuery(cq).getResultList();
    }

    /**
     * 更新驾驶员信息
     *
     * @param driverInfo 驾驶员信息
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TRsDriverinfoEntity createDriverWithPermission(TRsDriverinfoEntity driverInfo, PrincipalExt principalExt) {
        if (driverInfo == null) {
            throw new InvalidParameterException("Driver info is not valid!");
        }

        if (StringUtils.isNotBlank(driverInfo.getUuid())) {
            TRsDriverinfoEntity driverinfoEntity = em.find(TRsDriverinfoEntity.class, driverInfo.getUuid());
            if (null != driverinfoEntity) {
                throw new KeyAlreadyExistsException("Driver is already exist!");
            }
        }
        // 更新内容
        // 所属部门
        if (driverInfo.getDepartment() != null && StringUtils.isNotBlank(driverInfo.getDepartment().getOrgid())) {
            TSysOrgEntity department = em.find(TSysOrgEntity.class, driverInfo.getDepartment().getOrgid());
            if (null == department) {
                throw new EntityNotFoundException("Fleet is not exist!");
            }
            driverInfo.setDepartment(department);
        }

        driverInfo.setEnabled("1");            // 设置为启用

        if (driverInfo.getUuid() == null) {
            em.persist(driverInfo);
        } else {
            driverInfo = em.merge(driverInfo);
        }

        return driverInfo;
    }

    /**
     * 更新驾驶员信息
     *
     * @param driverId   驾驶员ID
     * @param driverInfo 驾驶员信息
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TRsDriverinfoEntity updateDriverWithPermission(String driverId, TRsDriverinfoEntity driverInfo, PrincipalExt principalExt) {
        if (driverId == null) {
            throw new EntityNotFoundException("Driver is not found!");
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<TRsDriverinfoEntity> cq = cb.createQuery(TRsDriverinfoEntity.class);
        Root<TRsDriverinfoEntity> driverRoot = cq.from(TRsDriverinfoEntity.class);
        cq.where(cb.equal(driverRoot.get(TRsDriverinfoEntity_.uuid), driverId));

        List<TRsDriverinfoEntity> results = em.createQuery(cq).getResultList();
        TRsDriverinfoEntity driverEntity;
        boolean insertFlg = false;
        if (results.size() == 0) {
            driverEntity = new TRsDriverinfoEntity();
            driverEntity.setUuid(driverId);
            insertFlg = true;
        } else {
            driverEntity = results.get(0);
        }
        // 更新内容
        // 所属部门
        if (driverInfo.getDepartment() == null || driverEntity.getDepartment() == null ||
                !driverEntity.getDepartment().getOrgid().equals(driverInfo.getDepartment().getOrgid())) {
            // 原来没有车队  或者  新的车队信息和旧的车队信息不同
            CriteriaQuery<TSysOrgEntity> cqOrg = cb.createQuery(TSysOrgEntity.class);
            Root<TSysOrgEntity> orgRoot = cqOrg.from(TSysOrgEntity.class);
            cqOrg.where(cb.equal(orgRoot.get(TSysOrgEntity_.orgid), driverInfo.getDepartment().getOrgid()));

            List<TSysOrgEntity> orgResults = em.createQuery(cqOrg).getResultList();
            if (orgResults.size() == 1) {
                driverEntity.setDepartment(orgResults.get(0));
            } else if (driverInfo.getDepartment().getOrgid() != null) {
                // fleet is not exist, return null to tell caller the data is wrong.
            }
        }

        // 姓名
        driverEntity.setDrivername(driverInfo.getDrivername());
        // 驾驶员工号
        driverEntity.setDriverno(driverInfo.getDriverno());
        // 职位
        driverEntity.setZw(driverInfo.getZw());
        // 手机号码
        driverEntity.setMobile(driverInfo.getMobile());
        // 短号
        driverEntity.setPhone(driverInfo.getPhone());
        // 驾照类型
        driverEntity.setDrivecartype(driverInfo.getDrivecartype());

        driverEntity.setEnabled("1");            // 设置为启用

        if (insertFlg) {
            em.persist(driverEntity);
        }
        return driverEntity;
    }

    /**
     * 逻辑删除驾驶员信息
     *
     * @param driverId 驾驶员ID
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean deleteDriverWithPermission(String driverId, PrincipalExt principalExt) {
        if (driverId == null) {
            throw new EntityNotFoundException("Driver is not found!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<TRsDriverinfoEntity> cq = cb.createQuery(TRsDriverinfoEntity.class);
        Root<TRsDriverinfoEntity> carRoot = cq.from(TRsDriverinfoEntity.class);
        cq.where(cb.equal(carRoot.get(TRsDriverinfoEntity_.uuid), driverId),
                cb.equal(carRoot.get(TRsDriverinfoEntity_.status), "1"));

        List<TRsDriverinfoEntity> results = em.createQuery(cq).getResultList();

        if (results.size() != 1) {
            throw new EntityNotFoundException("Driver is not found!");
        }
        // 状态，默认为1，逻辑删除时修改为0
        results.get(0).setStatus("0");

        return true;
    }

    /**
     * 更新驾驶员请假信息
     *
     * @param driverId 驾驶员ID
     * @param kqglInfo 考勤信息
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TRsKqglEntity updateLeaveInfoWithPermission(String driverId, TRsKqglEntity kqglInfo, PrincipalExt principalExt) {
        if (driverId == null || kqglInfo == null) {
            throw new EntityNotFoundException("Driver or attendanceInfo is not correct!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<TRsDriverinfoEntity> cq = cb.createQuery(TRsDriverinfoEntity.class);
        Root<TRsDriverinfoEntity> driverRoot = cq.from(TRsDriverinfoEntity.class);
        cq.where(cb.or(
                cb.equal(driverRoot.get(TRsDriverinfoEntity_.uuid), driverId),
                cb.equal(driverRoot.get(TRsDriverinfoEntity_.driverno), driverId)));

        List<TRsDriverinfoEntity> results = em.createQuery(cq).getResultList();
        if (results.size() == 0) {
            //error no such driver ??
            throw new EntityNotFoundException("Driver is not found!");
        }
        TRsDriverinfoEntity driverEntity = results.get(0);

        CriteriaQuery<TRsKqglEntity> cqKq = cb.createQuery(TRsKqglEntity.class);
        Root<TRsKqglEntity> kqglRoot = cqKq.from(TRsKqglEntity.class);
        String yearMonth = kqglInfo.getKqDate();
        if (yearMonth == null) {
            DateTime today = DateTime.now();
            yearMonth = String.valueOf(today.getYear()) + today.getMonthOfYear();
        }
        cqKq.where(cb.equal(kqglRoot.get(TRsKqglEntity_.driver).get(TRsDriverinfoEntity_.uuid), driverId),
                cb.equal(kqglRoot.get(TRsKqglEntity_.kqDate), yearMonth));

        List<TRsKqglEntity> kqResults = em.createQuery(cqKq).getResultList();
        TRsKqglEntity kqglEntity = null;
        // 新的月份, 需要插入新的数据
        if (kqResults.size() == 0) {
            // new entity
            kqglEntity = new TRsKqglEntity();
            kqglEntity.setDriver(driverEntity);
            kqglEntity.setDepartment(driverEntity.getDepartment());
            kqglEntity.setDriverName(driverEntity.getDrivername());
            kqglEntity.setKqDate(kqglInfo.getKqDate());
            kqglEntity.setUpdateDate(new Date());
            populateKQInfo(kqglEntity, kqglInfo);
            kqglEntity.setStatus("0");
            em.persist(kqglEntity);
        } else if (kqResults.size() == 1) {
            kqglEntity = kqResults.get(0);
            populateKQInfo(kqglEntity, kqglInfo);
            kqglEntity.setStatus("0");
        }

        return kqglEntity;
    }

    // 调用 反射的形式进行设置数据
    private void populateKQInfo(TRsKqglEntity kqglEntity, TRsKqglEntity kqglInfo) {
        for (int date = 1; date <= 31; date++) {
            String dateStr = ("0" + date);
            dateStr = dateStr.substring(dateStr.length() - 2);
            String setterName = "setKQ" + dateStr;
            String getterName = "getKQ" + dateStr;
            try {
                Method getMethod = kqglInfo.getClass().getMethod(getterName);
                Object data = getMethod.invoke(kqglInfo);
                if (data != null) {
                    Method setMethod = kqglEntity.getClass().getMethod(setterName, new Class[]{String.class});
                    setMethod.invoke(kqglEntity, new String[]{data.toString()});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public TRsDriverinfoEntity validDriverByMobile(String mobile) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TRsDriverinfoEntity> cq = cb.createQuery(TRsDriverinfoEntity.class);
        Root<TRsDriverinfoEntity> root = cq.from(TRsDriverinfoEntity.class);
        cq.select(root);
        cq.where(
                cb.and(
                        cb.equal(root.get(TRsDriverinfoEntity_.enabled), "1"),
                        cb.equal(root.get(TRsDriverinfoEntity_.mobile), mobile)
                )

        );

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);

    }
}
