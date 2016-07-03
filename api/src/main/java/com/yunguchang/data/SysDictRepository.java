package com.yunguchang.data;

import com.yunguchang.model.persistence.TSysDicEntity;
import com.yunguchang.model.persistence.TSysDicEntity_;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by gongy on 2015/10/19.
 */
@Stateless
public class SysDictRepository extends GenericRepository {

    public List<TSysDicEntity> getEnumByType(String enumType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TSysDicEntity> cq = cb.createQuery(TSysDicEntity.class);
        Root<TSysDicEntity> dictRoot = cq.from(TSysDicEntity.class);
        cq.where(
                cb.and(
                        cb.equal(dictRoot.get(TSysDicEntity_.classcode), enumType),
                        cb.notEqual(dictRoot.get(TSysDicEntity_.parentid), 0)
                )
        );
        cq.orderBy(
                cb.asc(
                        dictRoot.get(TSysDicEntity_.datacode)
                )
        );
        return em.createQuery(cq).getResultList();
    }
}
