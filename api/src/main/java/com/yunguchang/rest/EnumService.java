package com.yunguchang.rest;

import com.yunguchang.model.common.KeyValue;
import com.yunguchang.restapp.RequireLogin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/11/13.
 */
@Path("/enums")
@RequireLogin
public interface EnumService {
    @GET
    @Path("/{enumType}")
    @Produces(APPLICATION_JSON_UTF8)
    List<KeyValue> getDriverById(@PathParam("enumType") String enumType);
}
