package com.yunguchang.data;

import com.google.common.collect.Iterables;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.persistence.*;
import com.yunguchang.model.sync.SyncBusinessRelation;
import com.yunguchang.sam.PrincipalExt;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;


/**
 * Created by gongy on 9/27/2015.
 */
@Stateless
public class BusBusinessRelationRepository extends GenericRepository {

    @Inject
    FleetRepository fleetRepository;
    @Inject
    UserRepository userRepository;

    @TransactionAttribute(REQUIRES_NEW)
    public TSysOrgEntity setUniteDispatch(String orgId, String model, PrincipalExt principalExt) {
        TSysOrgEntity sysOrgEntity = em.find(TSysOrgEntity.class, orgId);
        if (sysOrgEntity == null || "0".equals(sysOrgEntity.getEnabled())) {
            throw new EntityExistsException("Business Department is not exists!");
        }
        sysOrgEntity.setModel(model);
        return sysOrgEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TBusBusinessRelaEntity createBusinessRelation(TBusBusinessRelaEntity businessRelation, PrincipalExt principalExt) {
        if (businessRelation == null) {
            throw new InvalidParameterException("Business Relation info is not valid!");
        }
        if (businessRelation.getUuid() != null) {
            businessRelation = em.merge(businessRelation);
        } else {
            em.persist(businessRelation);
        }
        return businessRelation;
    }

    public List<TBusBusinessRelaEntity> getBusinessRelation(String userId, String fleetId, String businessId) {
        if (userId == null && fleetId == null && businessId == null) {
            throw new InvalidParameterException("Parameter must not be all null!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusBusinessRelaEntity> criteriaQuery = cb.createQuery(TBusBusinessRelaEntity.class);
        Root<TBusBusinessRelaEntity> businessRoot = criteriaQuery.from(TBusBusinessRelaEntity.class);
        criteriaQuery.distinct(true);
        Predicate predicate = cb.conjunction();

        if (userId != null) {
            predicate = cb.and(
                    predicate,
                    cb.equal(businessRoot.get(TBusBusinessRelaEntity_.supervisor).get(TSysUserEntity_.userid), userId)
            );
        }
        if (fleetId != null) {
            predicate = cb.and(
                    predicate,
                    cb.equal(businessRoot.get(TBusBusinessRelaEntity_.fleet).get(TSysOrgEntity_.orgid), fleetId)
            );
        }
        if (businessId != null) {
            predicate = cb.and(
                    predicate,
                    cb.equal(businessRoot.get(TBusBusinessRelaEntity_.busOrg).get(TSysOrgEntity_.orgid), businessId)
            );
        }

        criteriaQuery.where(predicate);

        return em.createQuery(criteriaQuery).getResultList();
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<TBusBusinessRelaEntity> updateBusinessRelation(TBusBusinessRelaEntity businessRelation, PrincipalExt principalExt) {
        String userId = businessRelation.getSupervisor() == null ? null : businessRelation.getSupervisor().getUserid();
        String fleetId = businessRelation.getFleet() == null ? null : businessRelation.getFleet().getOrgid();
        String businessId = businessRelation.getBusOrg() == null ? null : businessRelation.getBusOrg().getOrgid();
        List<TBusBusinessRelaEntity> resultEntityList = getBusinessRelation(userId, fleetId, businessId);
        if (businessRelation.getMagmodel() != null && !resultEntityList.isEmpty()) {   // 业务关系统一调度设置        YWGXDDSZ#userId#model
            for (TBusBusinessRelaEntity entity : resultEntityList) {
                entity.setMagmodel(businessRelation.getMagmodel());
            }
        } else {                                        // 业务单位设置联系            YWDWSZLX#userId#orgId#bussnessids
            if(resultEntityList == null || resultEntityList.size() == 0) {
                resultEntityList = new ArrayList<>();
                resultEntityList.add(createBusinessRelation(businessRelation, principalExt));
            }
        }
        return resultEntityList;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public List<TBusBusinessRelaEntity> updateBusinessRelation(List<TBusBusinessRelaEntity> businessRelationList, PrincipalExt principalExt) {
        List<TBusBusinessRelaEntity> resultList = new ArrayList<>();
        for (TBusBusinessRelaEntity businessRelaEntity : businessRelationList) {
            resultList.addAll(updateBusinessRelation(businessRelaEntity, principalExt));
        }
        return resultList;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public void removeBusinessRelation(String uuid, PrincipalExt principalExt) {
        TBusBusinessRelaEntity relaEntity = em.find(TBusBusinessRelaEntity.class, uuid);
        em.remove(relaEntity);
    }

    // 获得 审核人
    public TSysUserEntity getAuditorOfPassenger(String passengerId) {
        TBusMainUserInfoEntity mainUserInfoEntity = em.find(TBusMainUserInfoEntity.class, passengerId);
        if(mainUserInfoEntity == null) {
            throw new EntityNotFoundException("Main User is not fount!");
        }
        TSysOrgEntity department = mainUserInfoEntity.getSysOrg();
        if (department == null || "0".equals(department.getEnabled())) {
            throw new EntityExistsException("Business Department is not fount!");
        }
        List<TBusBusinessRelaEntity> businessRelationList = getBusinessRelation(null, null, department.getOrgid());
        if(businessRelationList.size() != 1) {
            throw new InvalidParameterException("Business Relation mapping is error");
        }
        TBusBusinessRelaEntity businessRelaEntity = businessRelationList.get(0);
        List<TSysUserEntity> userList = userRepository.getFleetUser(businessRelaEntity.getBusOrg().getOrgid(), UserRoleEnum.用车审核人);
        if(userList.size() != 1) {
            userList = getDispatcherOfPassenger(passengerId);  // 总会有一个
        }
        return userList.get(0);
    }

    // 获得 调度员
    public List<TSysUserEntity> getDispatcherOfPassenger(String passengerId) {
        TBusMainUserInfoEntity mainUserInfoEntity = em.find(TBusMainUserInfoEntity.class, passengerId);
        if(mainUserInfoEntity == null) {
            throw new EntityNotFoundException("Main User is not fount!");
        }
        TSysOrgEntity department = mainUserInfoEntity.getSysOrg();
        if (department == null || "0".equals(department.getEnabled())) {
            throw new EntityExistsException("Business Department is not fount!");
        }
        // 业务模式-是否统一调度，1：是；0：否
        if("1".equals(department.getModel())){      // 总调
            return userRepository.getOverallDispatcher();
        }

        List<TBusBusinessRelaEntity> businessRelationList = getBusinessRelation(null, null, department.getOrgid());
        if(businessRelationList.size() != 1) {
            throw new InvalidParameterException("Business Relation mapping is error");
        }
        TBusBusinessRelaEntity businessRelaEntity = businessRelationList.get(0);
        List<TSysUserEntity> userList;
        // 管理模式：1：统一调度；2：自行管理；
        if("2".equals(businessRelaEntity.getMagmodel())) {      // 片组长  09
            userList = userRepository.getFleetUser(businessRelaEntity.getFleet().getOrgid(), UserRoleEnum.片组长);
        } else {                                                // 车队长  08
            userList = userRepository.getFleetUser(businessRelaEntity.getFleet().getOrgid(), UserRoleEnum.车队长);
        }
        // 当找不到 调度员时, 使用总调度
        if(userList == null || userList.isEmpty()) {
            userList = userRepository.getOverallDispatcher();
        }
        return userList;
    }
}
