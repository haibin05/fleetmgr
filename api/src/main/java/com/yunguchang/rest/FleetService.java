package com.yunguchang.rest;

import com.yunguchang.model.common.Fleet;
import com.yunguchang.restapp.RequireLogin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/11/13.
 */
@Path("/fleets")
@RequireLogin
public interface FleetService {
    /**
     * 查找所有车队信息
     *
     * @return
     */
    @GET
    @Produces(APPLICATION_JSON_UTF8)
    List<Fleet> listFleet(
            @QueryParam("keyword") String keyword,
            @Context SecurityContext securityContext);
}
