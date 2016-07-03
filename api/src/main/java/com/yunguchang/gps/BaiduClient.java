package com.yunguchang.gps;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by gongy on 9/10/2015.
 */
public interface BaiduClient {
    static String BAIDU_URL="http://api.map.baidu.com/";



    //http://api.map.baidu.com/direction/v1?mode=driving&origin=30.770210549816,120.71277780183&destination=30.529112145793,120.68602294001&origin_region=%E5%98%89%E5%85%B4&destination_region=%E5%98%89%E5%85%B4&output=json&ak=Qv3ObVtIt91cf06lXPGsQbE6"


    @GET
    @Path("direction/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DrivingResult driving( @QueryParam("mode") String mode,
                           @QueryParam("origin") String origin,
                           @QueryParam("destination") String destination,
                           @QueryParam("origin_region") String originRegion,
                           @QueryParam("destination_region") String destinationRegion,
                                  @QueryParam("tactics") int tactics,
                           @QueryParam("output") String output,
                           @QueryParam("ak") String ak
                           );


    //http://api.map.baidu.com/geocoder/v2/?ak=E4805d16520de693a3fe707cdc962045&location=39.983424,116.322987&output=json


    @GET
    @Path("geocoder/v2/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public GeoCoderResult geocoder( @QueryParam("location") String location,
                           @QueryParam("output") String output,
                           @QueryParam("ak") String ak
    );

    //http://api.map.baidu.com/geoconv/v1/?coords=114.21892734521,29.575429778924;114.21892734521,29.575429778924&from=1&to=5&ak=你的密钥
    @GET
    @Path("geoconv/v1/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public GeoconvResult geoconv( @QueryParam("coords") String coords,
                              @QueryParam("from") int from,
                             @QueryParam("to") int to,
                              @QueryParam("ak") String ak
    );


}
