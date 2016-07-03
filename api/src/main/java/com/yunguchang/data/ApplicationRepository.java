package com.yunguchang.data;

import com.google.common.collect.Iterables;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.SyncEntityConvert;
import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.*;
import com.yunguchang.sam.PrincipalExt;
import com.yunguchang.utils.tools.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

/**
 * Created by gongy on 9/27/2015.
 */

@Stateless
public class ApplicationRepository extends GenericRepository {
    @Inject
    private Logger logger;

    @Inject
    private ScheduleRepository scheduleRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private SyncDataStatusRepository syncDataStatusRepository;
    @Inject
    private BusBusinessRelationRepository busBusinessRelationRepository;

    public List<TBusApplyinfoEntity> getAllApplications(String coordinatorUserId,
                                                        String reasonType,
                                                        String status,
                                                        DateTime startBefore,
                                                        DateTime startAfter,
                                                        DateTime endBefore,
                                                        DateTime endAfter,
                                                        Integer offset,
                                                        Integer limit,
                                                        OrderByParam orderByParam,
                                                        PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusApplyinfoEntity> cq = cb.createQuery(TBusApplyinfoEntity.class);
        Root<TBusApplyinfoEntity> applyRoot = cq.from(TBusApplyinfoEntity.class);
        applyRoot.fetch(TBusApplyinfoEntity_.passenger);
        Fetch<TBusApplyinfoEntity, TSysUserEntity> fetchCoordinator = applyRoot.fetch(TBusApplyinfoEntity_.coordinator);
        applyRoot.fetch(TBusApplyinfoEntity_.department);
        Fetch<TBusApplyinfoEntity, TBusScheduleRelaEntity> scheduleFetch = applyRoot.fetch(TBusApplyinfoEntity_.schedule, JoinType.LEFT);
        scheduleFetch.fetch(TBusScheduleRelaEntity_.senduser, JoinType.LEFT);
        scheduleFetch.fetch(TBusScheduleRelaEntity_.reciveuser, JoinType.LEFT);
        Fetch<TBusScheduleRelaEntity, TBusScheduleCarEntity> fetchScheduleCar = scheduleFetch.fetch(TBusScheduleRelaEntity_.scheduleCars, JoinType.LEFT);
        fetchScheduleCar.fetch(TBusScheduleCarEntity_.car, JoinType.LEFT).fetch(TAzCarinfoEntity_.depot, JoinType.LEFT);
        fetchScheduleCar.fetch(TBusScheduleCarEntity_.driver, JoinType.LEFT).fetch(TRsDriverinfoEntity_.department, JoinType.LEFT);
        Predicate predicate = cb.conjunction();
        if (coordinatorUserId != null) {
            Join<TBusApplyinfoEntity, TSysUserEntity> joinCoordinator = (Join<TBusApplyinfoEntity, TSysUserEntity>) fetchCoordinator;
            predicate = cb.and(
                    predicate,
                    cb.equal(
                            joinCoordinator.get(TSysUserEntity_.userid), coordinatorUserId
                    )
            );
        }
        if (reasonType != null) {
            predicate = cb.and(
                    predicate,
                    cb.equal(
                            applyRoot.get(TBusApplyinfoEntity_.reason), reasonType
                    )
            );
        }
        if (status != null) {
            if (status.indexOf(":") < 0) {
                predicate = cb.and(
                        predicate,
                        cb.equal(
                                applyRoot.get(TBusApplyinfoEntity_.status), status
                        )
                );
            } else {
                String[] statusList = status.split(":");
                predicate = cb.and(
                        predicate,
                        applyRoot.get(TBusApplyinfoEntity_.status).in(Arrays.asList(statusList))
                );
            }
        }

        if (startBefore != null) {
            predicate = cb.and(
                    predicate,
                    cb.lessThanOrEqualTo(
                            applyRoot.get(TBusApplyinfoEntity_.begintime), startBefore
                    )
            );
        }

        if (startAfter != null) {
            predicate = cb.and(
                    predicate,
                    cb.greaterThanOrEqualTo(
                            applyRoot.get(TBusApplyinfoEntity_.begintime), startAfter
                    )
            );
        }


        if (endBefore != null) {
            predicate = cb.and(
                    predicate,
                    cb.lessThanOrEqualTo(
                            applyRoot.get(TBusApplyinfoEntity_.endtime), endBefore
                    )
            );
        }

        if (endAfter != null) {
            predicate = cb.and(
                    predicate,
                    cb.greaterThanOrEqualTo(
                            applyRoot.get(TBusApplyinfoEntity_.endtime), endAfter
                    )
            );
        }

        cq.where(predicate);

        List<Order> orders = new ArrayList<>();
        if (orderByParam != null) {
            for (OrderByParam.OrderBy orderBy : orderByParam.getOrderBies()) {
                Order order = null;
                if (orderBy.getFiled().toLowerCase().equals("start".toLowerCase())) {
                    order = cb.desc(applyRoot.get(TBusApplyinfoEntity_.begintime));
                }
                if (order != null && !orderBy.isAsc()) {
                    order = order.reverse();
                }
                if (order != null) {
                    orders.add(order);
                }
            }

        }
        if (orders.size() == 0) {
            cq.orderBy(
                    cb.desc(applyRoot.get(TBusApplyinfoEntity_.begintime))
            );
        } else {
            cq.orderBy(orders);
        }


        TypedQuery<TBusApplyinfoEntity> query = em.createQuery(cq);

        if (offset != null) {
            query.setFirstResult(offset);
        }
        if (limit != null) {
            query.setMaxResults(limit);
        }

        applySecurityFilter("applications", principalExt);

        return query.getResultList();
    }

    public List<TBusApplyinfoEntity> getApplicationForSync(DateTime startTime, DateTime endTime, String status, Boolean isSend, PrincipalExt principalExt) {
        if (startTime == null) {
            startTime = DateTimeUtil.appStartTime;
        }
        if (endTime == null) {
            endTime = DateTimeUtil.appEndTime;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusApplyinfoEntity> cq = cb.createQuery(TBusApplyinfoEntity.class);
        Root<TBusApplyinfoEntity> applyRoot = cq.from(TBusApplyinfoEntity.class);
        applyRoot.fetch(TBusApplyinfoEntity_.passenger);
        applyRoot.fetch(TBusApplyinfoEntity_.coordinator);
        applyRoot.fetch(TBusApplyinfoEntity_.department);
        applyRoot.fetch(TBusApplyinfoEntity_.senduser, JoinType.LEFT);

        Predicate predicate = cb.conjunction();

        predicate = cb.and(
                predicate,
                cb.or(
                        cb.isNull(applyRoot.get(TBusApplyinfoEntity_.updateBySync)),
                        cb.isFalse(applyRoot.get(TBusApplyinfoEntity_.updateBySync))
                )
        );
        if(status != null) {
            predicate = cb.and(
                    predicate,
                    // "2" -> 部门审批 "3" -> 申请成功 "4" -> 调度成功
                    cb.equal(applyRoot.get(TBusApplyinfoEntity_.status), status)
            );
        }
        if(isSend != null && isSend) {
            predicate = cb.and(
                    predicate,
                    cb.equal(applyRoot.get(TBusApplyinfoEntity_.issend), "1")   // 总调
            );
        }
        if("4".equals(status)) {
            Fetch<TBusApplyinfoEntity, TBusScheduleRelaEntity> scheduleFetch = applyRoot.fetch(TBusApplyinfoEntity_.schedule, JoinType.LEFT);
            Join<TBusApplyinfoEntity, TBusScheduleRelaEntity> scheduleJoin = (Join<TBusApplyinfoEntity, TBusScheduleRelaEntity>) scheduleFetch;
            scheduleJoin.fetch(TBusScheduleRelaEntity_.reciveuser, JoinType.LEFT);
            Fetch<TBusScheduleRelaEntity, TBusScheduleCarEntity> fetchScheduleCar = scheduleFetch.fetch(TBusScheduleRelaEntity_.scheduleCars, JoinType.LEFT);
            fetchScheduleCar.fetch(TBusScheduleCarEntity_.car, JoinType.LEFT).fetch(TAzCarinfoEntity_.depot, JoinType.LEFT);
            fetchScheduleCar.fetch(TBusScheduleCarEntity_.driver, JoinType.LEFT).fetch(TRsDriverinfoEntity_.department, JoinType.LEFT);
            predicate =  cb.and(
                    predicate,
                    cb.or(
                            cb.between(applyRoot.get(TBusApplyinfoEntity_.updatedate), startTime, endTime),
                            cb.between(scheduleJoin.get(TBusScheduleRelaEntity_.updatedate), startTime, endTime)
                    )
            );
        } else {
            predicate =  cb.and(
                    predicate,
                    cb.between(applyRoot.get(TBusApplyinfoEntity_.updatedate), startTime, endTime)
            );
        }

        cq.where(predicate);

        TypedQuery<TBusApplyinfoEntity> query = em.createQuery(cq);

//        applySecurityFilter("applications", principalExt);
        return query.getResultList();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity createApplicationForSync(TBusApplyinfoEntity application, PrincipalExt principalExt) {
        applySecurityFilter("applications", principalExt);

        TBusMainUserInfoEntity passenger = em.find(TBusMainUserInfoEntity.class, application.getPassenger().getUuid());
        if (passenger == null) {
//            throw new EntityNotFoundException("Passenger is not exist");      // 临时申请单的 passenger 属于新建
        } else {
            application.setPassenger(passenger);
        }
        TSysOrgEntity deparment = em.find(TSysOrgEntity.class, application.getDepartment().getOrgid());
        if (deparment == null) {
            throw new EntityNotFoundException("Department is not exist");
        }
        application.setDepartment(deparment);
        TSysUserEntity coordinator = em.find(TSysUserEntity.class, application.getCoordinator().getUserid());
        if (coordinator == null) {
            throw new EntityNotFoundException("Coordinator is not exist");
        }
        application.setCoordinator(coordinator);
        if(application.getSenduser() != null) {
            application.setSenduser(em.find(TSysUserEntity.class, application.getSenduser().getUserid()));
        }

        if(application.getUpdateBySync() != null && application.getUpdateBySync()) {
            application.setUpdateBySync(true);
        } else {
            application.setUpdateBySync(false);
        }

//        application.setSfgf("1");   // 是否规范
        application.setApplydate(DateTime.now());
        if(principalExt != null) {
            application.setUpdateuser(principalExt.getUserIdOrNull());
        }
        if (application.getUuid() == null) {
            em.persist(application);
        } else {
            application = em.merge(application);
        }
        //em.refresh(applyinfoEntity);
        return application;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity createApplication(TBusApplyinfoEntity application, PrincipalExt principalExt) {
        applySecurityFilter("applications", principalExt);

        TBusApplyinfoEntity applyinfoEntity = new TBusApplyinfoEntity();
//        applyinfoEntity.setSfgf("1");
        applyinfoEntity.setApplydate(DateTime.now());
        applyinfoEntity.setStatus(ApplyStatus.valueOf(Integer.valueOf(application.getStatus())).toStringValue());
        merge(application, applyinfoEntity);

        DateTime fifteenHour = DateTime.now().withHourOfDay(15).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        if(applyinfoEntity.getApplydate().isAfter(fifteenHour)) {
            applyinfoEntity.setUsetype("2");    // 1:正常申请   2:临时申请  3: 补录
        }
        if(applyinfoEntity.getUuid() == null) {
            em.persist(applyinfoEntity);
        } else {
            applyinfoEntity = em.merge(applyinfoEntity);
        }
        //em.refresh(applyinfoEntity);
        return applyinfoEntity;
    }

    public TBusApplyinfoEntity getApplicationById(String id, PrincipalExt principalExt) {
        applySecurityFilter("applications", principalExt);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusApplyinfoEntity> cq = cb.createQuery(TBusApplyinfoEntity.class);
        Root<TBusApplyinfoEntity> applyRoot = cq.from(TBusApplyinfoEntity.class);
        applyRoot.fetch(TBusApplyinfoEntity_.passenger, JoinType.LEFT);
        applyRoot.fetch(TBusApplyinfoEntity_.senduser, JoinType.LEFT);
        cq.where(
                cb.equal(applyRoot.get(TBusApplyinfoEntity_.uuid), id)
        );
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    public List<TBusApplyinfoEntity> getApplicationByScheduleId(String scheduleId, PrincipalExt principalExt) {
        applySecurityFilter("applications", principalExt);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusApplyinfoEntity> cq = cb.createQuery(TBusApplyinfoEntity.class);
        Root<TBusApplyinfoEntity> applyRoot = cq.from(TBusApplyinfoEntity.class);
        Fetch<TBusApplyinfoEntity, TBusScheduleRelaEntity> scheduleFetch = applyRoot.fetch(TBusApplyinfoEntity_.schedule, JoinType.LEFT);
        Join<TBusApplyinfoEntity, TBusScheduleRelaEntity> scheduleJoin = (Join<TBusApplyinfoEntity, TBusScheduleRelaEntity>) scheduleFetch;
        Predicate predicate = cb.conjunction();
        predicate = cb.and(
                predicate,
                cb.equal(scheduleJoin.get(TBusScheduleRelaEntity_.uuid), scheduleId)
        );
        cq.where(predicate);
        return em.createQuery(cq).getResultList();
    }


    public List<TBusApplyinfoEntity> getApplicationWithDetailByScheduleId(String scheduleId, PrincipalExt principalExt) {
        applySecurityFilter("applications", principalExt);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusApplyinfoEntity> cq = cb.createQuery(TBusApplyinfoEntity.class);
        Root<TBusApplyinfoEntity> applyRoot = cq.from(TBusApplyinfoEntity.class);
        Fetch<TBusApplyinfoEntity, TBusScheduleRelaEntity> scheduleFetch = applyRoot.fetch(TBusApplyinfoEntity_.schedule, JoinType.LEFT);
        applyRoot.fetch(TBusApplyinfoEntity_.fleet);
        applyRoot.fetch(TBusApplyinfoEntity_.passenger);
        applyRoot.fetch(TBusApplyinfoEntity_.coordinator);
        applyRoot.fetch(TBusApplyinfoEntity_.department);
        Join<TBusApplyinfoEntity, TBusScheduleRelaEntity> scheduleJoin = (Join<TBusApplyinfoEntity, TBusScheduleRelaEntity>) scheduleFetch;
        Predicate predicate = cb.conjunction();
        predicate = cb.and(
                predicate,
                cb.equal(scheduleJoin.get(TBusScheduleRelaEntity_.uuid), scheduleId)
        );
        cq.where(predicate);
        return em.createQuery(cq).getResultList();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity cancelApplication(String id, TBusApplyinfoEntity application, PrincipalExt principalExt) {
        TBusApplyinfoEntity applyinfoEntity = em.find(TBusApplyinfoEntity.class, id);
        if (applyinfoEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, id);
        }
        merge(application, applyinfoEntity);
        applyinfoEntity.setStatus(ApplyStatus.APPLY_CANCEL.toStringValue());
        String scheduleId = null;
        if(applyinfoEntity.getSchedule() != null) {
            scheduleId = applyinfoEntity.getSchedule().getUuid();
        }
        applyinfoEntity.setSchedule(null);
        em.flush();
        em.refresh(applyinfoEntity);
        if(scheduleId != null) {
            List<TBusApplyinfoEntity> applyInfoList = getApplicationByScheduleId(scheduleId, principalExt);
            if (applyInfoList.isEmpty()) {  // 单个申请单对应一个调度单时，需要把所有的 调度单 派车单删除
                TBusScheduleRelaEntity scheduleRelaEntity = em.find(TBusScheduleRelaEntity.class, scheduleId);
                if(scheduleRelaEntity != null && scheduleRelaEntity.getScheduleCars().size() != 0) {
                    for (TBusScheduleCarEntity scheduleCarEntity : scheduleRelaEntity.getScheduleCars()) {
                        scheduleCarEntity.setStatus(ScheduleStatus.CANCELED.id());
                    }
                    scheduleRelaEntity.setStatus(ScheduleStatus.CANCELED.id());
                }
            }
        }
        applyinfoEntity = em.find(TBusApplyinfoEntity.class, id);
        return applyinfoEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity devolveApply(String applyId, String applyNo, String isSend, String status, String reason, Boolean bySync, PrincipalExt principalExt) {
        if(applyId ==null && applyNo == null) {
            throw new InvalidParameterException("Approve info can not be null!");
        }
        TBusApplyinfoEntity applyinfoEntity;
        if(applyId != null) {
            applyinfoEntity = em.find(TBusApplyinfoEntity.class, applyId);
        } else {
            applyinfoEntity = getApplicationByNo(applyNo, principalExt);
        }
        if (applyinfoEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applyId);
        }
        applyinfoEntity.setIssend("1".equals(isSend) ? "1" : "0");      // 1 总调 0 非总调
//        applyinfoEntity.setStatus("3");      // status = "3"

        applyinfoEntity.setReasonms(reason);

        if(bySync != null && bySync) {
            applyinfoEntity.setUpdateBySync(true);
        } else {
            applyinfoEntity.setUpdateBySync(false);
        }

        return applyinfoEntity;
    }
    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity retreatDevolveApply(String applyId, String applyNo, Boolean bySync, PrincipalExt principalExt) {
        if(applyId ==null && applyNo == null) {
            throw new InvalidParameterException("Approve info can not be null!");
        }
        TBusApplyinfoEntity applyinfoEntity;
        if(applyId != null) {
            applyinfoEntity = em.find(TBusApplyinfoEntity.class, applyId);
        } else {
            applyinfoEntity = getApplicationByNo(applyNo, principalExt);
        }
        if (applyinfoEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applyId);
        }
        applyinfoEntity.setIssend("0");     // 1 总调 0 非总调
        applyinfoEntity.setStatus(ApplyStatus.APPLY.toStringValue());     // 申请调度

        if(bySync != null && bySync) {
            applyinfoEntity.setUpdateBySync(true);
        } else {
            applyinfoEntity.setUpdateBySync(false);
        }

        return applyinfoEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity concertApply(String applyId, String sendUserId, String sscd, String isSend, Boolean bySync, PrincipalExt principalExt) {
        TBusApplyinfoEntity applyinfoEntity = em.find(TBusApplyinfoEntity.class, applyId);
        if (applyinfoEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applyId);
        }
        if(StringUtils.isNotBlank(sendUserId)) {
            TSysUserEntity userEntity = em.find(TSysUserEntity.class, sendUserId);
            if(userEntity == null) {
                throw new EntityNotFoundException("User is not found!");
            }
            applyinfoEntity.setSenduser(userEntity);
        }
        if(StringUtils.isNotBlank(sscd)) {
            TSysOrgEntity fleet = em.find(TSysOrgEntity.class, sscd);
            if(fleet == null) {
                throw new EntityNotFoundException("SSCD is not found!");
            }
            applyinfoEntity.setFleet(fleet);
        }
        if(bySync != null && bySync) {
            applyinfoEntity.setUpdateBySync(true);
        } else {
            applyinfoEntity.setUpdateBySync(false);
        }
        applyinfoEntity.setIssend(isSend);

        return applyinfoEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity retreatApply(String applyId, String applyNo, Boolean bySync, PrincipalExt principalExt) {
        if(applyId ==null && applyNo == null) {
            throw new InvalidParameterException("Approve info can not be null!");
        }
        TBusApplyinfoEntity applyinfoEntity;
        if(applyId != null) {
            applyinfoEntity = em.find(TBusApplyinfoEntity.class, applyId);
        } else {
            applyinfoEntity = getApplicationByNo(applyNo, principalExt);
        }
        if (applyinfoEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applyId);
        }
        if(bySync != null && bySync) {
            applyinfoEntity.setUpdateBySync(true);
        } else {
            applyinfoEntity.setUpdateBySync(false);
        }

        return updateApplicationStatus(applyinfoEntity.getUuid(), ApplyStatus.DISPATCH_REJECT.toStringValue(), bySync, principalExt);
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity updateApplication(String id, TBusApplyinfoEntity application, PrincipalExt principalExt) {
        TBusApplyinfoEntity applyinfoEntity = em.find(TBusApplyinfoEntity.class, id);
        if (applyinfoEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, id);
        }
        merge(application, applyinfoEntity);

        return applyinfoEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity updateApplication4PostId(String id, TBusApplyinfoEntity application) {
        TBusApplyinfoEntity applyinfoEntity = em.find(TBusApplyinfoEntity.class, id);
        if (applyinfoEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, id);
        }
        if(application.getPostId4Coordinator() != null) {
            applyinfoEntity.setPostId4Coordinator(application.getPostId4Coordinator());
        }
        if(application.getPostId4Dispatcher() != null) {
            applyinfoEntity.setPostId4Dispatcher(application.getPostId4Dispatcher());
        }
        if(application.getPostId4Manager() != null) {
            applyinfoEntity.setPostId4Manager(application.getPostId4Manager());
        }

        em.flush();
        return applyinfoEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusEvaluateinfoEntity evaluateApplication(String applyId, String applyNo, TBusEvaluateinfoEntity evaluateInfoEntity, PrincipalExt principalExt) {
        if(evaluateInfoEntity.getApplication() != null) {
            TBusApplyinfoEntity application;
            if(StringUtils.isNotBlank(applyId)) {
                application = em.find(TBusApplyinfoEntity.class, applyId);
            } else {
                application = getApplicationByNo(applyNo, principalExt);
            }
            if(application == null){
                throw logger.entityNotFound(TBusApplyinfoEntity.class, applyId == null ? applyNo : applyId);
            }
            evaluateInfoEntity.setApplication(application);
        }
        if(evaluateInfoEntity.getCar() != null) {
            TAzCarinfoEntity car = em.find(TAzCarinfoEntity.class, evaluateInfoEntity.getCar().getId());
            if(car == null){
                throw new EntityNotFoundException("Car is not exist!");
            }
            evaluateInfoEntity.setCar(car);
        }
        if(evaluateInfoEntity.getDriver() != null) {
            TRsDriverinfoEntity driver = em.find(TRsDriverinfoEntity.class, evaluateInfoEntity.getDriver().getUuid());
            if(driver == null){
                throw new EntityNotFoundException("Driver is not exist!");
            }
            evaluateInfoEntity.setDriver(driver);
        }
        if (evaluateInfoEntity.getSchedule() != null) {
            TBusScheduleRelaEntity schedule = em.find(TBusScheduleRelaEntity.class, evaluateInfoEntity.getSchedule().getUuid());
            if (schedule == null) {
                throw new EntityNotFoundException("Schedule is not exist!");
            }
            evaluateInfoEntity.setSchedule(schedule);
        }

        if(evaluateInfoEntity.getUpdateBySync() != null && evaluateInfoEntity.getUpdateBySync()) {
            evaluateInfoEntity.setUpdateBySync(true);
        } else {
            evaluateInfoEntity.setUpdateBySync(false);
        }
        evaluateInfoEntity.setEvaldate(DateTime.now());
        evaluateInfoEntity.setEvalstatus("1"); // 评价状态(0:未评价；1：已评价)
        TSysUserEntity userEntity;
        if(principalExt != null && principalExt.getUserIdOrNull() != null && evaluateInfoEntity.getUser() == null) {
            userEntity = em.find(TSysUserEntity.class, principalExt.getUserIdOrNull());
        } else {
            userEntity = em.find(TSysUserEntity.class, evaluateInfoEntity.getUser().getUserid());
        }
        evaluateInfoEntity.setUser(userEntity);
        if (StringUtils.isBlank(evaluateInfoEntity.getUuid())) {
            em.persist(evaluateInfoEntity);
        } else {
            evaluateInfoEntity = em.merge(evaluateInfoEntity);
        }

        return evaluateInfoEntity;
    }

    private void merge(TBusApplyinfoEntity application, TBusApplyinfoEntity applyinfoEntity) {
        DateTime now = DateTime.now();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");
        applyinfoEntity.setUpdatedate(now);
        if(StringUtils.isNotBlank(application.getApplyno())) {
            applyinfoEntity.setApplyno(application.getApplyno());
        } else {
            applyinfoEntity.setApplyno(formatter.print(now));
        }
        applyinfoEntity.setBegintime(application.getBegintime());
        applyinfoEntity.setEndtime(application.getEndtime());
        applyinfoEntity.setCargodes(application.getCargodes());
        applyinfoEntity.setStartpoint(application.getStartpoint());
        applyinfoEntity.setWays(application.getWays());
        applyinfoEntity.setReason(application.getReason());
        applyinfoEntity.setRemark(application.getRemark());
        applyinfoEntity.setIssend(StringUtils.isBlank(application.getIssend()) ? "0" : application.getIssend());
        if(ApplyStatus.APPLY_CANCEL.toStringValue().equals(application.getStatus())) {   // 取消申请
            applyinfoEntity.setCanceltype(application.getCanceltype());
            applyinfoEntity.setCancelseason(application.getCancelseason());
        }
        if(application.getUpdateBySync() != null && application.getUpdateBySync()) {
            applyinfoEntity.setUpdateBySync(true);
        } else {
            applyinfoEntity.setUpdateBySync(false);
        }

        TSysUserEntity coordinator = em.find(TSysUserEntity.class, application.getCoordinator().getUserid());
        if (coordinator == null) {
            throw logger.entityNotFound(TSysUserEntity.class, application.getCoordinator().getUserid());
        }
        applyinfoEntity.setCoordinator(coordinator);

        if (application.getPassenger() != null) {
            TBusMainUserInfoEntity passenger = em.find(TBusMainUserInfoEntity.class, application.getPassenger().getUuid());

            TSysOrgEntity department;
            if(passenger != null) {     // null =>> 在数据同步时 只有 passenger ID
                applyinfoEntity.setPassenger(passenger);
                if (coordinator.getDepartment().getOrgid().startsWith("001")) {
                    department = passenger.getSysOrg();
                } else {
                    department = coordinator.getDepartment();
                }
            } else {
                department = coordinator.getDepartment();
            }
            applyinfoEntity.setDepartment(department);
            applyinfoEntity.setPeoplenum(application.getPeoplenum());

            TSysOrgEntity fleet = findRightFleetOfCoordinator(coordinator.getUserid());
            applyinfoEntity.setFleet(fleet);
        }

        if(application.getSenduser() != null && application.getSenduser().getUserid() != null) {
            TSysUserEntity sender = em.find(TSysUserEntity.class, application.getSenduser().getUserid());
            if (sender == null) {
                throw logger.entityNotFound(TSysUserEntity.class, application.getSenduser().getUserid());
            }
            applyinfoEntity.setSenduser(sender);
        } else {
            List<TSysUserEntity> senderList = busBusinessRelationRepository.getDispatcherOfPassenger(applyinfoEntity.getPassenger().getUuid());
            if (!senderList.isEmpty()) {
                applyinfoEntity.setSenduser(senderList.get(0));
            }
        }
    }

    private TBusScheduleCarEntity checkCarOrScheduleCar(String applicationId, String carId, PrincipalExt principalExt) {
        TBusScheduleCarEntity scheduleCarEntity = em.find(TBusScheduleCarEntity.class, carId);
        if(scheduleCarEntity == null) {
            TAzCarinfoEntity carEntity = em.find(TAzCarinfoEntity.class, carId);
            if(carEntity == null) {
                throw logger.entityNotFound(TAzCarinfoEntity.class, carId);
            }
            scheduleCarEntity = scheduleRepository.getScheduleCarByApplicationIdAndCarId(applicationId, carId, principalExt);
        }
        return scheduleCarEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusEvaluateinfoEntity updateRateOfDriver(String applicationId, String carId, RateOfDriver rateOfDriver, PrincipalExt principalExt) {
        TBusScheduleCarEntity scheduleCarEntity = checkCarOrScheduleCar(applicationId, carId, principalExt);
        carId = scheduleCarEntity.getCar().getId();

        TBusApplyinfoEntity applicationEntity = getApplicationById(applicationId, principalExt);
        TBusEvaluateinfoEntity rateEntity = getRateByByApplicationIdAndCarId(applicationId, carId, principalExt);

        if (rateEntity != null && rateEntity.getDriverscore() != null && rateEntity.getDriverscore() > 0) {
            throw logger.invalidOperation("Rote Driver");
        }

        if (rateEntity == null) {
            rateEntity = prepareRateEntity(scheduleCarEntity);
            rateEntity.setApplication(applicationEntity);
        }
        if(principalExt.getUserIdOrNull() != null) {
            TSysUserEntity userEntity = em.find(TSysUserEntity.class, principalExt.getUserIdOrNull());
            rateEntity.setUser(userEntity);
        }
        rateEntity.setEvalstatus("1");
        rateEntity.setGdzt("0");        // 归档状态(0未归档，1已归档)
        rateEntity.setType("0");        // 类型(0为用车评价，1为车队评价)

        rateEntity.setCarscore(rateOfDriver.getCarRate());
        rateEntity.setDriverscore(rateOfDriver.getDriverRate());
        rateEntity.setTeamscore(rateOfDriver.getFleetRate());
        if(rateOfDriver.getCarRate() < 4) {
            rateEntity.setCarreason(rateOfDriver.getCarConcern());
        }
        if(rateOfDriver.getCarRate() < 4) {
            rateEntity.setDriverreason(rateOfDriver.getDriverConcern());
        }
        if(rateOfDriver.getCarRate() < 4) {
            rateEntity.setTeamreason(rateOfDriver.getFleetConcern());
        }
        rateEntity.setUpdateBySync(true);

        if (rateEntity.getUuid() == null) {
            em.persist(rateEntity);
        } else {
            rateEntity = em.merge(rateEntity);
        }

        syncDataStatusRepository.saveLocalSyncDataStatus(TSyncDataStatusEntity.SyncDataType.EVALUATE, SyncEntityConvert.fromEntity(rateEntity));
        return rateEntity;
    }


    public TBusEvaluateinfoEntity getRateByByApplicationIdAndCarId(String applicationId, String carId, PrincipalExt principalExt) {
        applySecurityFilter("applications", principalExt);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusEvaluateinfoEntity> cq = cb.createQuery(TBusEvaluateinfoEntity.class);
        Root<TBusEvaluateinfoEntity> rateRoot = cq.from(TBusEvaluateinfoEntity.class);
        cq.select(rateRoot);
        cq.where(
                cb.equal(rateRoot.get(TBusEvaluateinfoEntity_.application).get(TBusApplyinfoEntity_.uuid), applicationId),
                cb.equal(rateRoot.get(TBusEvaluateinfoEntity_.car).get(TAzCarinfoEntity_.id), carId)
        );
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);

    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusEvaluateinfoEntity updateRateOfPassenger(String applicationId, String carId, RateOfPassenger rateOfPassenger, PrincipalExt principalExt) {
        applySecurityFilter("applications", principalExt);

        TBusScheduleCarEntity scheduleCarEntity = checkCarOrScheduleCar(applicationId, carId, principalExt);
        carId = scheduleCarEntity.getCar().getId();

        TBusApplyinfoEntity applicationEntity = getApplicationById(applicationId, principalExt);
        TBusEvaluateinfoEntity rateEntity = getRateByByApplicationIdAndCarId(applicationId, carId, principalExt);

        // !(applyscore == 0 && personscre == 0) is rated
        if (rateEntity != null &&
                !((rateEntity.getApplyscore() == null || rateEntity.getApplyscore() == 0)
                        && (rateEntity.getPersonscore() == null || rateEntity.getPersonscore() == 0))) {
            throw logger.invalidOperation("Rote Passenger");
        }

        if (rateEntity == null) {
            rateEntity = prepareRateEntity(scheduleCarEntity);
            rateEntity.setApplication(applicationEntity);
        }
        if(principalExt.getUserIdOrNull() != null) {
            TSysUserEntity userEntity = em.find(TSysUserEntity.class, principalExt.getUserIdOrNull());
            rateEntity.setUser(userEntity);
        }

        rateEntity.setApplyscore(rateOfPassenger.getApplicationRate());
        rateEntity.setApplyreason(rateOfPassenger.getApplicationConcern());
        rateEntity.setPersonscore(rateOfPassenger.getPassengerRate());
        rateEntity.setPersonreason(rateOfPassenger.getPassengerConcern());

        if (rateEntity.getUuid() == null) {
            em.persist(rateEntity);
        }
        return rateEntity;
    }

    private TBusEvaluateinfoEntity prepareRateEntity(TBusScheduleCarEntity scheduleCarEntity) {
        TBusEvaluateinfoEntity rateEntity = new TBusEvaluateinfoEntity();
        rateEntity.setCar(scheduleCarEntity.getCar());
        rateEntity.setDriver(scheduleCarEntity.getDriver());
        rateEntity.setSchedule(scheduleCarEntity.getSchedule());
        rateEntity.setEvaldate(DateTime.now());
        rateEntity.setEvalstatus("1"); // 评价状态(0:未评价；1：已评价)
        return rateEntity;
    }
    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity updateRateOfApplication(String applicationId, RateOfApplication rateOfApplication, PrincipalExt userId) {

        applySecurityFilter("applications", userId);

        TBusApplyinfoEntity applicationEntity = this.getApplicationById(applicationId, userId);
        if (applicationEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applicationId);
        }
        if (applicationEntity.getBgfxx() != null) {
            throw logger.invalidOperation("Rote Application");
        }

        String irregularReson = "";
        if (!rateOfApplication.getRegularApplication()) {
            irregularReson += "申请单填写不规范,";
        }
        if (!rateOfApplication.getRegularBookingTime()) {
            irregularReson += "用车人时间不规范,";
        }
        if (!rateOfApplication.getRegularRoute()) {
            irregularReson += "用车人路线不规范,";
        }

        if (StringUtils.isNotBlank(irregularReson)) {
            applicationEntity.setSfgf("0");
            applicationEntity.setBgfxx(irregularReson.substring(0, irregularReson.length() - 1));
        } else {
            applicationEntity.setSfgf("1");
            applicationEntity.setBgfxx("非常规范");
        }
        applicationEntity.setUpdatedate(DateTime.now());
        applicationEntity.setUpdateuser(userId.getUserIdOrNull());
        applicationEntity.setUpdateBySync(true);

        // [EVALUATE_APPLY#applyId#sfgf#bgfxx#userId]
        syncDataStatusRepository.saveLocalSyncDataStatus(TSyncDataStatusEntity.SyncDataType.EVALUATE_APPLY,
                "EVALUATE_APPLY#" + applicationEntity.getUuid() + "#" + applicationEntity.getSfgf() + "#" + applicationEntity.getBgfxx() + "#" + applicationEntity.getUpdateuser());
        return applicationEntity;
    }

    public List<TAzCarinfoEntity> listAllCandidateCars(String[] applicationIds, String keyword, Integer offset, Integer limit, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> carRoot = cq.from(TAzCarinfoEntity.class);
        carRoot.fetch(TAzCarinfoEntity_.driver, JoinType.LEFT);

        cq.select(carRoot);
        Subquery<TBusScheduleCarEntity> overlapScheduleCarSubQuery = cq.subquery(TBusScheduleCarEntity.class);

        applyOverlapScheduleCarSubquery(overlapScheduleCarSubQuery, applicationIds, carRoot, null, cb, principalExt);
        Predicate predicate = cb.and(
                cb.equal(
                        carRoot.get(TAzCarinfoEntity_.clzt), "02"
                ),
                cb.or(
                        cb.equal(carRoot.get(TAzCarinfoEntity_.repairingState), RepairingState.NONE.id()),
                        cb.isNull(carRoot.get(TAzCarinfoEntity_.repairingState))

                ),
                cb.not(
                        cb.exists(overlapScheduleCarSubQuery)
                )

        );

        if (keyword != null) {
            predicate = cb.and(
                    predicate,
                    cb.or(
                            cb.like(carRoot.get(TAzCarinfoEntity_.sysOrg).get(TSysOrgEntity_.orgname), "%" + keyword + "%"),
                            cb.like(carRoot.get(TAzCarinfoEntity_.cphm), "%" + keyword + "%")
                    )
            );
        }

        cq.where(predicate);
        cq.orderBy(
                cb.asc(carRoot.get(TAzCarinfoEntity_.cphm))
        );
        TypedQuery<TAzCarinfoEntity> query = em.createQuery(cq);
        if (offset != null) {
            query.setFirstResult(offset);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }


        applySecurityFilter("cars", principalExt);


        return query.getResultList();
    }

    private TSysOrgEntity findRightFleetOfCoordinator(String coordinatorId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysOrgEntity> cq = cb.createQuery(TSysOrgEntity.class);
        Root<TBusBusinessRelaEntity> mapRoot = cq.from(TBusBusinessRelaEntity.class);

        Root<TSysUserEntity> userRoot = cq.from(TSysUserEntity.class);


        cq.select(mapRoot.get(TBusBusinessRelaEntity_.fleet));

        Path<TSysOrgEntity> coordinatorOrg = userRoot.get(TSysUserEntity_.department);
        cq.where(
                cb.or(
                        cb.and(
                                cb.like(
                                        coordinatorOrg.get(TSysOrgEntity_.orgid),
                                        "001%"

                                ),
                                cb.equal(
                                        mapRoot.get(TBusBusinessRelaEntity_.fleet),
                                        coordinatorOrg
                                )
                        ),
                        cb.and(
                                cb.notLike(
                                        coordinatorOrg.get(TSysOrgEntity_.orgid),
                                        "001%"
                                ),
                                cb.equal(
                                        mapRoot.get(TBusBusinessRelaEntity_.busOrg),
                                        coordinatorOrg
                                )
                        )

                ),

                cb.equal(userRoot.get(TSysUserEntity_.userid), coordinatorId)


        );

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    private Subquery<TBusScheduleCarEntity> applyOverlapScheduleCarSubquery(Subquery<TBusScheduleCarEntity> overlapScheduleCarSubQuery, String[] applicationIds,
                                                                            Root<TAzCarinfoEntity> carRoot, Root<TRsDriverinfoEntity> driverRoot, CriteriaBuilder cb, PrincipalExt principalExt) {
        Root<TBusScheduleCarEntity> subScheduleCarRoot = overlapScheduleCarSubQuery.from(TBusScheduleCarEntity.class);
        overlapScheduleCarSubQuery.select(subScheduleCarRoot);

        Path<DateTime> scheduleStartTime = subScheduleCarRoot.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.starttime);
        Path<DateTime> scheduleEndTime = subScheduleCarRoot.get(TBusScheduleCarEntity_.schedule).get(TBusScheduleRelaEntity_.endtime);

        DateTime applicationStartTime = null;
        DateTime applicationEndTime = null;
        for (String applicationId : applicationIds) {
            TBusApplyinfoEntity applicationEntity = getApplicationById(applicationId, principalExt);
            if (applicationEntity == null) {
                throw logger.entityNotFound(TBusApplyinfoEntity.class, applicationId);
            }
            if (applicationStartTime == null || applicationEndTime.isAfter(applicationEntity.getBegintime())) {
                applicationStartTime = applicationEntity.getBegintime();
            }
            if (applicationEndTime == null || applicationEndTime.isBefore(applicationEntity.getEndtime())) {
                applicationEndTime = applicationEntity.getEndtime();
            }

        }


        if (applicationStartTime == null || applicationEndTime == null) {
            throw logger.invalidApplication(Arrays.deepToString(applicationIds));
        }

        Predicate predicate = cb.and(
                cb.or(
                        cb.and(cb.between(scheduleStartTime, applicationStartTime, applicationEndTime)),
                        cb.and(cb.between(scheduleEndTime, applicationStartTime, applicationEndTime)),
                        cb.and(
                                cb.lessThan(scheduleStartTime, applicationStartTime),
                                cb.greaterThan(scheduleEndTime, applicationEndTime)
                        )
                ),
                subScheduleCarRoot.get(TBusScheduleCarEntity_.status).in(ScheduleStatus.AWAITING.id())
        );
        if (driverRoot != null) {
            predicate = cb.and(
                    predicate,
                    cb.and(
                            cb.equal(subScheduleCarRoot.get(TBusScheduleCarEntity_.car), carRoot),
                            cb.equal(subScheduleCarRoot.get(TBusScheduleCarEntity_.driver), driverRoot)
                    )
            );
        } else {
            predicate = cb.and(
                    predicate,
                    cb.equal(
                            subScheduleCarRoot.get(TBusScheduleCarEntity_.car), carRoot
                    )
            );
        }

        overlapScheduleCarSubQuery.where(predicate);
        return overlapScheduleCarSubQuery;
    }

    public List<TRsDriverinfoEntity> listAllCandidateDrivers(String[] applicationIds, String carId, String keyword, Integer offset, Integer limit, PrincipalExt principalExt) {


        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TRsDriverinfoEntity> cq = cb.createQuery(TRsDriverinfoEntity.class);
        Root<TRsDriverinfoEntity> driverRoot = cq.from(TRsDriverinfoEntity.class);
        Root<TAzCarinfoEntity> carRoot = cq.from(TAzCarinfoEntity.class);
        cq.select(driverRoot);


        Subquery<TBusScheduleCarEntity> overlapScheduleCarSubQuery = cq.subquery(TBusScheduleCarEntity.class);

        applyOverlapScheduleCarSubquery(overlapScheduleCarSubQuery, applicationIds, carRoot, driverRoot, cb, principalExt);


        Subquery<TRsDriverinfoEntity> subqueryLicense = cq.subquery(TRsDriverinfoEntity.class);
        Root<TAzJzRelaEntity> licenseMapping = subqueryLicense.from(TAzJzRelaEntity.class);
        subqueryLicense.select(driverRoot);
        subqueryLicense.where(

                cb.equal(
                        carRoot.get(TAzCarinfoEntity_.xszcx),
                        licenseMapping.get(TAzJzRelaEntity_.xszcx)
                ),
                cb.equal(
                        licenseMapping.get(TAzJzRelaEntity_.jzyq),
                        driverRoot.get(TRsDriverinfoEntity_.drivecartype)
                )
        );


        Predicate predicate = cb.and(
                cb.equal(
                        carRoot.get(TAzCarinfoEntity_.id), carId
                ),
                cb.equal(
                        driverRoot.get(TRsDriverinfoEntity_.department),
                        carRoot.get(TAzCarinfoEntity_.sysOrg)
                ),
                cb.equal(driverRoot.get(TRsDriverinfoEntity_.enabled), "1"),
                cb.or(
                        cb.exists(subqueryLicense),
                        cb.isNull(driverRoot.get(TRsDriverinfoEntity_.drivecartype))

                ),

                cb.not(
                        cb.exists(overlapScheduleCarSubQuery)
                )

        );

        if (keyword != null) {
            predicate = cb.and(
                    predicate,
                    cb.or(
                            cb.like(driverRoot.get(TRsDriverinfoEntity_.drivername), "%" + keyword + "%")
                    )
            );
        }

        cq.where(predicate);
        cq.orderBy(cb.asc(cb.function("casttogbk", String.class,
                cb.trim(driverRoot.get(TRsDriverinfoEntity_.drivername))

        )));
        TypedQuery<TRsDriverinfoEntity> query = em.createQuery(cq);
        if (offset != null) {
            query.setFirstResult(offset);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }

        return query.getResultList();

    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusApplyinfoEntity updateApplicationStatus(String applicationId, String status, Boolean bySync, PrincipalExt principalExt) {

        TBusApplyinfoEntity applicationEntity = getApplicationById(applicationId, principalExt);
        if (applicationEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applicationId);
        }
        // 调度成功 之后的不能修改状态  除非 【调度作废，系统作废】
        if (ApplyStatus.DISPATCH_SUCCESS.toStringValue().equals(applicationEntity.getStatus())
                && (!ApplyStatus.DISPATCH_REJECT.toStringValue().equals(status) || !ApplyStatus.SYSTEM_CANCEL.toStringValue().equals(status))) {
            throw logger.invalidOperation("Update application status");
        }

        if(applicationEntity.getUpdateBySync() != null && applicationEntity.getUpdateBySync()) {
            applicationEntity.setUpdateBySync(true);
        } else {
            applicationEntity.setUpdateBySync(false);
        }
        applicationEntity.setUpdatedate(new DateTime());
        applicationEntity.setStatus(ApplyStatus.valueOf(Integer.valueOf(status)).toStringValue());

        if(bySync != null && bySync) {
            applicationEntity.setUpdateBySync(true);
        } else {
            applicationEntity.setUpdateBySync(false);
        }
        if(principalExt != null) {      // TODO sync is not ready principalExt is null
            applicationEntity.setUpdateuser(principalExt.getUserIdOrNull());
        }

        return em.find(TBusApplyinfoEntity.class, applicationId);
    }

    // 作废 调度单 信息 调度作废
    @TransactionAttribute(REQUIRES_NEW)
    public void deleteApply(String applyId, PrincipalExt principalExt) {
        TBusApplyinfoEntity applyinfoEntity = getApplicationById(applyId, principalExt);
        if (applyinfoEntity == null) {
            throw new EntityNotFoundException("Apply is not exist!");
        }
        em.remove(applyinfoEntity);
    }
    // 作废 调度单 信息 调度作废
    @TransactionAttribute(REQUIRES_NEW)
    public void deleteSchedule(String scheduleId, PrincipalExt principalExt) {
        applySecurityFilter("applications", principalExt);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusScheduleRelaEntity> cq = cb.createQuery(TBusScheduleRelaEntity.class);
        Root<TBusScheduleRelaEntity> scheduleRoot = cq.from(TBusScheduleRelaEntity.class);
        cq.where(
                cb.equal(scheduleRoot.get(TBusScheduleRelaEntity_.uuid), scheduleId)
        );
        TBusScheduleRelaEntity relaEntity = Iterables.getFirst(em.createQuery(cq).getResultList(), null);

        for (TBusApplyinfoEntity applyinfoEntity : relaEntity.getApplications()) {
            TBusApplyinfoEntity applicationEntity = getApplicationById(applyinfoEntity.getUuid(), principalExt);
            applicationEntity.setSchedule(null);
        }
        relaEntity.setApplications(null);
        for (TBusScheduleCarEntity carEntity : relaEntity.getScheduleCars()) {
            carEntity = em.find(TBusScheduleCarEntity.class, carEntity.getUuid());
            em.remove(carEntity);
        }
        relaEntity.setScheduleCars(null);
        em.remove(relaEntity);
    }

    public TBusApplyinfoEntity getApplicationById(String id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusApplyinfoEntity> cq = cb.createQuery(TBusApplyinfoEntity.class);
        Root<TBusApplyinfoEntity> applyRoot = cq.from(TBusApplyinfoEntity.class);
        cq.where(
                cb.equal(applyRoot.get(TBusApplyinfoEntity_.uuid), id)
        );
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }
    // 车辆调度-获得车辆调度用车审核信息
    public TBusApproveSugEntity getApplyApproveInfoByApplyNo(String applyNo) {
        if(applyNo == null) {
            throw new InvalidParameterException("Apply No can not be null!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusApproveSugEntity> cq = cb.createQuery(TBusApproveSugEntity.class);
        Root<TBusApproveSugEntity> approveRoot = cq.from(TBusApproveSugEntity.class);
        approveRoot.fetch(TBusApproveSugEntity_.application);
        cq.where(
                cb.equal(approveRoot.get(TBusApproveSugEntity_.application).get(TBusApplyinfoEntity_.applyno), applyNo)
        );
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }
    // 车辆调度-获得车辆调度用车审核信息
    public TBusApproveSugEntity getApplyApproveInfo(String uuid) {
        if(uuid == null) {
            throw new InvalidParameterException("ID can not be null!");
        }
        return em.find(TBusApproveSugEntity.class, uuid);
    }
    // 车辆调度-获得车辆调度用车审核信息
    public List<TBusApproveSugEntity> listApplyApproveInfo(DateTime startTime, DateTime endTime, PrincipalExt principalExtOrNull) {
        if (startTime == null) {
            startTime = DateTimeUtil.appStartTime;
        }
        if (endTime == null) {
            endTime = DateTimeUtil.appEndTime;
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusApproveSugEntity> cq = cb.createQuery(TBusApproveSugEntity.class);
        Root<TBusApproveSugEntity> approveRoot = cq.from(TBusApproveSugEntity.class);
        approveRoot.fetch(TBusApproveSugEntity_.application);
        approveRoot.fetch(TBusApproveSugEntity_.user, JoinType.LEFT);
        cq.select(approveRoot);

        Predicate predicate = cb.and(
                cb.between(approveRoot.get(TBusApproveSugEntity_.operatedate), new Timestamp(startTime.getMillis()), new Timestamp(endTime.getMillis())),
                cb.or(
                        cb.isNull(approveRoot.get(TBusApproveSugEntity_.updateBySync)),
                        cb.isFalse(approveRoot.get(TBusApproveSugEntity_.updateBySync))
                )
        );

        cq.where(predicate);

        TypedQuery<TBusApproveSugEntity> query = em.createQuery(cq);

        applySecurityFilter("applications", principalExtOrNull);
        return query.getResultList();
    }
    // 车辆调度-车辆调度用车审核
    @TransactionAttribute(REQUIRES_NEW)
    public TBusApproveSugEntity approveApplication(String applyId, String applyNo, TBusApproveSugEntity approveSugInfo, PrincipalExt principalExtOrNull) {
        if(StringUtils.isBlank(applyId) && (StringUtils.isBlank(applyNo) &&
                (approveSugInfo == null || approveSugInfo.getApplication() == null || StringUtils.isBlank(approveSugInfo.getApplication().getApplyno())))) {
            throw new InvalidParameterException("Approve info can not be null!");
        }

        TSysUserEntity userEntity;
        if(approveSugInfo.getUser() != null) {
            userEntity = em.find(TSysUserEntity.class, approveSugInfo.getUser().getUserid());
            if (userEntity == null) {
                throw new EntityNotFoundException("User is not found!");
            }
        } else {
            userEntity = em.find(TSysUserEntity.class, principalExtOrNull.getUserIdOrNull());
        }
        approveSugInfo.setUser(userEntity);
        approveSugInfo.setDepartment(userEntity.getDepartment());
        approveSugInfo.setOperatedate(new Timestamp(DateTime.now().getMillis()));

        if(StringUtils.isNotBlank(approveSugInfo.getUuid())) {
            TBusApproveSugEntity approveSugEntity = em.find(TBusApproveSugEntity.class, approveSugInfo.getUuid());
            if(approveSugEntity != null) {
                TBusApplyinfoEntity applyinfoEntity = getApplicationByNo(applyNo, principalExtOrNull);
                if(applyinfoEntity == null) {
                    throw new EntityNotFoundException("Apply is not found!");
                }
                approveSugInfo.setApplication(applyinfoEntity);
                return updateApproveInfo(approveSugEntity.getUuid(), approveSugInfo, principalExtOrNull);
            }
        }

        TBusApplyinfoEntity applyinfoEntity;
        if(StringUtils.isNotBlank(applyId)) {
            applyinfoEntity = em.find(TBusApplyinfoEntity.class, applyId);
            if(applyinfoEntity == null && StringUtils.isBlank(applyNo)) {
                throw new EntityNotFoundException("Apply info is not found!");
            }
        } else {
            applyinfoEntity = getApplicationByNo(applyNo, principalExtOrNull);
        }
        if(applyinfoEntity == null) {
            throw new EntityNotFoundException("Apply is not found!");
        }
        String applyNewStatus = ApplyStatus.APPLY.toStringValue(); // 审批通过
        if(!"01".equals(approveSugInfo.getSuggest())){
            applyNewStatus = ApplyStatus.APPLY_REJECT.toStringValue(); // 审批退回
        }
        if(approveSugInfo.getUpdateBySync() != null && approveSugInfo.getUpdateBySync()) {
            approveSugInfo.setUpdateBySync(true);
        } else {
            approveSugInfo.setUpdateBySync(false);
        }
        updateApplicationStatus(applyinfoEntity.getUuid(), applyNewStatus, approveSugInfo.getUpdateBySync(), principalExtOrNull);

        approveSugInfo.setApplication(applyinfoEntity);
        if(StringUtils.isNotBlank(approveSugInfo.getUuid())) {
            approveSugInfo = em.merge(approveSugInfo);
        } else {
            em.persist(approveSugInfo);
        }

        return approveSugInfo;
    }
    public TBusApplyinfoEntity getApplicationByNo(String applyNo, PrincipalExt principalExtOrNull) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusApplyinfoEntity> cq = cb.createQuery(TBusApplyinfoEntity.class);
        Root<TBusApplyinfoEntity> scheduleRoot = cq.from(TBusApplyinfoEntity.class);
        cq.where(cb.equal(scheduleRoot.get(TBusApplyinfoEntity_.applyno), applyNo));
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }
    // 车辆调度-车辆调度用车审核信息更新
    @TransactionAttribute(REQUIRES_NEW)
    public TBusApproveSugEntity updateApproveInfo(String uuid, TBusApproveSugEntity approveSugInfo, PrincipalExt principalExtOrNull) {
        TBusApproveSugEntity approveSugEntity = em.find(TBusApproveSugEntity.class, uuid);
        if(approveSugEntity == null) {
            throw new EntityNotFoundException("Approve info is not found!");
        }
        approveSugEntity.setSuggest(approveSugInfo.getSuggest());
        approveSugEntity.setRemark(approveSugInfo.getRemark());
        approveSugEntity.setOperatedate(approveSugInfo.getOperatedate());

        if(approveSugInfo.getApplication() != null) {
            TBusApplyinfoEntity applyEntity = getApplicationByNo(approveSugInfo.getApplication().getApplyno(), principalExtOrNull);
            if(applyEntity == null) {
                throw new EntityNotFoundException("Apply is not found!");
            }
            approveSugEntity.setApplication(applyEntity);
            String applyNewStatus = ApplyStatus.DEP_APPROVE.toStringValue();
            if("01".equals(approveSugInfo.getSuggest())){
                applyNewStatus = ApplyStatus.APPLY.toStringValue();
            }
            applyEntity.setUpdateBySync(approveSugInfo.getUpdateBySync());

            updateApplicationStatus(applyEntity.getUuid(), applyNewStatus, approveSugInfo.getUpdateBySync(), principalExtOrNull);
        }
        if(approveSugInfo.getUser() != null) {
            TSysUserEntity userEntity = em.find(TSysUserEntity.class, approveSugInfo.getUser().getUserid());
            if (userEntity == null) {
                throw new EntityNotFoundException("User is not found!");
            }
            approveSugEntity.setUser(userEntity);
        }

        return approveSugEntity;
    }


}
