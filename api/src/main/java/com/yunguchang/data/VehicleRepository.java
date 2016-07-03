package com.yunguchang.data;

import com.google.common.collect.Iterables;
import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.*;
import com.yunguchang.sam.PrincipalExt;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

/**
 * Created by gongy on 8/20/2015.
 */
@Stateless
public class VehicleRepository extends GenericRepository {

    public List<TAzCarinfoEntity> listAllCars() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> car = cq.from(TAzCarinfoEntity.class);
        cq.select(car);
        return em.createQuery(cq).getResultList();
    }


    /**
     * 查询所有的车辆信息
     *
     * @param badge
     * @param fleet
     * @param keyword
     * @param fleetId
     * @param isMoving
     * @param state
     * @param principalExt
     * @return
     */
    public List<TAzCarinfoEntity> listAllCars(String badge, String fleet, String keyword, String fleetId, Integer isMoving, CarState state, Boolean lastGps, Boolean gpsInstalled, Integer offset, Integer limit, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> car = cq.from(TAzCarinfoEntity.class);
        cq.select(car);
        Fetch<TAzCarinfoEntity, TSysOrgEntity> fetchFleet = car.fetch(TAzCarinfoEntity_.sysOrg);
        Join<TAzCarinfoEntity, TSysOrgEntity> joinFleet = (Join<TAzCarinfoEntity, TSysOrgEntity>) fetchFleet;
        car.fetch(TAzCarinfoEntity_.driver, JoinType.LEFT);
        cq.orderBy(cb.asc(car.get(TAzCarinfoEntity_.cphm)));
        Predicate predicate = cb.conjunction();
        if (badge != null) {
            predicate = cb.and(predicate, cb.like(car.get(TAzCarinfoEntity_.cphm), "%" + badge + "%"));
        }
        if (fleet != null) {
            predicate = cb.and(predicate, cb.like(joinFleet.get(TSysOrgEntity_.orgname), "%" + fleet + "%"));
        }

        if (fleetId != null) {
            predicate = cb.and(predicate, cb.like(joinFleet.get(TSysOrgEntity_.orgid), fleetId));
        }

        if (keyword != null) {
            predicate =
                    cb.and(predicate,
                            cb.or(
                                    cb.like(car.get(TAzCarinfoEntity_.cphm), "%" + keyword + "%"),
                                    cb.like(joinFleet.get(TSysOrgEntity_.orgname), "%" + keyword + "%")
                            )

                    );

        }

        if (state != null) {
            if (state.equals(CarState.REPAIRING)) {
                predicate =
                        cb.and(predicate,
                                cb.equal(car.get(TAzCarinfoEntity_.repairingState), RepairingState.ONGOING.id())
                        );
            } else if (state.equals(CarState.AWAITING_REPAIRE)) {
                predicate =
                        cb.and(predicate,
                                cb.equal(car.get(TAzCarinfoEntity_.repairingState), RepairingState.REQUESTED.id())
                        );
            } else if (state.equals(CarState.INUSE) || state.equals(CarState.IDLE)) {
                DateTime now = DateTime.now();

                Subquery<TBusScheduleCarEntity> subquery = cq.subquery(TBusScheduleCarEntity.class);
                Root<TBusScheduleCarEntity> scheduleCar = subquery.from(TBusScheduleCarEntity.class);
                Join<TBusScheduleCarEntity, TBusScheduleRelaEntity> schedule = scheduleCar.join(TBusScheduleCarEntity_.schedule);

                subquery.select(scheduleCar);
                subquery.where(
                        cb.and(
                                cb.equal(scheduleCar.get(TBusScheduleCarEntity_.car), car),
                                cb.lessThanOrEqualTo(schedule.get(TBusScheduleRelaEntity_.starttime), now),
                                cb.greaterThanOrEqualTo(schedule.get(TBusScheduleRelaEntity_.endtime), now),
                                schedule.get(TBusScheduleRelaEntity_.status).in(ScheduleStatus.AWAITING.id(), ScheduleStatus.FINISHED.id())
                        )
                );

                Predicate scheduleCarExistsPredicate = cb.exists(subquery);
                Predicate noRepairingPredicate = cb.or(
                        cb.isNull(car.get(TAzCarinfoEntity_.repairingState)),
                        cb.equal(car.get(TAzCarinfoEntity_.repairingState), RepairingState.NONE.id())
                );
                if (state.equals(CarState.INUSE)) {
                    predicate =
                            cb.and(predicate,
                                    scheduleCarExistsPredicate,
                                    noRepairingPredicate
                            );
                } else {
                    predicate =
                            cb.and(predicate,
                                    cb.not(scheduleCarExistsPredicate),
                                    noRepairingPredicate);
                }

            }
        }


        if (isMoving != null) {
            Subquery<TGpsPointEntity> subquery = cq.subquery(TGpsPointEntity.class);
            Root<TGpsPointEntity> gpsPoint = subquery.from(TGpsPointEntity.class);
            subquery.select(gpsPoint);
            List<Expression<Double>> speedFields = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                speedFields.add(
                        cb.function("COALESCE", Double.class, gpsPoint.get("speed" + i), cb.literal(0))
                );
            }
            Predicate gpsSubPredicate = cb.and(
                    cb.equal(gpsPoint.get(TGpsPointEntity_.car), car)
            );
            switch (isMoving) {
                case 1:     // 行车 最后一个5分钟内的gps数据速度大于 5.0
                    gpsSubPredicate = cb.and(
                            gpsSubPredicate,
                            cb.greaterThanOrEqualTo(gpsPoint.get(TGpsPointEntity_.pointTime), DateTime.now().minusMinutes(5)),
                            cb.greaterThanOrEqualTo(cb.function("GREATEST", Double.class,speedFields.toArray(new Expression[]{})), 5.0)
                    );
                    predicate = cb.and(predicate, cb.exists(subquery.where(gpsSubPredicate)));
                    break;
                case 2:     // 停车 最后一个5分钟内的gps数据速度小于 5.0
                    gpsSubPredicate = cb.and(
                            gpsSubPredicate,
                            cb.greaterThanOrEqualTo(gpsPoint.get(TGpsPointEntity_.pointTime), DateTime.now().minusMinutes(5)),
                            cb.lessThan(cb.function("GREATEST", Double.class,speedFields.toArray(new Expression[]{})), 5.0)
                    );
                    predicate = cb.and(predicate, cb.exists(subquery.where(gpsSubPredicate)));
                    break;
                case 3 :    // 离线   最后一个gps数据距离现在 大于 5分钟
                    gpsSubPredicate = cb.and(
                            gpsSubPredicate,
                            cb.greaterThan(gpsPoint.get(TGpsPointEntity_.pointTime), DateTime.now().minusMinutes(5))
                    );
                    predicate = cb.and(
                            predicate,
                            cb.not(cb.exists(subquery.where(gpsSubPredicate)))
                    );
                    break;
            }
        }

        if (Boolean.TRUE.equals(gpsInstalled)) {
            predicate = cb.and(predicate, cb.equal(
                    car.get(TAzCarinfoEntity_.gpsazqk),
                    "1"
            ));
        } else if (Boolean.FALSE.equals(gpsInstalled)) {
            predicate = cb.and(predicate,
                    cb.or(
                            cb.equal(
                                    car.get(TAzCarinfoEntity_.gpsazqk),
                                    "0"),
                            cb.isNull(
                                    car.get(TAzCarinfoEntity_.gpsazqk)
                            )
                    )
            );
        }

        if (Boolean.TRUE.equals(lastGps)) {
            Fetch<TAzCarinfoEntity, TGpsPointEntity> fetchGpsPoints = car.fetch(TAzCarinfoEntity_.gpsPoints, JoinType.LEFT);
            Join<TAzCarinfoEntity, TGpsPointEntity> joinGpsPoints = (Join<TAzCarinfoEntity, TGpsPointEntity>) fetchGpsPoints;
            Subquery<DateTime> gpsSubQuery = cq.subquery(DateTime.class);
            Root<TGpsPointEntity> gpsRoot = gpsSubQuery.from(TGpsPointEntity.class);
            gpsSubQuery.select(
                    cb.greatest(gpsRoot.get(TGpsPointEntity_.pointTime))
            );
            gpsSubQuery.where(
                    cb.equal(
                            gpsRoot.get(TGpsPointEntity_.car), car
                    )

            );
            predicate = cb.and(predicate, cb.or(
                    cb.equal(joinGpsPoints.get(TGpsPointEntity_.pointTime), gpsSubQuery)

            ));
        }
        cq.where(predicate);

        applySecurityFilter("cars", principalExt);

        TypedQuery<TAzCarinfoEntity> query = em.createQuery(cq);
        if (offset != null) {
            query.setFirstResult(offset);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }


    public TAzCarinfoEntity getCarById(String id, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> car = cq.from(TAzCarinfoEntity.class);
        car.fetch(TAzCarinfoEntity_.driver, JoinType.LEFT);
        car.fetch(TAzCarinfoEntity_.sysOrg);
        cq.where(cb.equal(car.get(TAzCarinfoEntity_.id), id));
        applySecurityFilter("cars", principalExt);


        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    public String getFleetIdOfCar(String carId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<TAzCarinfoEntity> car = cq.from(TAzCarinfoEntity.class);
        cq.select(car.join(TAzCarinfoEntity_.sysOrg).get(TSysOrgEntity_.orgid).as(String.class));
        cq.where(
                cb.equal(car.get(TAzCarinfoEntity_.id), carId)
        );

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);


    }

    public List<TAzCarinfoEntity> listAllCarsWithGPS() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> car = cq.from(TAzCarinfoEntity.class);
        cq.where(cb.equal(car.get(TAzCarinfoEntity_.gpsazqk), "1"));
        TypedQuery<TAzCarinfoEntity> query = em.createQuery(cq);
        return query.getResultList();
    }

    public TAzCarinfoEntity getCarByBadge(String badge) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> car = cq.from(TAzCarinfoEntity.class);
        cq.where(cb.like(car.get(TAzCarinfoEntity_.cphm), "%" + badge + "%"));
        TypedQuery<TAzCarinfoEntity> query = em.createQuery(cq);
        return Iterables.getFirst(query.setMaxResults(1).getResultList(), null);

    }

    public TAzCarinfoEntity getCarByDriverId(String driverId, PrincipalExt principalExt){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> car = cq.from(TAzCarinfoEntity.class);
        cq.where(cb.equal(
                car.get(TAzCarinfoEntity_.driver).get(TRsDriverinfoEntity_.uuid),driverId
        ));
        TypedQuery<TAzCarinfoEntity> query = em.createQuery(cq);
        applySecurityFilter("cars", principalExt);
        return Iterables.getFirst(query.setMaxResults(1).getResultList(), null);
    }


    @TransactionAttribute(REQUIRES_NEW)
    public void saveReturningDepot(TBusReturningDepotEntity busReturningDepotEntity) {
        em.persist(busReturningDepotEntity);
    }


    public List<TAzJzRelaEntity> listAllLicenceClassAndVehicleMappings() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzJzRelaEntity> cq = cb.createQuery(TAzJzRelaEntity.class);
        cq.from(TAzJzRelaEntity.class);
        return em.createQuery(cq).getResultList();
    }

    public TAzCarinfoEntity getCarById(String id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> car = cq.from(TAzCarinfoEntity.class);
        car.fetch(TAzCarinfoEntity_.driver, JoinType.LEFT);
        cq.where(cb.equal(car.get(TAzCarinfoEntity_.id), id));
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    /**
     * 更新车辆的停车点信息
     *
     * @param carId
     * @param depot
     * @return
     */
    @TransactionAttribute(REQUIRES_NEW)
    public TAzCarinfoEntity updateDepotOfCar(String carId, Depot depot) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> carRoot = cq.from(TAzCarinfoEntity.class);
        cq.where(
                cb.equal(carRoot.get(TAzCarinfoEntity_.id), carId)
        );
        List<TAzCarinfoEntity> results = em.createQuery(cq).getResultList();
        if (results.size() > 0) {
            TAzCarinfoEntity carEntity = results.get(0);
            TEmbeddedDepot depotEntity = carEntity.getDepot();
            if (depotEntity == null) {
                depotEntity = new TEmbeddedDepot();
                carEntity.setDepot(depotEntity);
            }
            depotEntity.setLatitude(depot.getLat());
            depotEntity.setLongitude(depot.getLng());
            depotEntity.setName(depot.getName());
            return carEntity;
        } else {
            return null;
        }
    }

    /**
     * 新增车辆信息
     *
     * @param carInfo 车辆信息
     * @return
     */
    @TransactionAttribute(REQUIRES_NEW)
    public TAzCarinfoEntity createCarWithPermission(TAzCarinfoEntity carInfo, PrincipalExt principalExt) {
        if (carInfo == null || StringUtils.isBlank(carInfo.getCphm())) {
            throw new InvalidParameterException("Car info is not valid!");
        }
        if (StringUtils.isNotBlank(carInfo.getId())) {
            TAzCarinfoEntity carEntity = em.find(TAzCarinfoEntity.class, carInfo.getId());
            if (carEntity != null) {
                throw new KeyAlreadyExistsException("Car already exists");
            }
        }
        // 驾驶员
        if (null != carInfo.getDriver() && StringUtils.isNotBlank(carInfo.getDriver().getUuid())) {
            TRsDriverinfoEntity driverinfoEntity = em.find(TRsDriverinfoEntity.class, carInfo.getDriver().getUuid());
            if (null == driverinfoEntity) {
                throw new EntityNotFoundException("Driver is not exist!");
            }
            carInfo.setDriver(driverinfoEntity);
        }
        // 所属车队
        if (null != carInfo.getSysOrg() && StringUtils.isNotBlank(carInfo.getSysOrg().getOrgid())) {
            TSysOrgEntity fleet = em.find(TSysOrgEntity.class, carInfo.getSysOrg().getOrgid());
            if (null == fleet) {
                throw new EntityNotFoundException("Driver is not exist!");
            }
            carInfo.setSysOrg(fleet);
        }
        if (carInfo.getId() == null) {
            em.persist(carInfo);
        } else {
            carInfo = em.merge(carInfo);
        }
        return carInfo;
    }

    /**
     * 更新车辆GPS信息
     *
     * @param carId  车辆ID
     * @param gpsflg 车辆GPS信息
     * @return
     */
    @TransactionAttribute(REQUIRES_NEW)
    public TAzCarinfoEntity updateCarWithPermission(String carId, String gpsflg, PrincipalExt principalExt) {
        if (carId == null) {
            throw new EntityNotFoundException("Car is not found!");
        }
        TAzCarinfoEntity carinfoEntity = em.find(TAzCarinfoEntity.class, carId);
        if (null == carinfoEntity) {
            throw new EntityNotFoundException("Car is not found!");
        }
        // 02 未安装 01 安装         ==>>   GPS安装情况-   1：是；0：否
        // 02	未安装 04	缓装 01	已安装  05	其他   03	不安装
        if (null != gpsflg && ("01".equals(gpsflg) || "1".equals(gpsflg))) {
            carinfoEntity.setGpsazqk("1");
        } else {
            carinfoEntity.setGpsazqk("0");
        }

        return carinfoEntity;
    }

    /**
     * 更新车辆信息
     *
     * @param carId   车辆ID
     * @param carInfo 车辆信息
     * @return
     */
    @TransactionAttribute(REQUIRES_NEW)
    public TAzCarinfoEntity updateCarWithPermission(String carId, TAzCarinfoEntity carInfo, boolean inner, PrincipalExt principalExt) {
        if (carId == null || carInfo == null) {
            throw new EntityNotFoundException("Car is not found!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> carRoot = cq.from(TAzCarinfoEntity.class);
        cq.where(cb.equal(carRoot.get(TAzCarinfoEntity_.id), carId));

        List<TAzCarinfoEntity> carEntities = em.createQuery(cq).getResultList();
        if (carEntities.size() != 1) {
            //error no such car ??
            throw new EntityNotFoundException("Car is not found!");
        }

        TAzCarinfoEntity carEntity = carEntities.get(0);
        // 更新内容
        // 车牌号
        if (carInfo.getCphm() != null) {
            carEntity.setCphm(carInfo.getCphm());
        }
        String fleetId = null;
        // 驾驶员
        if (carEntity.getDriver() == null || !carEntity.getDriver().equals(carInfo.getDriver())) {
            // 原来没有驾驶员  或者  新的驾驶员和旧的驾驶员不同
            CriteriaQuery<TRsDriverinfoEntity> cqDriver = cb.createQuery(TRsDriverinfoEntity.class);
            Root<TRsDriverinfoEntity> driverRoot = cqDriver.from(TRsDriverinfoEntity.class);
            cqDriver.where(cb.equal(driverRoot.get(TRsDriverinfoEntity_.uuid), carInfo.getDriver().getUuid()));

            List<TRsDriverinfoEntity> driverResults = em.createQuery(cqDriver).getResultList();
            if (driverResults.size() == 1) {
                carEntity.setDriver(driverResults.get(0));
                // 内部移交使用
                if (inner) {
                    fleetId = driverResults.get(0).getDepartment().getOrgid();
                }
            } else if (carInfo.getDriver().getUuid() != null) {
                // driver is not exist, return null to tell caller the data is wrong.
            }
        }
        // 所属车队
        if (carEntity.getSysOrg() == null || !carEntity.getSysOrg().getOrgid().equals(carInfo.getSysOrg().getOrgid())) {
            // 原来没有车队  或者  新的车队信息和旧的车队信息不同
            CriteriaQuery<TSysOrgEntity> cqOrg = cb.createQuery(TSysOrgEntity.class);
            Root<TSysOrgEntity> orgRoot = cqOrg.from(TSysOrgEntity.class);
            // 车队变更 非 内部移交
            if (carInfo.getSysOrg().getOrgid() != null) {
                fleetId = carInfo.getSysOrg().getOrgid();
            }
            cqOrg.where(cb.equal(orgRoot.get(TSysOrgEntity_.orgid), fleetId));

            List<TSysOrgEntity> orgResults = em.createQuery(cqOrg).getResultList();
            if (inner && orgResults.size() == 1) {
                carEntity.setSysOrg(orgResults.get(0));
            } else if (!inner) {
                // 移交到外部
                carEntity.setSysOrg(null);
            } else {
                // fleet is not exist, return null to tell caller the data is wrong.
            }
        }
        // 是否安装GPS 1-安装 0 未安装
        if(StringUtils.isNotBlank(carInfo.getGpsazqk())) {
            carEntity.setGpsazqk(carInfo.getGpsazqk());
        }
        // 车辆型号
        carEntity.setCllx(carInfo.getCllx());

        carEntity.setStatus("1");       // 设置为启用

        return carEntity;
    }

    /**
     * 删除车辆信息(逻辑删除)
     *
     * @param carId 车辆ID
     * @return
     */
    @TransactionAttribute(REQUIRES_NEW)
    public boolean deleteCarWithPermission(String carId, PrincipalExt principalExt) {
        if (carId == null) {
            throw new EntityNotFoundException("Car is not found!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> carRoot = cq.from(TAzCarinfoEntity.class);
        cq.where(cb.equal(carRoot.get(TAzCarinfoEntity_.id), carId),
                cb.equal(carRoot.get(TAzCarinfoEntity_.status), "1"));

        List<TAzCarinfoEntity> results = em.createQuery(cq).getResultList();

        if (results.size() != 1) {
            throw new EntityNotFoundException("Car is not found!");
        }
        // 状态，默认为1，逻辑删除时修改为0
        results.get(0).setStatus("0");

        return true;
    }

    /**
     * 更新车辆维修状况
     *
     * @param carId           车辆ID
     * @param azCarinfoEntity 车辆信息
     * @return
     */
    @TransactionAttribute(REQUIRES_NEW)
    public TAzCarinfoEntity updateCarRepairInfoWithPermission(String carId, TAzCarinfoEntity azCarinfoEntity, PrincipalExt principalExt) {
        if (carId == null || azCarinfoEntity == null) {
            throw new EntityNotFoundException("Car or CarRepairInfo is not correct!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> carRoot = cq.from(TAzCarinfoEntity.class);
        cq.where(cb.equal(carRoot.get(TAzCarinfoEntity_.id), carId));

        List<TAzCarinfoEntity> carEntities = em.createQuery(cq).getResultList();
        TAzCarinfoEntity carEntity;
        if (carEntities.size() == 1) {
            carEntity = carEntities.get(0);
        } else {
            throw new EntityNotFoundException("Car is not exist!");
        }

        // 修车状态
        if (azCarinfoEntity.getRepairingStateTime() != null && azCarinfoEntity.getRepairingState() != null) {
            carEntity.setRepairingStateTime(azCarinfoEntity.getRepairingStateTime());
            carEntity.setRepairingState(azCarinfoEntity.getRepairingState());
        } else {
            carEntity.setRepairingStateTime(null);
            carEntity.setRepairingState(RepairingState.NONE.id());
        }
        if (azCarinfoEntity.getCphm() != null) {
            carEntity.setCphm(azCarinfoEntity.getCphm());
        }

        return carEntity;
    }

    /**
     * 更新车辆违章次数
     *
     * @param carId         车辆ID
     * @param carInfoEntity 车辆信息
     * @return
     */
    @TransactionAttribute(REQUIRES_NEW)
    public TAzCarinfoEntity updateViolationInfoWithPermission(String carId, TAzCarinfoEntity carInfoEntity, PrincipalExt principalExt) {
        if (carId == null || carInfoEntity == null) {
            throw new EntityNotFoundException("Car or CarInfo is not correct!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<TAzCarinfoEntity> cq = cb.createQuery(TAzCarinfoEntity.class);
        Root<TAzCarinfoEntity> carRoot = cq.from(TAzCarinfoEntity.class);
        cq.where(cb.equal(carRoot.get(TAzCarinfoEntity_.id), carId),
                cb.equal(carRoot.get(TAzCarinfoEntity_.status), "1"));

        List<TAzCarinfoEntity> carEntities = em.createQuery(cq).getResultList();
        if (carEntities.size() != 1) {
            //error no such car ??
            throw new EntityNotFoundException("Car is not exist!");
        }

        TAzCarinfoEntity carEntity = carEntities.get(0);

        if (carInfoEntity.getViolationTimes() != null) {
            // 违章信息-次数
            carEntity.setViolationTimes(carInfoEntity.getViolationTimes());
        }

        return carEntity;
    }

    public int countCarWithGpsState(Boolean gpsInstalled, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TAzCarinfoEntity> carRoot = cq.from(TAzCarinfoEntity.class);

        cq.select(cb.countDistinct(carRoot)).distinct(true);

        Predicate predicate = cb.equal(carRoot.get(TAzCarinfoEntity_.status), "1");

        if(gpsInstalled != null) {
            predicate = cb.and(
                    predicate,
                    cb.equal(carRoot.get(TAzCarinfoEntity_.gpsazqk), gpsInstalled ? "1" : "0")
            );
        }
        cq.where(predicate);

        applySecurityFilter("cars", principalExt);

        return em.createQuery(cq).getSingleResult().intValue();
    }
}
