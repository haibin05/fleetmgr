package com.yunguchang.data;

import com.yunguchang.model.persistence.TSysRoleEntity;
import com.yunguchang.sam.PrincipalExt;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityNotFoundException;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

/**
 * Created by gongy on 9/9/2015.
 */
@Stateless
public class RoleRepository extends GenericRepository {

    public TSysRoleEntity getRole(String roleId, PrincipalExt principalExt) {
        return em.find(TSysRoleEntity.class, roleId);
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TSysRoleEntity createRole(TSysRoleEntity roleEntity, PrincipalExt principalExt) {
        if (roleEntity.getRoleid() != null) {
            roleEntity = em.merge(roleEntity);
        } else {
            em.persist(roleEntity);
        }
        return roleEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public TSysRoleEntity updateRole(String roleId, TSysRoleEntity roleEntity, PrincipalExt principalExt) {
        TSysRoleEntity persistedRoleEntity = em.find(TSysRoleEntity.class, roleId);
        if (persistedRoleEntity == null) {
            throw new EntityNotFoundException("Role is not exist");
        }
        merge(persistedRoleEntity, roleEntity);

        return persistedRoleEntity;
    }

    @TransactionAttribute(REQUIRES_NEW)
    public void deleteRole(String roleId, PrincipalExt principalExt) {
        TSysRoleEntity roleEntity = em.find(TSysRoleEntity.class, roleId);
        if (roleEntity == null) {
            throw new EntityNotFoundException("Role is not exist");
        }
        em.remove(roleEntity);
    }


    private TSysRoleEntity merge(TSysRoleEntity oldEntity, TSysRoleEntity newEntity) {
        oldEntity.setRolename(newEntity.getRolename());
        oldEntity.setDeptid(newEntity.getDeptid());
        oldEntity.setRoletype(newEntity.getRoletype());
        oldEntity.setLocked(newEntity.getLocked());
        oldEntity.setRemark(newEntity.getRemark());
        return oldEntity;
    }
}
