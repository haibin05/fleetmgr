package com.yunguchang.rest;

import com.yunguchang.model.common.User;
import com.yunguchang.sam.PrincipalExt;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Created by gongy on 2015/10/5.
 */
public class SecurityUtil {




    static public User getUserOrNull(SecurityContext securityContext){
        Principal userPrincipal = securityContext.getUserPrincipal();
        if (userPrincipal instanceof PrincipalExt){
            return ((PrincipalExt)userPrincipal).getUser();
        }else{
            return null;
        }

    }


    public static String getUserIdOrNull(SecurityContext securityContext) {
        User usr = getUserOrNull(securityContext);
        if (usr==null){
            return null;
        }else{
            return  usr.getUserId();
        }

    }

    public static PrincipalExt getPrincipalExtOrNull(SecurityContext securityContext) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        if (userPrincipal instanceof PrincipalExt){
            return ((PrincipalExt)userPrincipal);
        }else{
            return null;
        }
    }
}
