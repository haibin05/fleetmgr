package com.yunguchang.rest;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.model.common.*;
import com.yunguchang.restapp.RequireLogin;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;
import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_SPREAD_SHEET;

/**
 * Created by gongy on 2015/11/13.
 */
@Path("/cars")
@RequireLogin
public interface CarService {
    /**
     * 根据条件查询车辆列表
     *
     * @param badge     车牌(模糊查询 %badge%)
     * @param fleetName 车队(模糊查询 %fleet%)
     * @param isMoving  是否移动中。根据最近的车辆平均速度计算 True 返回速度>0的车辆
     * @param state     车辆状态
     * @return
     */
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    List<Car> listAllCars(@QueryParam("badge") String badge,
                          @QueryParam("fleet") String fleetName,
                          @QueryParam("fleetId") String fleetId,
                          @QueryParam("keyword") String keyword,
                          @QueryParam("moving") Integer isMoving,
                          @QueryParam("state") CarState state,
                          @QueryParam("lastGps")Boolean lastGps,
                          @QueryParam("gpsInstalled") Boolean gpsInstalled,
                          @QueryParam("$offset") Integer offset,
                          @QueryParam("$limit") Integer limit,
                          @Context SecurityContext securityContext);

    /**
     * 查询单辆车情况
     *
     * @param id 车辆id
     * @return
     */
    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Car.class)
    Car getCar(@PathParam("id") String id, @Context SecurityContext securityContext);

    /**
     * 查询车辆当前信息
     *
     * @param id 车辆id
     * @return
     */

    @GET
    @NoCache
    @Path("/{id}/state")
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(CarInfo.class)
    CarInfo getCarState(@PathParam("id") String id,
                        @QueryParam("now") String now,
                        @QueryParam("datetime") DateTime dateTime,
                        @Context SecurityContext securityContext);

    /**
     * 查询某辆车下的分派任务
     *
     * @param id    车辆id
     * @param start 开始时间
     * @param end   结束时间
     * @return 任务列表
     */
    @GET
    @NoCache
    @Path("/{id}/schedules")
    @Produces(APPLICATION_JSON_UTF8)
    List<Schedule> getCarSchedules(@PathParam("id") String id,
                                   @QueryParam("start") DateTime start,
                                   @QueryParam("end") DateTime end,
                                   @Context SecurityContext securityContext);

    /**
     * 查询某辆车的 所有 行驶轨迹
     *
     * @param carId 车辆id
     * @param start 开始时间
     * @param end   结束时间
     * @param response
     * @return 轨迹列表
     */
    @GET
    @NoCache
    @Path("/{carId}/paths")
    @Produces({APPLICATION_JSON_UTF8, APPLICATION_SPREAD_SHEET})//"application/json; charset=UTF-8"
    @TypeHint(List.class)
    List<PathWithAlarm> getPaths(@PathParam("carId") String carId,
                                 @QueryParam("start") DateTime start,
                                 @QueryParam("end") DateTime end,
                                 @Context SecurityContext securityContext,
                                 @Context Request req, @Context HttpServletResponse response);

    @PUT
    @Path("/{carId}/depot")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Car.class)
    Car updateFleet(@PathParam("carId") String carId, Depot depot);

    @GET
    @NoCache
    @Path("/{id}/singlePaths")
    @Produces(APPLICATION_JSON_UTF8)//"application/json; charset=UTF-8"
    List<PathWithAlarm> getSinglePaths(@PathParam("id") String id,
                                       @QueryParam("start") DateTime start,
                                       @QueryParam("end") DateTime end);

    @GET
    @NoCache
    @Path("/{id}/paths/{pathid}")
    @Produces(APPLICATION_JSON_UTF8)
    PathWithAlarm getPath(@PathParam("id") String id, @PathParam("pathid") String pathid
    );

    /**
     * 查询某辆车特定时刻的GPS信息
     *
     * @param id       车辆id
     * @param datetime 所查询的时刻
     * @return GPS信息
     */
    @GET
    @NoCache
    @Path("/{id}/gps")
    @Produces(APPLICATION_JSON_UTF8)
    GpsPoint getGpsPoint(@PathParam("id") String id,
                         @QueryParam("now") String now,
                         @QueryParam("datetime") DateTime datetime,
                         @Context SecurityContext securityContext);


    /**
     * 查询某辆车在此时间段内原始gps数据
     * @param id
     * @param start
     * @param end
     * @param securityContext
     * @return
     */
    @GET
    @NoCache
    @Path("/{id}/gpsPoints")
    @Produces({APPLICATION_JSON_UTF8, APPLICATION_SPREAD_SHEET})
    List<GpsPoint> getGpsPoints(@PathParam("id") String id,
                                @QueryParam("start") DateTime start,
                                @QueryParam("end") DateTime end,
                                @Context SecurityContext securityContext);


    @POST
    @Path ("/{id}/voiceMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    Response sendVoiceMessage(@PathParam("id") String id, VoiceMessage voiceMessage, @Context SecurityContext securityContext);
}
