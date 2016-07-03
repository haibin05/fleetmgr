package com.yunguchang.rest;

import com.yunguchang.model.common.Depot;
import com.yunguchang.model.common.ReturningDepotEvent;
import com.yunguchang.restapp.RequireLogin;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/11/13.
 */
@Path("/depots")
@RequireLogin
public interface DepotService {
    /**
     * 获取所有 停车场 信息
     *
     * @return
     */
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    List<Depot> listAllDepot(@Context SecurityContext securityContext);

    /**
     * 查询车辆回厂信息
     * 返回在查询时间段中的返厂车辆和时间
     *
     * @param start 起始时间
     * @param end   截止时间
     * @return
     */
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/carsInDepot")
    List<ReturningDepotEvent> listAllCarsInDepot(@QueryParam("start") DateTime start, @QueryParam("end") DateTime end, @Context SecurityContext securityContext);

    /**
     * 查询车辆
     *
     * @param date
     * @return
     */
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/carsNotInDepot")
    List<ReturningDepotEvent> listAllCarsNotInDepot(@QueryParam("date") DateTime date, @Context SecurityContext securityContext);
}
