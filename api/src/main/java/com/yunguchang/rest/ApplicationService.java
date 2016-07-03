package com.yunguchang.rest;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.model.common.*;
import com.yunguchang.restapp.RequireLogin;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/10/30.
 */
@Path("/applications")
@RequireLogin
public interface ApplicationService {
    /**
     * @param coordinatorUserId 申请人(申请单创建人)用户id
     * @param reasonType        从api/enums/ReasonType 获取的key
     * @param status            从api/enums/ApplyStatus 获取的key
     * @param startBefore
     * @param startAfter
     * @param endBefore
     * @param endAfter
     * @param offset
     * @param limit
     * @param orderByParam      排序参数.格式为  start [asc|desc],filed2 [asc|desc],...
     * @param securityContext
     * @return
     */
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    List<Application> listAllApplications(@QueryParam("coordinator") String coordinatorUserId,
                                          @QueryParam("reasonType") String reasonType,
                                          @QueryParam("status") String status,
                                          @QueryParam("startBefore") DateTime startBefore,
                                          @QueryParam("startAfter") DateTime startAfter,
                                          @QueryParam("endBefore") DateTime endBefore,
                                          @QueryParam("endAfter") DateTime endAfter,
                                          @QueryParam("$offset") Integer offset,
                                          @QueryParam("$limit") Integer limit,
                                          @QueryParam("$orderby") OrderByParam orderByParam,
                                          @Context SecurityContext securityContext);


    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    Application lookupApplicationById(@PathParam("id") String id,
                                      @Context SecurityContext securityContext);

    /**
     * 创建用车申请
     *
     * @param application
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Application.class)
    Application createApplication(@Valid Application application, @Context SecurityContext securityContext);

//    /**
//     * 更新用车申请
//     *
//     * @param application
//     * @return
//     */
//    @PUT
//    @Path("/{id}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(APPLICATION_JSON_UTF8)
//    @TypeHint(Application.class)
//    Application updateApplication(@PathParam("id") String id, Application application, @Context SecurityContext securityContext);
//
//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(APPLICATION_JSON_UTF8)
//    @Path("/{id}/action")
//    @TypeHint(Application.class)
//    Application processApplication(@PathParam("id") String id, ApplicationAction action, @Context SecurityContext securityContext);

    @PUT
    @Path("/{id}/cars/{carId}/rateOfDriver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    RateOfDriver updateRateOfDriver(@PathParam("id") String applicationId, @PathParam("carId") String carId, RateOfDriver rateOfDriver, @Context SecurityContext securityContext);

    @GET
    @Path("/{id}/cars/{carId}/rateOfDriver")
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    RateOfDriver getRateOfDriver(@PathParam("id") String applicationId, @PathParam("carId") String carId, @Context SecurityContext securityContext);

    @PUT
    @Path("/{id}/cars/{carId}/rateOfPassenger")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    RateOfDriver updateRateOfPassenger(@PathParam("id") String applicationId, @PathParam("carId") String carId, RateOfPassenger rateOfPassenger, @Context SecurityContext securityContext);

    @GET
    @Path("/{id}/cars/{carId}/rateOfPassenger")
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    RateOfPassenger getRateOfPassenger(@PathParam("id") String applicationId, @PathParam("carId") String carId, @Context SecurityContext securityContext);

    @PUT
    @Path("/{id}/rateOfApplication")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Application.class)
    Application updateRateOfApplication(@PathParam("id") String applicationId, RateOfApplication rateOfApplication, @Context SecurityContext securityContext);

    @GET
    @Path("/{id}/rateOfApplication")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Application.class)
    Application getRateOfApplication(@PathParam("id") String applicationId, @Context SecurityContext securityContext);


    @GET
    @Path("/{ids}/candidateCars")
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    List<Car> listAllCandidateCars(
            @PathParam("ids") String applicationIds,
            @QueryParam("keyword") String keyword,
            @QueryParam("$offset") Integer offset,
            @QueryParam("$limit") Integer limit,
            @Context SecurityContext securityContext);


    @GET
    @Path("/{ids}/cars/{carId}/candidateDrivers")
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    List<Driver> listAllCandidateDrivers(
            @PathParam("ids") String applicationIds,
            @PathParam("carId") String carId,
            @QueryParam("keyword") String keyword,
            @QueryParam("$offset") Integer offset,
            @QueryParam("$limit") Integer limit,
            @Context SecurityContext securityContext);


    @PUT
    @Path("/{id}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Application.class)
    Application updateApplicationStatus(@PathParam("id") String applicationId, ApplicationStatus status, @Context SecurityContext securityContext);

}
