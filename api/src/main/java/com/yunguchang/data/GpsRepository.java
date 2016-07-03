package com.yunguchang.data;

import com.google.common.collect.Iterables;
import com.yunguchang.gps.GpsUtil;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.persistence.*;
import com.yunguchang.sam.PrincipalExt;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

/**
 * Created by gongy on 9/9/2015.
 */
@Stateless
public class GpsRepository extends GenericRepository {


    private static final int pageSize = 20;


    @TransactionAttribute(REQUIRES_NEW)
    public void mergePoint(int gpsEntityId, GpsPoint point, DateTime persistTime) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<TGpsPointEntity> updateCriteria = cb.createCriteriaUpdate(TGpsPointEntity.class);
        Root<TGpsPointEntity> root = updateCriteria.from(TGpsPointEntity.class);
        int section = GpsUtil.getPersistSection(persistTime);
        updateCriteria.set(root.get("lat" + section), point.getLat());
        updateCriteria.set(root.get("lng" + section), point.getLng());
        updateCriteria.set(root.get("speed" + section), point.getSpeed());
        updateCriteria.set(root.get("sampleTime" + section), point.getSampleTime());
        updateCriteria.where(
                cb.equal(
                        root.get(TGpsPointEntity_.id), gpsEntityId
                )
        );
        em.createQuery(updateCriteria).executeUpdate();
        em.flush();
    }


    public List<TAzCarinfoEntity> getCarIdToAdjusted() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TGpsPointEntity> root = cq.from(TGpsPointEntity.class);
        cq.select(root.get(TGpsPointEntity_.car));
        cq.orderBy(cb.desc(root.get(TGpsPointEntity_.pointTime)));
        cq.distinct(true);
        return em.createQuery(cq).getResultList();
    }

    public List<TGpsPointEntity> getGpsRecordByCarIdAndAfterPointTime(String carid, DateTime pointTime, Integer limit) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsPointEntity> cq = cb.createQuery(TGpsPointEntity.class);
        Root<TGpsPointEntity> gpsPoints = cq.from(TGpsPointEntity.class);
        cq.select(gpsPoints);

        Path<TAzCarinfoEntity> carPath = gpsPoints.get(TGpsPointEntity_.car);


        Predicate predicate = cb.equal(carPath.get(TAzCarinfoEntity_.id), carid);
        if (pointTime != null) {
            predicate = cb.and(
                    predicate,
                    cb.greaterThanOrEqualTo(gpsPoints.get(TGpsPointEntity_.pointTime), pointTime)

            );
        }
        cq.where(predicate);
        cq.orderBy(cb.asc(gpsPoints.get(TGpsPointEntity_.pointTime)));

        TypedQuery<TGpsPointEntity> query = em.createQuery(cq);
        if (limit != null) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public void saveAdjustedPath(List<TGpsAdjustedPath> paths) {
        for (TGpsAdjustedPath path : paths) {
            em.persist(path);
        }
        em.flush();

    }

    public TGpsPointEntity getLastGpsPointRecord(String carId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsPointEntity> cq = cb.createQuery(TGpsPointEntity.class);
        Root<TGpsPointEntity> gps = cq.from(TGpsPointEntity.class);
        cq.where(cb.equal(gps.join(TGpsPointEntity_.car).get(TAzCarinfoEntity_.id), carId));
        cq.orderBy(cb.desc(gps.get(TGpsPointEntity_.pointTime)));
        return Iterables.getFirst(em.createQuery(cq).setMaxResults(1).getResultList(), null);


    }

    public TGpsPointEntity getGpsRecordByCarIdAndAfterPointTime(String carId, DateTime pointTime) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsPointEntity> cq = cb.createQuery(TGpsPointEntity.class);
        Root<TGpsPointEntity> gps = cq.from(TGpsPointEntity.class);
        cq.where(
                cb.and(
                        cb.equal(gps.join(TGpsPointEntity_.car).get(TAzCarinfoEntity_.id), carId),
                        cb.equal(gps.get(TGpsPointEntity_.pointTime), pointTime)
                )
        );
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    public List<TGpsPointEntity> getGpsRecordByCarIdAndBeforeTime(String carId, DateTime dateTime, int size, PrincipalExt principalExt) {
        CriteriaQuery<TGpsPointEntity> cq = createGpsByCarIdAndBeforeTimeQuery(carId, dateTime);
        applySecurityFilter("cars", principalExt);
        return em.createQuery(cq).setMaxResults(size).getResultList();
    }

    public List<TGpsPointEntity> getGpsRecordByCarIdAndBeforeTime(String carId, DateTime dateTime, int size) {
        CriteriaQuery<TGpsPointEntity> cq = createGpsByCarIdAndBeforeTimeQuery(carId, dateTime);

        return em.createQuery(cq).setMaxResults(size).getResultList();
    }

    public TGpsAdjustedPath getLastAdjustedPathByCarId(String carId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsAdjustedPath> cq = cb.createQuery(TGpsAdjustedPath.class);
        Root<TGpsAdjustedPath> root = cq.from(TGpsAdjustedPath.class);
        cq.select(root);
        cq.where(
                cb.equal(
                        root.get(TGpsAdjustedPath_.car).get(TAzCarinfoEntity_.id), carId
                )
        );
        cq.orderBy(cb.desc(root.get(TGpsAdjustedPath_.end)));
        return Iterables.getFirst(em.createQuery(cq).setMaxResults(1).getResultList(), null);
    }

    private CriteriaQuery<TGpsPointEntity> createGpsByCarIdAndBeforeTimeQuery(String carId, DateTime dateTime) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsPointEntity> cq = cb.createQuery(TGpsPointEntity.class);
        Root<TGpsPointEntity> gps = cq.from(TGpsPointEntity.class);
        cq.where(
                cb.and(
                        cb.equal(gps.join(TGpsPointEntity_.car).get(TAzCarinfoEntity_.id), carId),
                        cb.lessThanOrEqualTo(gps.get(TGpsPointEntity_.pointTime), dateTime)
                )
        );
        cq.orderBy(cb.desc(gps.get(TGpsPointEntity_.pointTime)));
        return cq;
    }

    public List<TGpsPointEntity> getGpsRecordByCarIdAndStartEnd(String carId, DateTime start, DateTime end, PrincipalExt principalExt) {
        DateTime pointTimeStart = GpsUtil.getPersistPointTime(start);
        DateTime pointTimeEnd = GpsUtil.getPersistPointTime(end);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsPointEntity> cq = cb.createQuery(TGpsPointEntity.class);
        Root<TGpsPointEntity> gps = cq.from(TGpsPointEntity.class);
        cq.where(
                cb.and(
                        cb.equal(gps.join(TGpsPointEntity_.car).get(TAzCarinfoEntity_.id), carId),
                        cb.greaterThanOrEqualTo(gps.get(TGpsPointEntity_.pointTime), pointTimeStart),
                        cb.lessThanOrEqualTo(gps.get(TGpsPointEntity_.pointTime), pointTimeEnd)
                )
        );
        cq.orderBy(cb.asc(gps.get(TGpsPointEntity_.pointTime)));
        applySecurityFilter("cars", principalExt);

        return em.createQuery(cq).getResultList();
    }

    public List<TGpsAdjustedPath> getValidGpsAdjustedPathByCarIdAndTime(String carId, DateTime start, DateTime end, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsAdjustedPath> cq = cb.createQuery(TGpsAdjustedPath.class);
        Root<TGpsAdjustedPath> adjustedPath = cq.from(TGpsAdjustedPath.class);
        cq.where(
                cb.and(
                        cb.equal(adjustedPath.join(TGpsAdjustedPath_.car).get(TAzCarinfoEntity_.id), carId),
                        cb.greaterThanOrEqualTo(adjustedPath.get(TGpsAdjustedPath_.start), start),
                        cb.lessThanOrEqualTo(adjustedPath.get(TGpsAdjustedPath_.end), end),
                        cb.or(
                                cb.equal(adjustedPath.get(TGpsAdjustedPath_.status), 0),
                                cb.equal(adjustedPath.get(TGpsAdjustedPath_.status), 1),
                                cb.isNull(adjustedPath.get(TGpsAdjustedPath_.status))
                        )
                )
        );
        cq.orderBy(cb.asc(adjustedPath.get(TGpsAdjustedPath_.start)));
        applySecurityFilter("cars", principalExt);

        return em.createQuery(cq).getResultList();
    }


    public List<TGpsAdjustedPath> getAllGpsAdjustedPathByCarIdAndTime(String carId, DateTime start, DateTime end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsAdjustedPath> cq = cb.createQuery(TGpsAdjustedPath.class);
        Root<TGpsAdjustedPath> adjustedPath = cq.from(TGpsAdjustedPath.class);
        cq.where(
                cb.and(
                        cb.equal(adjustedPath.join(TGpsAdjustedPath_.car).get(TAzCarinfoEntity_.id), carId),
                        cb.greaterThanOrEqualTo(adjustedPath.get(TGpsAdjustedPath_.start), start),
                        cb.lessThanOrEqualTo(adjustedPath.get(TGpsAdjustedPath_.end), end)

                )
        );
        cq.orderBy(cb.asc(adjustedPath.get(TGpsAdjustedPath_.start)));
        return em.createQuery(cq).getResultList();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<TGpsAdjustedPath> getLimitedValidGpsAdjustedPathByCarIdAndBeforeTime(String carId, DateTime time, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsAdjustedPath> cq = cb.createQuery(TGpsAdjustedPath.class);
        Root<TGpsAdjustedPath> adjustedPath = cq.from(TGpsAdjustedPath.class);
        Fetch<TGpsAdjustedPath, TAzCarinfoEntity> fetchCar = adjustedPath.fetch(TGpsAdjustedPath_.car);
        Join<TGpsAdjustedPath, TAzCarinfoEntity>  joinCar= (Join<TGpsAdjustedPath, TAzCarinfoEntity>) fetchCar;
        cq.where(
                cb.and(
                        cb.equal(joinCar.get(TAzCarinfoEntity_.id), carId),
                        cb.lessThan(adjustedPath.get(TGpsAdjustedPath_.start), time),
                        cb.equal(adjustedPath.get(TGpsAdjustedPath_.status), 0)

                )
        );
        cq.orderBy(cb.desc(adjustedPath.get(TGpsAdjustedPath_.start)));
        return em.createQuery(cq).setMaxResults(size).getResultList();
    }


    @TransactionAttribute(REQUIRES_NEW)
    public List<TGpsAdjustedPath> getLimitedGpsAdjustedPathByCarIdAndAfterTime(String carId, DateTime start, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsAdjustedPath> cq = cb.createQuery(TGpsAdjustedPath.class);
        Root<TGpsAdjustedPath> adjustedPath = cq.from(TGpsAdjustedPath.class);
        Fetch<TGpsAdjustedPath, TAzCarinfoEntity> fetchCar = adjustedPath.fetch(TGpsAdjustedPath_.car);
        Join<TGpsAdjustedPath, TAzCarinfoEntity>  joinCar= (Join<TGpsAdjustedPath, TAzCarinfoEntity>) fetchCar;
        cq.where(
                cb.and(
                        cb.equal(joinCar.get(TAzCarinfoEntity_.id), carId),
                        cb.greaterThan(adjustedPath.get(TGpsAdjustedPath_.start), start)

                )
        );
        cq.orderBy(cb.asc(adjustedPath.get(TGpsAdjustedPath_.start)));
        return em.createQuery(cq).setMaxResults(size).getResultList();
    }

    /**
     * 根据 驾驶员ID 查询 GPS 点集合 (时间段内的所有点的集合)
     *
     * @param driverId
     * @param start
     * @param end
     * @return
     */
    public List<TGpsPointEntity> getGpsRecordByDriverIdAndStartEnd(String driverId, DateTime start, DateTime end) {

        DateTime pointTimeStart = GpsUtil.getPersistPointTime(start);
        DateTime pointTimeEnd = GpsUtil.getPersistPointTime(end);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsPointEntity> cq = cb.createQuery(TGpsPointEntity.class);
        Root<TGpsPointEntity> gps = cq.from(TGpsPointEntity.class);
        Predicate predicate = cb.equal(gps.join(TGpsPointEntity_.car).join(TAzCarinfoEntity_.driver).get(TRsDriverinfoEntity_.uuid), driverId);

        predicate = cb.and(predicate, cb.lessThanOrEqualTo(gps.get(TGpsPointEntity_.pointTime), pointTimeEnd));
        predicate = cb.and(predicate, cb.greaterThanOrEqualTo(gps.get(TGpsPointEntity_.pointTime), pointTimeStart));

        cq.where(predicate);

        cq.orderBy(cb.desc(gps.get(TGpsPointEntity_.pointTime)));
        return em.createQuery(cq).getResultList();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TGpsAdjustedPath getOneInvalidAdjustedPathByCarIdAndEndBeforeTime(String carId, DateTime time) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TGpsAdjustedPath> cq = cb.createQuery(TGpsAdjustedPath.class);
        Root<TGpsAdjustedPath> adjustedPath = cq.from(TGpsAdjustedPath.class);
        Fetch<TGpsAdjustedPath, TAzCarinfoEntity> fetchCar = adjustedPath.fetch(TGpsAdjustedPath_.car);
        Join<TGpsAdjustedPath, TAzCarinfoEntity> joinCar= (Join<TGpsAdjustedPath, TAzCarinfoEntity>) fetchCar;
        cq.where(

                cb.equal(adjustedPath.get(TGpsAdjustedPath_.status), 1),
                cb.equal(joinCar.get(TAzCarinfoEntity_.id), carId),
                cb.lessThan(adjustedPath.get(TGpsAdjustedPath_.end), time)


        );
        cq.orderBy(cb.asc(adjustedPath.get(TGpsAdjustedPath_.start)));
        return Iterables.getFirst(em.createQuery(cq).setMaxResults(1).getResultList(), null);
    }

    @TransactionAttribute(REQUIRES_NEW)
    public void fixAdjustedPath(TGpsAdjustedPath validAdjustedPath, List<Integer> overwriteIds) {
        em.persist(validAdjustedPath);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<TGpsAdjustedPath> cq= cb.createCriteriaUpdate(TGpsAdjustedPath.class);
        Root<TGpsAdjustedPath> root = cq.from(TGpsAdjustedPath.class);
        cq.set(root.get(TGpsAdjustedPath_.status), 2);
        cq.where(
                root.get(TGpsAdjustedPath_.id).in(overwriteIds)
        );
        em.createQuery(cq).executeUpdate();
        em.flush();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public void setAdjustedPathAsValid(TGpsAdjustedPath adjustedPath) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<TGpsAdjustedPath> cq= cb.createCriteriaUpdate(TGpsAdjustedPath.class);
        Root<TGpsAdjustedPath> root = cq.from(TGpsAdjustedPath.class);
        cq.set(root.get(TGpsAdjustedPath_.status), 0);
        cq.where(
                cb.equal(root.get(TGpsAdjustedPath_.id), adjustedPath.getId())
        );
        em.createQuery(cq).executeUpdate();
        em.flush();
    }

    public List<TGpsPointEntity> getLastGpsPointOfEachCar() {
        Query query = em.createNativeQuery(
                "SELECT a.* FROM  t_gps_point a, " +
                        "( " +
                        "select max(pointtime) pointtime, carid " +
                        "from t_gps_point c " +
                        "group by c.carid " +
                        ")d " +
                        "where " +
                        "a.pointtime=d.pointtime and  a.carid=d.carid " +
                        "order by a.carid", TGpsPointEntity.class
        );


        List<TGpsPointEntity> results = query.getResultList();
        for (TGpsPointEntity point : results) {
            Hibernate.initialize(point.getCar());
        }


        return  results;
    }


    public List<TAzCarinfoEntity> getCarsNoGpsMoreThan(int days) {
//        select id from t_az_carinfo
//        where not exists
//                (
//                        select * from t_gps_point g
//                        where
//                        g.carid=id
//                        and
//                        g.pointTime >= '2011-01-1'
//
//                )
//      and GPSAZQK ='1'
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> carRoot = cq.from(TAzCarinfoEntity.class);

        Subquery<TGpsPointEntity> gpsQuery = cq.subquery(TGpsPointEntity.class);
        Root<TGpsPointEntity> gpsRoot = gpsQuery.from(TGpsPointEntity.class);
        gpsQuery.select(gpsRoot);

        DateTime time = DateTime.now().minusDays(days).withTimeAtStartOfDay();
        gpsQuery.where(
                cb.and(
                        cb.equal(gpsRoot.get(TGpsPointEntity_.car), carRoot),
                        cb.greaterThanOrEqualTo(gpsRoot.get(TGpsPointEntity_.pointTime), time)
                )
        );
        cq.where(
                cb.not(
                        cb.exists(gpsQuery)
                ),
                cb.equal(carRoot.get(TAzCarinfoEntity_.gpsazqk), "1")
        );


        return em.createQuery(cq).getResultList();


    }


    @TransactionAttribute(REQUIRES_NEW)
    public TGpsPointEntity createRecord(String carId, DateTime pointTime) {
        TGpsPointEntity gpsPointEntity = new TGpsPointEntity();
        TAzCarinfoEntity car = em.getReference(TAzCarinfoEntity.class, carId);
        gpsPointEntity.setCar(car);
        gpsPointEntity.setPointTime(pointTime);
        em.persist(gpsPointEntity);
        em.flush();
        em.detach(gpsPointEntity);
        return gpsPointEntity;
    }
}
