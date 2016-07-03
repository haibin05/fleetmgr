package com.yunguchang.data;

import com.google.common.collect.Iterables;
import com.yunguchang.model.persistence.*;
import com.yunguchang.sam.PrincipalExt;
import com.yunguchang.utils.tools.MD5Util;
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
 * Created by gongy on 9/27/2015.
 */
@Stateless
public class UserRepository extends GenericRepository {

    public List<TSysUserEntity> getAllUsers(PrincipalExt principalExt) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysUserEntity> cq = cb.createQuery(TSysUserEntity.class);
        Root<TSysUserEntity> userRoot = cq.from(TSysUserEntity.class);

        return em.createQuery(cq).getResultList();
    }


    public String getFleetIdOfSupervisor(String userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<TSysUserEntity> userRoot = cq.from(TSysUserEntity.class);
        cq.select(userRoot.get(TSysUserEntity_.department).get(TSysOrgEntity_.orgid));
        cq.where(
                cb.equal(userRoot.get(TSysUserEntity_.userid), userId)
        );

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);
    }


    public TSysUserEntity validUserByEidAndPassword(String eid, String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysUserEntity> cq = cb.createQuery(TSysUserEntity.class);
        Root<TSysUserEntity> user = cq.from(TSysUserEntity.class);
        user.fetch(TSysUserEntity_.roles);
        cq.select(user);
        cq.where(
                cb.and(
                        cb.equal(user.get(TSysUserEntity_.userno), eid),
                        cb.or(
                                cb.isNull(user.get(TSysUserEntity_.enabled)),
                                cb.equal(user.get(TSysUserEntity_.enabled), "1")
                        ),
                        cb.equal(user.get(TSysUserEntity_.password), password)
                )
        );
        cq.orderBy(cb.asc(user.get(TSysUserEntity_.userid)));

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);

    }


    public TSysUserEntity getUserByEId(String eid) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysUserEntity> cq = cb.createQuery(TSysUserEntity.class);
        Root<TSysUserEntity> user = cq.from(TSysUserEntity.class);
        user.fetch(TSysUserEntity_.roles);
        cq.select(user);
        cq.where(
                cb.and(
                        cb.equal(user.get(TSysUserEntity_.userno), eid)

                )
        );
        cq.orderBy(cb.asc(user.get(TSysUserEntity_.userid)));

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);

    }

    public TSysUserEntity getUserById(String id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysUserEntity> cq = cb.createQuery(TSysUserEntity.class);
        Root<TSysUserEntity> user = cq.from(TSysUserEntity.class);
        user.fetch(TSysUserEntity_.roles);
        cq.select(user);
        cq.where(
                cb.and(
                        cb.equal(user.get(TSysUserEntity_.userid), id)

                )
        );
        cq.orderBy(cb.asc(user.get(TSysUserEntity_.userid)));

        return Iterables.getFirst(em.createQuery(cq).getResultList(), null);

    }

    /**
     * 更新用户角色信息
     *
     * @param userId   用户ID
     * @param roleIds  用户角色信息
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TSysUserEntity updateUserRolesWithPermission(String userId, List<String> roleIds, PrincipalExt principalExt) {
        if (userId == null || roleIds == null) {
            throw new InvalidParameterException("User info is not valid!");
        }
        TSysUserEntity userEntity = em.find(TSysUserEntity.class, userId);
        if (userEntity == null) {
            throw new EntityNotFoundException("User is not found!");
        }
        for(String roleId : roleIds) {
            TSysRoleEntity roleEntity = em.find(TSysRoleEntity.class, roleId);
            if(roleEntity == null) {
                throw new EntityNotFoundException("Role is not found!");
            }
            TSysUserRoleEntity userRoleEntity = new TSysUserRoleEntity();
            userRoleEntity.setRoleid(roleId);
            userRoleEntity.setUserid(userId);
            em.persist(userRoleEntity);
        }

        return userEntity;
    }

    /**
     * 新增用户信息
     *
     * @param userInfo 用户信息
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TSysUserEntity createUserWithPermission(TSysUserEntity userInfo, PrincipalExt principalExt) {
        if (userInfo == null) {
            throw new InvalidParameterException("User info is not valid!");
        }
        if(StringUtils.isNotBlank(userInfo.getUserid())) {
            TSysUserEntity userEntity = em.find(TSysUserEntity.class, userInfo.getUserid());
            if(userEntity != null) {
                throw new KeyAlreadyExistsException("User is already exist!");
            }
        }

        // 所属部门
        if (userInfo.getDepartment() != null && StringUtils.isNotBlank(userInfo.getDepartment().getOrgid())) {
            TSysOrgEntity department = em.find(TSysOrgEntity.class, userInfo.getDepartment().getOrgid());
            if (department == null) {
                throw new EntityNotFoundException("Department is not found!");
            }
            userInfo.setDepartment(department);
        }
        if(StringUtils.isBlank(userInfo.getPassword())) {
            userInfo.setPassword(MD5Util.MD5Encode("1"));
        }
        if(StringUtils.isBlank(userInfo.getUserid())){
            em.persist(userInfo);
        } else {
            userInfo = em.merge(userInfo);
        }
        return userInfo;
    }

    /**
     * 更新用户信息
     *
     * @param userId   用户ID
     * @param userInfo 用户信息
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TSysUserEntity updateUserWithPermission(String userId, TSysUserEntity userInfo, PrincipalExt principalExt) {
        if (userId == null || userInfo == null) {
            throw new EntityNotFoundException("User is not found!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysUserEntity> cq = cb.createQuery(TSysUserEntity.class);
        Root<TSysUserEntity> userRoot = cq.from(TSysUserEntity.class);
        cq.where(cb.equal(userRoot.get(TSysUserEntity_.userid), userId));

        List<TSysUserEntity> results = em.createQuery(cq).getResultList();
        if (results.size() != 1) {
            throw new EntityNotFoundException("User is not found!");
        }

        TSysUserEntity userEntity = results.get(0);
        // 更新内容
        // 所属部门
        if (userEntity.getDepartment() == null || !userEntity.getDepartment().getOrgid().equals(userInfo.getDepartment().getOrgid())) {
            // 原来没有部门  或者  新的部门信息和旧的部门信息不同
            CriteriaQuery<TSysOrgEntity> cqOrg = cb.createQuery(TSysOrgEntity.class);
            Root<TSysOrgEntity> orgRoot = cqOrg.from(TSysOrgEntity.class);
            cqOrg.where(cb.equal(orgRoot.get(TSysOrgEntity_.orgid), userInfo.getDepartment().getOrgid()));

            List<TSysOrgEntity> orgResults = em.createQuery(cqOrg).getResultList();
            if (orgResults.size() == 1) {
                userEntity.setDepartment(orgResults.get(0));
            } else if (userEntity.getDepartment().getOrgid() != null) {
                // fleet is not exist, return null to tell caller the data is wrong.
            }
        }

        // 姓名
        if (StringUtils.isNotEmpty(userInfo.getUsername())) {
            userEntity.setUsername(userInfo.getUsername());
        }
        // 密码
        if (StringUtils.isNotEmpty(userInfo.getPassword())) {
            userEntity.setPassword(userInfo.getPassword());
        }
        // 手机号码
        if (StringUtils.isNotEmpty(userInfo.getMobile())) {
            userEntity.setMobile(userInfo.getMobile());
        }

        return userEntity;
    }

    /**
     * 删除用户信息(逻辑删除)
     *
     * @param userId 用户ID
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean deleteUserWithPermission(String userId, PrincipalExt principalExt) {
        if (userId == null) {
            throw new EntityNotFoundException("User is not found!");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysUserEntity> cq = cb.createQuery(TSysUserEntity.class);
        Root<TSysUserEntity> userRoot = cq.from(TSysUserEntity.class);
        cq.where(
                cb.and(
                        cb.equal(userRoot.get(TSysUserEntity_.userid), userId),
                        cb.equal(userRoot.get(TSysUserEntity_.enabled), "1")
                )
        );

        List<TSysUserEntity> results = em.createQuery(cq).getResultList();
        if (results.size() != 1) {
            throw new EntityNotFoundException("User is not found!");
        }

        // 启用状态(1:启用;0:停用)
        results.get(0).setEnabled("0");

        return true;
    }

    // 获得车队 对应角色 用户  roleName【车队长，片组长】
    public List<TSysUserEntity> getFleetUser(String fleetId, UserRoleEnum userRole) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysUserEntity> cq = cb.createQuery(TSysUserEntity.class);
        Root<TSysUserEntity> userRoot = cq.from(TSysUserEntity.class);

        Fetch<TSysUserEntity, TSysRoleEntity> roleFetch = userRoot.fetch(TSysUserEntity_.roles, JoinType.LEFT);
        Join<TSysUserEntity, TSysRoleEntity> roleJoin = (Join<TSysUserEntity, TSysRoleEntity>) roleFetch;

        cq.select(userRoot);

        Predicate predicate = cb.and(
                cb.equal(userRoot.get(TSysUserEntity_.department).get(TSysOrgEntity_.orgid), fleetId),
                cb.equal(roleJoin.get(TSysRoleEntity_.rolename), userRole.name())
        );

        cq.where(predicate);
        return em.createQuery(cq).getResultList();
    }
    public List<TSysUserEntity> getOverallDispatcher() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysUserEntity> cq = cb.createQuery(TSysUserEntity.class);
        Root<TSysUserEntity> user = cq.from(TSysUserEntity.class);
        cq.select(user);
        cq.where(
                cb.and(
                        cb.equal(user.get(TSysUserEntity_.usertype), "0610")

                )
        );
        cq.orderBy(cb.desc(user.get(TSysUserEntity_.userid)));

        return em.createQuery(cq).getResultList();

    }

}
