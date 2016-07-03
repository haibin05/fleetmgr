package com.yunguchang.rest.impl;

import com.yunguchang.rest.PingService;
import com.yunguchang.restapp.RequireLogin;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;


@Stateless
@PermitAll
public class PingServiceImpl implements PingService {


    public String ping() {
        return PING_PONG;
    }

    public String permitPing(@Context SecurityContext securityContext) {
        return PING_PONG;
    }


}
