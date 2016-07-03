package com.yunguchang.rest;

import com.yunguchang.model.common.Driver;
import com.yunguchang.model.common.DriverInfo;
import com.yunguchang.model.common.PathWithAlarm;
import com.yunguchang.model.common.Schedule;
import com.yunguchang.restapp.RequireLogin;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/11/4.
 */
@Path("/drivers")
@RequireLogin
public interface DriverService {
    /**
     * 查询所有驾驶员
     * 查询所有指定查询条件的驾驶员
     *
     * @return
     * @status 驾驶员状态(出车, 待命, 休假, 请假)
     * @keyword 搜索关键词(驾驶员姓名, 手机号码 或者 短号码)
     * @fleetId 车队ID信息筛选
     */
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    List<Driver> listAllDrivers(@QueryParam("status") String status,
                                @QueryParam("keyword") String keyword,
                                @QueryParam("fleetId") String fleetId,
                                @QueryParam("$offset") Integer offset,
                                @QueryParam("$limit") Integer limit,
                                @Context SecurityContext securityContext);

    /**
     * 查看驾驶员的信息
     *
     * @param driverId 驾驶员ID
     * @return
     */
    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    Driver getDriverById(@PathParam("id") String driverId, @Context SecurityContext securityContext);

    /**
     * 查看当前驾驶员的当前信息
     * 如果当前驾驶员在正常上班但没有任务,就显示当前位置,
     * 如果当前驾驶员在正常上班同时有任务,就显示当前位置以及任务线路等信息,
     * 如果当前驾驶员在休假,需要显示休假信息,
     * 如果当前驾驶员在请假,需要显示请假日期信息,
     *
     * @param driverId 驾驶员ID
     * @return
     */
    @GET
    @NoCache
    @Path("/{id}/state")
    @Produces(APPLICATION_JSON_UTF8)
    DriverInfo getDriverState(@PathParam("id") String driverId,
                              @QueryParam("now") String now,
                              @QueryParam("datetime") DateTime dateTime,
                              @Context SecurityContext securityContext);

    /**
     * 查询某个驾驶员的任务信息
     *
     * @param driverId 驾驶员ID
     * @param start    查询开始时间
     * @param end      查询结束时间
     * @return
     */
    @GET
    @NoCache
    @Path("/{id}/schedules")
    @Produces(APPLICATION_JSON_UTF8)
    List<Schedule> getDriverTasks(@PathParam("id") String driverId,
                                  @QueryParam("start") DateTime start,
                                  @QueryParam("end") DateTime end, @Context SecurityContext securityContext);

    /**
     * 查询某个驾驶员的行驶轨迹
     *
     * @param driverId        驾驶员ID
     * @param start           查询开始时间
     * @param end             查询结束时间
     * @param securityContext 保密上下文
     * @return 轨迹列表
     */
    @GET
    @NoCache
    @Path("/{id}/paths")
    @Produces(APPLICATION_JSON_UTF8)
//"application/json; charset=UTF-8"
    List<PathWithAlarm> getPaths(@PathParam("id") String driverId,
                                 @QueryParam("start") DateTime start,
                                 @QueryParam("end") DateTime end,
                                 @Context SecurityContext securityContext);
}
