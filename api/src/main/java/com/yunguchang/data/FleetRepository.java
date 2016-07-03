package com.yunguchang.data;

import com.google.common.collect.Iterables;
import com.yunguchang.model.persistence.*;
import com.yunguchang.sam.PrincipalExt;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.*;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by WHB on 2015-09-22.
 */
@Stateless
public class FleetRepository extends GenericRepository {

    private static final String fleetCode = "00107";

    /**
     * 获得所有车队信息
     *
     * @param keyword
     * @param principalExtOrNull
     * @return
     */
    public List<TSysOrgEntity> listFleet(String keyword, PrincipalExt principalExtOrNull) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysOrgEntity> cq = cb.createQuery(TSysOrgEntity.class);

        // select * from t_sys_org
        Root<TSysOrgEntity> orgInfoRoot = cq.from(TSysOrgEntity.class);
        cq.select(orgInfoRoot);

        Predicate predicate = cb.equal(orgInfoRoot.get(TSysOrgEntity_.enabled), 1);
        predicate = cb.and(predicate,
                cb.like(orgInfoRoot.get(TSysOrgEntity_.orgid), fleetCode + "_%")
//                , cb.equal(orgInfoRoot.get(TSysOrgEntity_.parentid), fleetCode)
        );

        // 车队名称  OR  停车场名称
        if (StringUtils.isNotBlank(keyword)) {
            predicate = cb.and(predicate, cb.or(cb.like(orgInfoRoot.get(TSysOrgEntity_.orgname), "%" + keyword + "%")));
        }
        cq.where(predicate);
        cq.orderBy(cb.desc(orgInfoRoot.get(TSysOrgEntity_.sortno)));

        return em.createQuery(cq).getResultList();
    }

    /**
     * 更新车队信息
     *
     * @param fleet   车队信息
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TSysOrgEntity createFleetWithPermission(TSysOrgEntity fleet, PrincipalExt principalExt) {
        if (fleet == null) {
            throw new InvalidParameterException("Fleet info is not valid!");
        }
        TSysOrgEntity result = em.find(TSysOrgEntity.class, fleet.getOrgid());
        if (null != result && "1".equals(result.getEnabled())) {
            throw new KeyAlreadyExistsException("Fleet is already exist!");
        }
        TSysOrgEntity parentOrg = em.find(TSysOrgEntity.class, fleet.getParent().getOrgid());
        if(parentOrg == null) {
            throw new EntityNotFoundException("Parent Fleet is not found!");
        }

        fleet.setEnabled("1");
        if(fleet.getOrgid() == null) {
            em.persist(fleet);
        } else {
            fleet = em.merge(fleet);
        }

        return fleet;
    }

    /**
     * 更新车队信息
     *
     * @param fleetId 车队ID
     * @param fleet   车队信息
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TSysOrgEntity updateFleetWithPermission(String fleetId, TSysOrgEntity fleet, PrincipalExt principalExt) {
        if (fleetId == null || fleet == null) {
            throw new EntityNotFoundException("Fleet is not found!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<TSysOrgEntity> cq = cb.createQuery(TSysOrgEntity.class);
        Root<TSysOrgEntity> fleetRoot = cq.from(TSysOrgEntity.class);
        cq.where(cb.equal(fleetRoot.get(TSysOrgEntity_.orgid), fleetId));

        List<TSysOrgEntity> results = em.createQuery(cq).getResultList();
        if (results.size() == 0) {
            //error no such fleet ??
            throw new EntityNotFoundException("Fleet is not found!");
        }

        TSysOrgEntity fleetEntity = results.get(0);
        // 更新内容
        fleetEntity.setOrgname(fleet.getOrgname());
        fleetEntity.setParent(em.getReference(TSysOrgEntity.class, fleet.getParent().getOrgid()));
        fleetEntity.setEnabled("1");            // 设置为启用

        return fleetEntity;
    }

    /**
     * 根据名称获得车队信息
     *
     * @param name
     * @return
     */
    public TSysOrgEntity getFleetByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysOrgEntity> cq = cb.createQuery(TSysOrgEntity.class);
        Root<TSysOrgEntity> fleet = cq.from(TSysOrgEntity.class);
        cq.where(
                cb.like(fleet.get(TSysOrgEntity_.orgname), "%" + name + "%")
        );
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }

    /**
     * 根据ID获得车队信息
     *
     * @param fleetId
     * @return
     */
    public TSysOrgEntity getFleetById(String fleetId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysOrgEntity> cq = cb.createQuery(TSysOrgEntity.class);
        Root<TSysOrgEntity> fleet = cq.from(TSysOrgEntity.class);
        cq.where(cb.equal(fleet.get(TSysOrgEntity_.orgid), fleetId),
                cb.equal(fleet.get(TSysOrgEntity_.enabled), "1"));
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);

    }


    /**
     * 根据车辆ID获得车队信息
     *
     * @param carId
     * @return
     */
    public TSysOrgEntity getFleetByCarId(String carId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysOrgEntity> cq = cb.createQuery(TSysOrgEntity.class);
        Root<TAzCarinfoEntity> car = cq.from(TAzCarinfoEntity.class);
        Path<TSysOrgEntity> fleet = car.get(TAzCarinfoEntity_.sysOrg);
        cq.select(fleet);
        cq.where(cb.equal(car.get(TAzCarinfoEntity_.id), carId),
                cb.equal(fleet.get(TSysOrgEntity_.enabled), "1"));
        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);

    }


    /**
     * 删除车队信息(逻辑删除)
     *
     * @param fleetId 车队ID
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean deleteFleetWithPermission(String fleetId, PrincipalExt principalExt) {
        if (fleetId == null) {
            throw new EntityNotFoundException("Fleet is not found!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<TSysOrgEntity> cq = cb.createQuery(TSysOrgEntity.class);
        Root<TSysOrgEntity> fleetRoot = cq.from(TSysOrgEntity.class);
        cq.where(cb.equal(fleetRoot.get(TSysOrgEntity_.orgid), fleetId),
                cb.equal(fleetRoot.get(TSysOrgEntity_.enabled), "1"));

        List<TSysOrgEntity> results = em.createQuery(cq).getResultList();

        if (results.size() != 1) {
            throw new EntityNotFoundException("Fleet is not found!");
        }
        // 启用状态(1:启用;0:停用)
        results.get(0).setEnabled("0");

        return true;
    }

}
