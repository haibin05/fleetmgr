package com.yunguchang.data;

import com.yunguchang.model.persistence.*;
import com.yunguchang.sam.PrincipalExt;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
/**
 * Created by gongy on 9/27/2015.
 */

@Stateless
public class PassengerRepository extends GenericRepository {


    public List<TBusMainUserInfoEntity> getAllPassengersByCoordinator(String name, String keyword, Integer offset, Integer limit, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusMainUserInfoEntity> cq = cb.createQuery(TBusMainUserInfoEntity.class);
        Root<TBusMainUserInfoEntity> passengerRoot = cq.from(TBusMainUserInfoEntity.class);
        Predicate predicate = cb.isNotNull(passengerRoot.get(TBusMainUserInfoEntity_.name));
        if (name != null) {
            predicate = cb.and(
                    predicate,
                    cb.or(
                            cb.isNull(passengerRoot.get(TBusMainUserInfoEntity_.enabled)),
                            cb.equal(passengerRoot.get(TBusMainUserInfoEntity_.enabled), "1")
                    ),
                    cb.like(passengerRoot.get(TBusMainUserInfoEntity_.name), "%" + name + "%")
            );
        }
        if (keyword != null) {
            predicate = cb.and(
                    predicate,
                    cb.or(
                            cb.isNull(passengerRoot.get(TBusMainUserInfoEntity_.enabled)),
                            cb.equal(passengerRoot.get(TBusMainUserInfoEntity_.enabled), "1")
                    ),
                    cb.or(
                            cb.like(passengerRoot.get(TBusMainUserInfoEntity_.name), "%" + keyword + "%"),
                            cb.like(passengerRoot.get(TBusMainUserInfoEntity_.mobile), "%" + keyword + "%")
                    )
            );
        }
        cq.where(predicate);

        //只有mysql有效,group by后依旧可以查询全表字段
        //其他数据库不支持
        cq.groupBy(
                passengerRoot.get(TBusMainUserInfoEntity_.name),
                passengerRoot.get(TBusMainUserInfoEntity_.mobile)
        );
        cq.orderBy(cb.asc(cb.function("casttogbk", String.class,
                cb.trim(passengerRoot.get(TBusMainUserInfoEntity_.name))
        )));
        applySecurityFilter("passengers", principalExt);

        TypedQuery<TBusMainUserInfoEntity> query = em.createQuery(cq);
        if (offset != null) {
            query.setFirstResult(offset);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    public TBusMainUserInfoEntity getPassengerById(String id, PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusMainUserInfoEntity> cq = cb.createQuery(TBusMainUserInfoEntity.class);
        Root<TBusMainUserInfoEntity> passengerRoot = cq.from(TBusMainUserInfoEntity.class);
        Predicate predicate = cb.equal(passengerRoot.get(TBusMainUserInfoEntity_.uuid), (id));
        cq.where(predicate);

        applySecurityFilter("passengers", principalExt);

        List<TBusMainUserInfoEntity> mainUserInfoEntityList = em.createQuery(cq).getResultList();

        return mainUserInfoEntityList == null || mainUserInfoEntityList.size() == 0 ? null : mainUserInfoEntityList.get(0);
    }

    /**
     * 新增用户信息
     *
     * @param mainUserInfo 用户信息
     * @return
     */
    @TransactionAttribute(REQUIRES_NEW)
    public TBusMainUserInfoEntity createMainUserWithPermission(TBusMainUserInfoEntity mainUserInfo, PrincipalExt principalExt) {
        if (mainUserInfo == null) {
            throw new EntityNotFoundException("Main User is not found!");
        }

        // 所属部门
        if (mainUserInfo.getSysOrg() != null && mainUserInfo.getSysOrg().getOrgid() != null) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<TSysOrgEntity> cqOrg = cb.createQuery(TSysOrgEntity.class);
            Root<TSysOrgEntity> orgRoot = cqOrg.from(TSysOrgEntity.class);
            cqOrg.where(cb.equal(orgRoot.get(TSysOrgEntity_.orgid), mainUserInfo.getSysOrg().getOrgid()));

            List<TSysOrgEntity> orgResults = em.createQuery(cqOrg).getResultList();
            if (orgResults.size() == 1) {
                mainUserInfo.setSysOrg(orgResults.get(0));
            } else if (mainUserInfo.getSysOrg().getOrgid() != null) {
                // fleet is not exist, return null to tell caller the data is wrong.
            }
        }
        if(mainUserInfo.getUuid() == null) {
            em.persist(mainUserInfo);
        } else {
            mainUserInfo = em.merge(mainUserInfo);
        }
        return mainUserInfo;
    }

    /**
     * 取得用车负责人信息
     *
     * @param mainUserId   用车负责人ID
     * @return
     */
    public TBusMainUserInfoEntity getMainUserWithPermission(String mainUserId, PrincipalExt principalExt) {
        if (mainUserId == null) {
            return null;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusMainUserInfoEntity> cq = cb.createQuery(TBusMainUserInfoEntity.class);
        Root<TBusMainUserInfoEntity> userRoot = cq.from(TBusMainUserInfoEntity.class);
        cq.where(cb.equal(userRoot.get(TBusMainUserInfoEntity_.uuid), mainUserId));

        List<TBusMainUserInfoEntity> results = em.createQuery(cq).getResultList();
        if (results.size() != 1) {
            return null;
        }

        return results.get(0);
    }

    /**
     * 更新用车负责人信息
     *
     * @param mainUserId   用车负责人ID
     * @param mainUserInfo 用车负责人信息
     * @return
     */
    public TBusMainUserInfoEntity updateMainUserWithPermission(String mainUserId, TBusMainUserInfoEntity mainUserInfo, PrincipalExt principalExt) {
        if (mainUserId == null || mainUserInfo == null) {
            throw new EntityNotFoundException("MainUser or mainUserInfo is not correct!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusMainUserInfoEntity> cq = cb.createQuery(TBusMainUserInfoEntity.class);
        Root<TBusMainUserInfoEntity> userRoot = cq.from(TBusMainUserInfoEntity.class);
        cq.where(cb.equal(userRoot.get(TBusMainUserInfoEntity_.uuid), mainUserId));

        List<TBusMainUserInfoEntity> results = em.createQuery(cq).getResultList();
        if (results.size() != 1) {
            throw new EntityNotFoundException("Main User is not exist!");
        }

        TBusMainUserInfoEntity userEntity = results.get(0);
        // 更新内容
        userEntity.setName(mainUserInfo.getName());
        userEntity.setMobile(mainUserInfo.getMobile());

        if (mainUserInfo.getSysOrg() != null) {
            TSysOrgEntity sysOrg = em.find(TSysOrgEntity.class, mainUserInfo.getSysOrg().getOrgid());
            if (sysOrg == null) {
                throw new EntityNotFoundException("Org is not exist!");
            }
            userEntity.setSysOrg(sysOrg);
        }

        return userEntity;
    }


    /**
     * 删除用车负责人信息(逻辑删除)
     *
     * @param mainUserId 用车负责人ID
     * @return
     */
    public boolean deleteMainUserWithPermission(String mainUserId, PrincipalExt principalExt) {
        if (mainUserId == null) {
            throw new EntityNotFoundException("MainUser is not exist!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TBusMainUserInfoEntity> cq = cb.createQuery(TBusMainUserInfoEntity.class);
        Root<TBusMainUserInfoEntity> userRoot = cq.from(TBusMainUserInfoEntity.class);
        cq.where(cb.equal(userRoot.get(TBusMainUserInfoEntity_.uuid), mainUserId),
                cb.or(cb.isNull(userRoot.get(TBusMainUserInfoEntity_.enabled))
                        , cb.equal(userRoot.get(TBusMainUserInfoEntity_.enabled), "1")));
        // TODO enabled 字段在原数据库表定义中没有, 用以标识用户是否被删除.

        List<TBusMainUserInfoEntity> results = em.createQuery(cq).getResultList();
        if (results.size() != 1) {
            throw new EntityNotFoundException("MainUser is not exist!");
        }

        // 启用状态(1:启用;0:停用)
        results.get(0).setEnabled("0");

        return true;
    }

}
