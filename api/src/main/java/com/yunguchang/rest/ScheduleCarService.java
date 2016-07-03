package com.yunguchang.rest;

import com.yunguchang.model.common.Record;
import com.yunguchang.model.common.ScheduleCar;
import com.yunguchang.model.common.ScheduleCarStatusModel;
import com.yunguchang.restapp.RequireLogin;
import org.joda.time.DateTime;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/11/2.
 */
@Path("/scheduleCars")
@RequireLogin
public interface ScheduleCarService {
    @GET
    @Produces(APPLICATION_JSON_UTF8)
    List<ScheduleCar> getScheduleCars(
            @QueryParam("start") DateTime start,
            @QueryParam("end") DateTime end,
            @Context SecurityContext securityContext);

    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    ScheduleCar getScheduleCarById(@PathParam("id") String id, @Context SecurityContext securityContext);

    @PUT
    @Path("/{id}/record")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
     Record updateScheduleCarRecord(@PathParam("id") String scheduleCarId, Record record, @Context SecurityContext securityContext);

    @GET
    @Path("/{id}/record")
    @Produces(APPLICATION_JSON_UTF8)
    Record getScheduleCarRecord(@PathParam("id") String scheduleCarId, @Context SecurityContext securityContext);
}
