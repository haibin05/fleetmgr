package com.yunguchang.rest;

import com.yunguchang.model.common.User;
import com.yunguchang.restapp.RequireLogin;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/10/30.
 */
@Path("/passengers")
@RequireLogin
public interface PassengerService {
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    List<User> listAllPassengers(
            @QueryParam("name") String name,
            @QueryParam("keyword") String keyword,
            @QueryParam("$offset") Integer offset,
            @QueryParam("$limit") Integer limit,
            @Context SecurityContext securityContext);
    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    User getPassengerById(@PathParam("id") String id, @Context SecurityContext securityContext);

}
