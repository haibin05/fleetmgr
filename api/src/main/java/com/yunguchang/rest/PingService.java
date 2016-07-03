package com.yunguchang.rest;

import com.yunguchang.restapp.RequireLogin;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Ping Service
 * 用来检测服务是否正常运行
 */
@Path("/ping")

public interface PingService {
    String PING_PONG = "{\"ping\": \"pong\"}";

    /**
     * 返回{"ping": "pong"}
     *
     * @return {"ping": "pong"}
     */
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    String ping();

    @GET
    @NoCache
    @Path("/permitPing")
    @Produces(APPLICATION_JSON_UTF8)
    @RequireLogin
    String permitPing(@Context SecurityContext securityContext);
}
