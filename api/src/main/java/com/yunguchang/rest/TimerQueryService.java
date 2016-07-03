package com.yunguchang.rest;

import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/11/13.
 */
@Path("/timers")
public interface TimerQueryService {
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    List listAllTimers();
}
