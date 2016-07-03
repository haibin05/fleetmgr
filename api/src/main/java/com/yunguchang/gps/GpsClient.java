package com.yunguchang.gps;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Created by 禕 on 2015/9/8.
 */
public interface GpsClient {

    //external http://60.12.95.22:8667/GetLastGpsData?VehicleNo=%E6%B5%99F699KY
    //internal http://192.168.1.102:8667/GetLastGpsData?VehicleNo=%E6%B5%99F699KY

    static String GPS_INTERNAL_URL = "http://192.168.1.102:8667";
    @GET
    @Path("GetLastGpsData")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Map<String,String> getGpsData(@QueryParam("VehicleNo") String vechileNo);


    @GET
    @Path("BroadCast")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Map<String,String> broadCast(@QueryParam("VehicleNo") String vechileNo,@QueryParam("Comment") String comment);
}
