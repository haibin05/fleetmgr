package com.yunguchang.rest;

import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.model.sync.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Map;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * 数据同步接口
 */
@Path("/data")
public interface SyncService {


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    Response requestDispatcher(Map<String, Object> paramMap, @Context SecurityContext securityContext) throws Exception;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    Response getCacheData(@Context SecurityContext securityContext) throws Exception;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/update/{scheduleCarId}/{carId}/car")
    Response updateScheduleCar(@PathParam("scheduleCarId") String scheduleCarId, @PathParam("carId") String newCarId, @Context SecurityContext securityContext);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/finish/{scheduleId}/{driverId}/driver")
    Response updateScheduleDriver(@PathParam("scheduleId") String scheduleId, @PathParam("driverId") String newDriverId, @Context SecurityContext securityContext);

    /**
     * 车辆调度-车辆调度总调协调（提交给总调）
     */
    @POST
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/devolve/{applyId}/{isSend}/{status}/{reason}")
    public Response devolveApply(@PathParam("applyId") String applyId, @PathParam("isSend") String isSend, @PathParam("status") String status,
                                 @PathParam("reason") String reason, @Context SecurityContext securityContext);

    /**
     * 车辆调度-车辆调度总调协调（提交给总调）
     */
    @POST
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/devolve/retreat/{applyId}")
    public Response retreatDevolveApply(@PathParam("applyId") String applyId, @Context SecurityContext securityContext);

    /**
     * 车辆调度-车辆调度调度协调（变更车队）
     */
    @POST
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/concert/{applyId}/{sendUser}/{sscd}/{isSend}")
    public Response concertApply(@PathParam("applyId") String applyId, @PathParam("sendUser") String sendUserId, @PathParam("sscd") String sscd,
                                 @PathParam("isSend") String isSend, @Context SecurityContext securityContext);

    /**
     * 车辆调度-调度退回
     */
    @POST
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/retreat/{applyId}")
    public Response retreatApply(@PathParam("applyId") String applyId, @Context SecurityContext securityContext);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/cancel/{applyId}/{cancelType}")
    Response cancelApply(@PathParam("applyId") String applyId, @PathParam("cancelType") ScheduleCancelType cancelType, String reason, @Context SecurityContext securityContext);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/finish/{scheduleCarId}/car")
    Response finishSchedule(@PathParam("scheduleCarId") String scheduleCarId, SyncRecord syncRecord, @Context SecurityContext securityContext);

    @DELETE
    @TypeHint(String.class)
    @Path("/delete/{scheduleId}")
    @StatusCodes({
            @ResponseCode(code = 204, condition = "正常删除"),
            @ResponseCode(code = 404, condition = "记录不存在")
    })
    Response deleteSchedule(@PathParam("scheduleId") String scheduleId, @Context SecurityContext securityContext);

    @DELETE
    @TypeHint(String.class)
    @Path("/cancel/{scheduleId}")
    @StatusCodes({
            @ResponseCode(code = 204, condition = "正常删除"),
            @ResponseCode(code = 404, condition = "记录不存在")
    })
    Response cancelSchedule(@PathParam("scheduleId") String scheduleId, @Context SecurityContext securityContext);

    @POST
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/ping/all")
    public Response checkSyncResult(@Context SecurityContext securityContext) throws Exception;

    @POST
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(List.class)
    @Path("/qr")
    public Response checkBackSyncResult(String param, @Context SecurityContext securityContext) throws Exception;

    /**
     * 车队信息新增
     *
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncFleet.class)
    @Path("/fleet")
    public Response insert(SyncFleet fleet, @Context SecurityContext securityContext);

    /**
     * 车队信息删除
     *
     * @param fleetId 车队id
     * @return
     */
    @DELETE
    @TypeHint(String.class)
    @Path("/fleet/{fleetId}")
    @StatusCodes({
            @ResponseCode(code = 204, condition = "正常删除"),
            @ResponseCode(code = 404, condition = "记录不存在")
    })
    public Response delete(@PathParam("fleetId") String fleetId, @Context SecurityContext securityContext);

    /**
     * 车队信息变更
     *
     * @param orgId 车队id
     * @param fleet 车队信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncFleet.class)
    @Path("/fleet/{orgId}")
    public Response update(@PathParam("orgId") String orgId, SyncFleet fleet,
                           @Context SecurityContext securityContext);

    /**
     * 更新车辆GPS信息
     *
     * @param carId  车辆ID
     * @param gpsFlg GPS 安装情况
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncCar.class)
    @Path("/cars/{carId}/{gps}")
    public Response updateGps(@PathParam("carId") String carId, @PathParam("gps") String gpsFlg, @Context SecurityContext securityContext);

    /**
     * 车辆信息新增
     *
     * @param car 车辆信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncCar.class)
    @Path("/cars")
    public Response insert(CarJJForSync car, @Context SecurityContext securityContext);

    /**
     * 车辆信息变更
     *
     * @param carGuid 车辆GUID
     * @param car     车辆信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncCar.class)
    @Path("/cars/{carGuid}")
    public Response update(@PathParam("carGuid") String carGuid, SyncCar car,
                           @Context SecurityContext securityContext);


    /**
     * 删除某车辆记录
     *
     * @param carGuid 车辆GUID
     * @return
     */
    @DELETE
    @Path("/cars/{carGuid}")
    @StatusCodes({
            @ResponseCode(code = 204, condition = "正常删除"),
            @ResponseCode(code = 404, condition = "记录不存在")
    })
    public Response deleteCar(@PathParam("carGuid") String carGuid,
                              @Context SecurityContext securityContext);

    /**
     * 驾驶员信息变更
     *
     * @param driver 驾驶员信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncDriver.class)
    @Path("/drivers")
    public Response insert(SyncDriver driver, @Context SecurityContext securityContext);

    /**
     * 驾驶员信息变更
     *
     * @param driverGuid 驾驶员GUID
     * @param driver     驾驶员信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncDriver.class)
    @Path("/drivers/{driverGuid}")
    public Response update(@PathParam("driverGuid") String driverGuid, SyncDriver driver,
                           @Context SecurityContext securityContext);

    @DELETE
    @Path("/drivers/{driverGuid}")
    @StatusCodes({
            @ResponseCode(code = 204, condition = "正常删除"),
            @ResponseCode(code = 404, condition = "记录不存在")
    })
    public Response deleteDriver(@PathParam("driverGuid") String driverGuid,
                                 @Context SecurityContext securityContext);

    /**
     * 用户角色信息变更
     *
     * @param userGuid 用户GUID
     * @param roles    用户角色信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncUser.class)
    @Path("/users/{userGuid}")
    public Response update(@PathParam("userGuid") String userGuid, List<String> roles, @Context SecurityContext securityContext);

    /**
     * 用户信息变更
     *
     * @param user     用户信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncUser.class)
    @Path("/users")
    public Response insert(SyncUser user, @Context SecurityContext securityContext);

    /**
     * 用户信息变更
     *
     * @param userGuid 用户GUID
     * @param user     用户信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncUser.class)
    @Path("/users/{userGuid}")
    public Response update(@PathParam("userGuid") String userGuid, SyncUser user,
                           @Context SecurityContext securityContext);


    /**
     * 删除用户信息
     *
     * @param userGuid 用户GUID
     * @return
     */
    @DELETE
    @Path("/users/{userGuid}")
    @StatusCodes({
            @ResponseCode(code = 204, condition = "正常删除"),
            @ResponseCode(code = 404, condition = "记录不存在")
    })
    public Response deleteUser(@PathParam("userGuid") String userGuid,
                               @Context SecurityContext securityContext);


    /**
     * 修改车辆维修状态
     *
     * @param carGuid   车辆GUID
     * @param repairing 维修状态
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncCarRepairing.class)
    @Path("/cars/{carGuid}/status")
    public Response update(@PathParam("carGuid") String carGuid, SyncCarRepairing repairing,
                           @Context SecurityContext securityContext);


    /**
     * 车辆违章信息
     *
     * @param carGuid   车辆GUID
     * @param violation 违章信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncViolation.class)
    @Path("/cars/{carGuid}/violations")
    public Response update(@PathParam("carGuid") String carGuid, SyncViolation violation,
                           @Context SecurityContext securityContext);

    /**
     * 车辆调度-提交调度申请
     *
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(ScheduleApplyInfoForSync.class)
    @Path("/apply")
    public Response insertOrUpdate(ScheduleApplyInfoForSync applyInfoForSync, @Context SecurityContext securityContext);

    /**
     * 车辆调度-提交调度申请
     *
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(ApproveSugForSync.class)
    @Path("/approve/{applyNo}")
    public Response insert(@PathParam("applyNo") String applyNo, ApproveSugForSync applyInfo, @Context SecurityContext securityContext);

    /**
     * 车辆调度-添加评价
     *
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(ApproveSugForSync.class)
    @Path("/evaluate/{applyId}/{applyNo}")
    public Response insert(@PathParam("applyId") String applyId, @PathParam("applyNo") String applyNo, SyncEvaluateInfo evaluateInfo, @Context SecurityContext securityContext);

    /**
     * 车辆调度
     *
     * @param schedule 调度单
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncSchedule.class)
    @Path("/application")
    public Response update(SyncSchedule schedule, @Context SecurityContext securityContext);

    /**
     * 驾驶员请假状态
     *
     * @param driverGuid 驾驶员id
     * @param leave      请假内容
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncLeave.class)
    @Path("/drivers/{driverGuid}/leaves")
    public Response update(@PathParam("driverGuid") String driverGuid, SyncLeave leave,
                           @Context SecurityContext securityContext);

    /**
     * 新增用车负责人信息
     *
     * @param mainUser 用车负责人信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncUser.class)
    @Path("/mainuser")
    Response create(SyncMainUser mainUser, @Context SecurityContext securityContext);

    /**
     * 更新用车负责人信息
     *
     * @param mainUserId 用车人id
     * @param mainUser   用车人信息
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(SyncSchedule.class)
    @Path("/mainuser/{mainUserId}")
    public Response update(@PathParam("mainUserId") String mainUserId, SyncMainUser mainUser,
                           @Context SecurityContext securityContext);


    /**
     * 删除用车负责人信息
     *
     * @param mainUserId 用车负责人GUID
     * @return
     */
    @DELETE
    @Path("/mainuser/{mainUserId}")
    @StatusCodes({
            @ResponseCode(code = 204, condition = "正常删除"),
            @ResponseCode(code = 404, condition = "记录不存在")
    })
    public Response deleteMainUser(@PathParam("mainUserId") String mainUserId,
                                   @Context SecurityContext securityContext);


}
