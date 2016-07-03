package com.yunguchang.data;

import com.google.common.collect.Iterables;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import com.yunguchang.model.persistence.TAzCarinfoEntity_;
import com.yunguchang.model.persistence.TBusAlarmEntity;
import com.yunguchang.model.persistence.TBusAlarmEntity_;
import com.yunguchang.sam.PrincipalExt;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

/**
 * Created by gongy on 9/27/2015.
 */

@Stateless
public class AlarmRepository extends GenericRepository {

    @Inject
    private Logger logger;

    public List<TBusAlarmEntity> getAlarmByStartEndType(DateTime start, DateTime end, Alarm alarm, Integer limit, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusAlarmEntity> criteriaQuery = cb.createQuery(TBusAlarmEntity.class);
        Root<TBusAlarmEntity> busAlarm = criteriaQuery.from(TBusAlarmEntity.class);
        Fetch<TBusAlarmEntity, TAzCarinfoEntity> fetchCar = busAlarm.fetch(TBusAlarmEntity_.car);

        //fetchCar.fetch(TAzCarinfoEntity_.sysOrg).fetch(TSysOrgEntity_.depot);
        Join<TBusAlarmEntity, TAzCarinfoEntity> joinCar = (Join<TBusAlarmEntity, TAzCarinfoEntity>) fetchCar;

        criteriaQuery.select(busAlarm);

        Predicate predicate = cb.conjunction();

        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(busAlarm.get(TBusAlarmEntity_.start), start));
        }

        if (end != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(busAlarm.get(TBusAlarmEntity_.start), end));
        }

        if (alarm != null) {
            predicate = cb.and(predicate, cb.equal(busAlarm.get(TBusAlarmEntity_.alarm), alarm.getId()));

        }


        criteriaQuery.where(predicate);

        criteriaQuery.orderBy(
                cb.desc(busAlarm.get(TBusAlarmEntity_.start)),
                cb.asc(joinCar.get(TAzCarinfoEntity_.cphm))


        );

        applySecurityFilter("alarms", principalExt);

        TypedQuery<TBusAlarmEntity> query = em.createQuery(criteriaQuery);
        if (limit != null) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    public List<TBusAlarmEntity> getCarAlarmByStartEndType(String carId, DateTime start, DateTime end, Alarm alarm, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusAlarmEntity> criteriaQuery = cb.createQuery(TBusAlarmEntity.class);
        Root<TBusAlarmEntity> busAlarm = criteriaQuery.from(TBusAlarmEntity.class);
        Fetch<TBusAlarmEntity, TAzCarinfoEntity> fetchCar = busAlarm.fetch(TBusAlarmEntity_.car);
        Join<TBusAlarmEntity, TAzCarinfoEntity> joinCar = (Join<TBusAlarmEntity, TAzCarinfoEntity>) fetchCar;
        criteriaQuery.select(busAlarm);
        Predicate predicate = cb.conjunction();
        if (carId != null) {
            predicate = cb.and(predicate, cb.equal(joinCar.get(TAzCarinfoEntity_.id), carId));
        }
        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(busAlarm.get(TBusAlarmEntity_.start), start));
        }
        if (end != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(busAlarm.get(TBusAlarmEntity_.start), end));
        }
        if (alarm != null) {
            predicate = cb.and(predicate, cb.equal(busAlarm.get(TBusAlarmEntity_.alarm), alarm.getId()));
        }
        criteriaQuery.where(predicate);

        criteriaQuery.orderBy(
                cb.desc(busAlarm.get(TBusAlarmEntity_.start)),
                cb.asc(joinCar.get(TAzCarinfoEntity_.cphm))
        );

        applySecurityFilter("alarms", principalExt);

        TypedQuery<TBusAlarmEntity> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }


    public List<TBusAlarmEntity> getNoClearedAlarmByStartEnd(DateTime start, DateTime end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusAlarmEntity> criteriaQuery = cb.createQuery(TBusAlarmEntity.class);
        Root<TBusAlarmEntity> busAlarm = criteriaQuery.from(TBusAlarmEntity.class);
        Fetch<TBusAlarmEntity, TAzCarinfoEntity> fetchCar = busAlarm.fetch(TBusAlarmEntity_.car);

        //fetchCar.fetch(TAzCarinfoEntity_.sysOrg).fetch(TSysOrgEntity_.depot);
        Join<TBusAlarmEntity, TAzCarinfoEntity> joinCar = (Join<TBusAlarmEntity, TAzCarinfoEntity>) fetchCar;

        criteriaQuery.select(busAlarm);

        Predicate predicate = cb.conjunction();

        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(busAlarm.get(TBusAlarmEntity_.start), start));
        }

        if (end != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(busAlarm.get(TBusAlarmEntity_.start), end));
        }


        criteriaQuery.where(
                predicate,
                cb.isNull(busAlarm.get(TBusAlarmEntity_.end))
        );

        criteriaQuery.orderBy(
                cb.desc(busAlarm.get(TBusAlarmEntity_.start)),
                cb.asc(joinCar.get(TAzCarinfoEntity_.cphm))


        );
        return em.createQuery(criteriaQuery).getResultList();
    }


    public TBusAlarmEntity getAlarmByExample(TBusAlarmEntity busAlarmEntity) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TBusAlarmEntity> cq = cb.createQuery(TBusAlarmEntity.class);
            Root<TBusAlarmEntity> alarm = cq.from(TBusAlarmEntity.class);
            cq.select(alarm);
            cq.where(
                    cb.equal(alarm.get(TBusAlarmEntity_.start), busAlarmEntity.getStart()),
                    cb.equal(alarm.get(TBusAlarmEntity_.car).get(TAzCarinfoEntity_.id), busAlarmEntity.getCar().getId()),
                    cb.equal(alarm.get(TBusAlarmEntity_.alarm), busAlarmEntity.getAlarm())
            );
            return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
        }catch (Exception e){
            logger.queryAlarmByExampleError(busAlarmEntity.toString());
            return null;
        }
    }

    @TransactionAttribute(REQUIRES_NEW)
    public void saveAlarm(TBusAlarmEntity busAlarmEntity) {
        busAlarmEntity.setPersistTime(DateTime.now());
        em.persist(busAlarmEntity);
    }

    @TransactionAttribute(REQUIRES_NEW)
    public void cleanAlarm(Integer alarmId) {
        TBusAlarmEntity event = em.find(TBusAlarmEntity.class, alarmId);
        if(event == null) {
            return;
        }
        em.remove(event);
    }

    @TransactionAttribute(REQUIRES_NEW)
    public void updateAlarmEndTime(int id, DateTime end) {
        TBusAlarmEntity event = em.find(TBusAlarmEntity.class, id);
        if (event != null) {
            event.setEnd(end);
        }
    }

    public int countAllAlarmsByType(DateTime start, DateTime end, Alarm alarm, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TBusAlarmEntity> busAlarmRoot = cq.from(TBusAlarmEntity.class);

        cq.select(cb.countDistinct(busAlarmRoot));

        Predicate predicate = cb.conjunction();

        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(busAlarmRoot.get(TBusAlarmEntity_.start), start));
        }
        if (end != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(busAlarmRoot.get(TBusAlarmEntity_.start), end));
        }
        if (alarm != null) {
            predicate = cb.and(predicate, cb.equal(busAlarmRoot.get(TBusAlarmEntity_.alarm), alarm.getId()));
        }

        cq.where(predicate);

        applySecurityFilter("alarms", principalExt);
        return em.createQuery(cq).getSingleResult().intValue();
    }

}
