package com.yunguchang.data;

import com.google.common.collect.Iterables;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.persistence.*;
import com.yunguchang.sam.PrincipalExt;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by WHB on 2015-09-21.
 */
@Stateless
public class DepotRepository extends GenericRepository {


    private static final int pageSize = 20;

    private Logger logger;
    /**
     * 获得驻车点信息
     *
     * @return
     */
//    public List<TDepotEntity> listAllDepot() {
//        List<TDepotEntity> parkList = new ArrayList<>();
//        // Mock Data start
//        for (int i = 0; i < 10; i++) {
//            TDepotEntity entity = new TDepotEntity();
//            entity.setId("1111111111" + i);
//            entity.setName("驻车点" + i);
//            entity.setLatitude(120.9982 + i * 0.005);
//            entity.setLongitude(120.9982 + i * 0.01);
//            parkList.add(entity);
//        }
//        // Mock Data end
//        return parkList;
//    }
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(TBusReturningDepotEntity entity) {
        em.persist(entity);
    }

    public TBusReturningDepotEntity getReturnEventBySample(TBusReturningDepotEntity returnEvent){
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TBusReturningDepotEntity> cq = cb.createQuery(TBusReturningDepotEntity.class);
            Root<TBusReturningDepotEntity> returnDepot = cq.from(TBusReturningDepotEntity.class);
            cq.select(returnDepot);
            cq.where(
                    cb.equal(returnDepot.get(TBusReturningDepotEntity_.returnTime), returnEvent.getReturnTime()),
                    cb.equal(returnDepot.get(TBusReturningDepotEntity_.car).get(TAzCarinfoEntity_.id), returnEvent.getCar().getId())
            );
            return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
        }catch (Exception e){
            logger.queryReturnEventByExampleError(returnEvent.toString());
            return null;
        }
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(TBusNoReturningDepotEntity entity) {
        em.persist(entity);
    }


    public List<TAzCarinfoEntity> listNoReturnByDate(DateTime date, PrincipalExt principal) {
        if (date == null) {
            return Collections.EMPTY_LIST;
        }


        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> criteriaQuery = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> carRoot = criteriaQuery.from(TAzCarinfoEntity.class);
        criteriaQuery.distinct(true);
        Fetch<TAzCarinfoEntity, TSysOrgEntity> fetchOrg = carRoot.fetch(TAzCarinfoEntity_.sysOrg);
        //fetchOrg.fetch(TSysOrgEntity_.depot, JoinType.LEFT);
        carRoot.fetch(TAzCarinfoEntity_.driver);

        Join<TAzCarinfoEntity, TSysOrgEntity> joinOrg = (Join<TAzCarinfoEntity, TSysOrgEntity>) fetchOrg;

        Subquery<String> carIdQuery = criteriaQuery.subquery(String.class);
        Root<TBusNoReturningDepotEntity> busAlarm = carIdQuery.from(TBusNoReturningDepotEntity.class);

        carIdQuery.select(busAlarm.join(TBusNoReturningDepotEntity_.car).get(TAzCarinfoEntity_.id).as(String.class));

        carIdQuery.where(
                cb.and(
                        cb.greaterThanOrEqualTo(busAlarm.get(TBusNoReturningDepotEntity_.dateTime), date.withTimeAtStartOfDay()),
                        cb.lessThan(busAlarm.get(TBusNoReturningDepotEntity_.dateTime), date.withTimeAtStartOfDay().plusDays(1))
                )
        );

        criteriaQuery.where(
                carRoot.get(TAzCarinfoEntity_.id).in(carIdQuery)
        );

        criteriaQuery.orderBy(
                cb.asc(joinOrg.get(TSysOrgEntity_.orgname)),
                cb.asc(carRoot.get(TAzCarinfoEntity_.cphm)));


        applySecurityFilter("bus_no_return",principal);
        return em.createQuery(criteriaQuery).getResultList();
    }

    public List<TBusReturningDepotEntity> listReturningDepotByStartAndEnd(DateTime start, DateTime end, PrincipalExt principal) {
        if (start == null) {
            return Collections.EMPTY_LIST;
        }
        if (end == null) {
            return Collections.EMPTY_LIST;
        }


        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusReturningDepotEntity> criteriaQuery = cb.createQuery(TBusReturningDepotEntity.class);
        Root<TBusReturningDepotEntity> returningDepot = criteriaQuery.from(TBusReturningDepotEntity.class);
        Fetch<TBusReturningDepotEntity, TAzCarinfoEntity> fetchCar = returningDepot.fetch(TBusReturningDepotEntity_.car);
        Fetch<TAzCarinfoEntity, TSysOrgEntity> fetchOrg = fetchCar.fetch(TAzCarinfoEntity_.sysOrg);
        fetchCar.fetch(TAzCarinfoEntity_.driver);

        Join<TBusReturningDepotEntity, TAzCarinfoEntity> joinCar = (Join<TBusReturningDepotEntity, TAzCarinfoEntity>) fetchCar;
        Join<TAzCarinfoEntity, TSysOrgEntity> joinOrg = (Join<TAzCarinfoEntity, TSysOrgEntity>) fetchOrg;


        criteriaQuery.select(returningDepot);
        criteriaQuery.where(
                cb.greaterThanOrEqualTo(returningDepot.get(TBusReturningDepotEntity_.returnTime), start),
                cb.lessThanOrEqualTo(returningDepot.get(TBusReturningDepotEntity_.returnTime), end)

        );

        criteriaQuery.orderBy(
                cb.asc(joinOrg.get(TSysOrgEntity_.orgname)),
                cb.desc(returningDepot.get(TBusReturningDepotEntity_.returnTime)),
                cb.asc(joinCar.get(TAzCarinfoEntity_.cphm)));

        applySecurityFilter("bus_return",principal);

        return em.createQuery(criteriaQuery).getResultList();
    }


    public List<TEmbeddedDepot> listDepots(PrincipalExt principal) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TEmbeddedDepot> cq = cb.createQuery(TEmbeddedDepot.class);
        Root<TAzCarinfoEntity> carRoot = cq.from(TAzCarinfoEntity.class);
        cq.select(carRoot.get(TAzCarinfoEntity_.depot));
        cq.distinct(true);
        cq.orderBy(cb.desc(carRoot.get(TAzCarinfoEntity_.depot).get("name")));
        applySecurityFilter("cars",principal);
        return em.createQuery(cq).getResultList();


    }

    public int countCarsNotInDepot(DateTime startTime, DateTime endTime, PrincipalExt principal) {
        if (startTime == null) {
            startTime = DateTime.now().minusDays(1);
        }
        startTime = startTime.withTimeAtStartOfDay();
        if(endTime == null) {
            endTime = startTime.plusDays(1);
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<TAzCarinfoEntity> carRoot = criteriaQuery.from(TAzCarinfoEntity.class);

        criteriaQuery.select(cb.countDistinct(carRoot)).distinct(true);

        Subquery<String> carIdQuery = criteriaQuery.subquery(String.class);
        Root<TBusNoReturningDepotEntity> busAlarm = carIdQuery.from(TBusNoReturningDepotEntity.class);

        carIdQuery.select(busAlarm.join(TBusNoReturningDepotEntity_.car).get(TAzCarinfoEntity_.id).as(String.class));

        carIdQuery.where(
                cb.and(
                        cb.between(busAlarm.get(TBusNoReturningDepotEntity_.dateTime), startTime, endTime)
                )
        );

        criteriaQuery.where(carRoot.get(TAzCarinfoEntity_.id).in(carIdQuery));

        applySecurityFilter("bus_no_return",principal);
        return em.createQuery(criteriaQuery).getSingleResult().intValue();
    }
}
