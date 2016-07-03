package com.yunguchang.rest;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.model.common.User;
import com.yunguchang.restapp.RequireLogin;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/11/13.
 */
@Path("/users")
@RequireLogin
public interface UserService {
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    List<User> listAllUsers(
            @Context SecurityContext securityContext
    );

    @GET
    @NoCache
    @Path("/me")
    @Produces(APPLICATION_JSON_UTF8)
    @PermitAll
    @TypeHint(User.class)
    User lookupCurrentUser(@Context SecurityContext securityContext);
}
