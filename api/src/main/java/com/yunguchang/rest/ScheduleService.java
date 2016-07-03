package com.yunguchang.rest;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.model.common.OrderByParam;
import com.yunguchang.model.common.Schedule;
import com.yunguchang.restapp.RequireLogin;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/11/2.
 */
@Path("/schedules")
@RequireLogin
public interface ScheduleService {
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    List<Schedule> listAllSchedules(
            @QueryParam("driverId") String driverId,
            @QueryParam("carId") String carId,
            @QueryParam("scheduleCarId") String scheduleCarId,
            @QueryParam("$offset") Integer offset,
            @QueryParam("$limit") Integer limit,
            @QueryParam("$orderby") OrderByParam orderByParam,
            @Context SecurityContext securityContext);

    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    Schedule lookupScheduleById(@PathParam("id") String id, @Context SecurityContext securityContext);

    /**
     * 创建调度单
     *
     * @param schedule
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    Schedule createSchedule(Schedule schedule, @Context SecurityContext securityContext);

    /**
     * 更新调度单
     *
     * @param schedule
     * @return
     */
    @POST
    @Path("/{scheduleId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    public Schedule updateSchedule(@PathParam("scheduleId") String scheduleId, Schedule schedule, @Context SecurityContext securityContext);

    /**
     * 删除调度单
     *
     * @param scheduleId
     * @return
     */
    @DELETE
    @Path("/{scheduleId}")
    @TypeHint(Boolean.class)
    public Boolean deleteSchedule(@PathParam("scheduleId") String scheduleId, @Context SecurityContext securityContext);

}
