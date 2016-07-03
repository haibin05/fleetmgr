package com.yunguchang.data;

import com.yunguchang.permission.PermissionUtil;
import com.yunguchang.sam.PrincipalExt;
import org.hibernate.Session;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Created by gongy on 2015/10/21.
 */
public class GenericRepository implements Serializable {

    @PersistenceContext
    protected EntityManager em;


    @Inject
    protected PermissionUtil permissionUtil;


    protected void applySecurityFilter(String filterCategory, PrincipalExt principalExt) {
        if (principalExt != null) {
            Session s = (Session) em.getDelegate();
            String theMaxDataScope = permissionUtil.getTheMaxDataScope(principalExt, filterCategory);

            String filterName = "filter_" + filterCategory + "_" + theMaxDataScope;
            if (!s.getSessionFactory().getDefinedFilterNames().contains(filterName)){
                filterName="filter_" + filterCategory + "_none" ;
            }
            if (s.getSessionFactory().getDefinedFilterNames().contains(filterName)) {
                s.enableFilter(filterName).setParameter("userId", principalExt.getName());
            }

        }
    }


}
