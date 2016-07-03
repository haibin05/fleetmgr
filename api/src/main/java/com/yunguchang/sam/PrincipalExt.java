package com.yunguchang.sam;


import com.yunguchang.model.common.User;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by gongy on 2015/11/4.
 */
public class PrincipalExt implements java.security.Principal {


    private User user;


    @Override
    public String getName() {
        return user.getUserId();
    }

    public User getUser() {
        return user;
    }

    public PrincipalExt setUser(User user) {
        this.user = user;
        return this;
    }

    public boolean isUserInRoles(String role, String... roles) {
        return user.getRolesAsList().contains(role) || !Collections.disjoint(user.getRolesAsList(), Arrays.asList(roles));
    }

    public String getUserIdOrNull() {
        return user != null ? user.getUserId() : null;
    }


}
