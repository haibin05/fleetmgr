package com.yunguchang.rest;

import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.common.AlarmEvent;
import com.yunguchang.restapp.RequireLogin;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Map;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;
import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_SPREAD_SHEET;

/**
 * Created by gongy on 2015/11/13.
 */
@Path("/alarms")
@RequireLogin
public interface AlarmService {
    /**
     * 返回告警列表
     *
     * @param start 在此时之后开始的告警
     * @param end   在此时之前开始的告警
     * @param request
     * @param response
     * @return
     */
    @GET
    @NoCache
    @Produces({APPLICATION_JSON_UTF8})
    Map<String, Object> listAllAlarms(
            @QueryParam("start") DateTime start,
            @QueryParam("end") DateTime end,
            @QueryParam("alarm") Alarm alarm,
            @QueryParam("$limit") Integer limit,
            @Context SecurityContext securityContext, @Context Request request, @Context HttpServletResponse response);

    /**
     * 返回告警列表
     *
     * @param start 在此时之后开始的告警
     * @param end   在此时之前开始的告警
     * @param request
     * @param response
     * @return
     */
    @GET
    @NoCache
    @Produces({APPLICATION_JSON_UTF8, APPLICATION_SPREAD_SHEET})
    @Path("/export")
    List<AlarmEvent> exportAllAlarms(
            @QueryParam("start") DateTime start,
            @QueryParam("end") DateTime end,
            @QueryParam("alarm") Alarm alarm,
            @QueryParam("$limit") Integer limit,
            @Context SecurityContext securityContext, @Context Request request, @Context HttpServletResponse response);

    /**
     * 返回告警个数
     *
     * @param start 在此时之后开始的告警
     * @param end   在此时之前开始的告警
     * @return
     */
    @GET
    @NoCache
    @Path("/count")
    @Produces({APPLICATION_JSON_UTF8})
    int countAllAlarms(
            @QueryParam("start") DateTime start,
            @QueryParam("end") DateTime end,
            @QueryParam("alarm") Alarm alarm,
            @Context SecurityContext securityContext);

    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    AlarmEvent lookupAlarmById(@PathParam("id") String id);
}
