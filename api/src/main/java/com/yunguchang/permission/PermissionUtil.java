package com.yunguchang.permission;

import com.yunguchang.model.common.Roles;
import com.yunguchang.sam.PrincipalExt;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by gongy on 2015/10/13.
 */
@ApplicationScoped
public class PermissionUtil {

    public static final String SCOPE_ALL="all";
    public static final String SCOPE_FLEET="fleet";
    public static final String SCOPE_NONE="none";
    public static final String SCOPE_DRIVER = "driver";



    public String getTheMaxDataScope(PrincipalExt principalExt, String filterCategory){
        if (principalExt==null || principalExt.getUser()==null || StringUtils.isBlank(filterCategory)){
            return SCOPE_NONE;
        }

        List<String> roles = principalExt.getUser().getRolesAsList();




        if (roles.size()==0){
            return SCOPE_NONE;
        }

        if ( !Collections.disjoint(roles, Arrays.asList(Roles.ADMIN, Roles.MANAGER, Roles.CENTRAL_DISPATCHER))){
            return SCOPE_ALL;
        }

        if (roles.contains(Roles.SUPERVISOR)){
            return SCOPE_FLEET;
        }

        if (roles.contains(Roles.DRIVER)){
            return SCOPE_DRIVER;
        }

        if (filterCategory.equals("applications")){
            if (roles.contains(Roles.APPROVER)){
                return "approver";
            }
            if (roles.contains(Roles.COORDINATOR)){
                return "coordinator";
            }
            return "self";
        }

        if (filterCategory.equals("schedule_car")){
            if (roles.contains(Roles.APPROVER)){
                return "approver";
            }
            return "self";
        }

        if (filterCategory.equals("passengers")){
            if (roles.contains(Roles.APPROVER)){        // 用车审核人
                return "coordinator";
            }
            if (roles.contains(Roles.COORDINATOR)){     // 用车申请人，协调人
                return "coordinator";
            }
        }

        return "none";
    }
}
