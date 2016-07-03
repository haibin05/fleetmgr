package com.yunguchang.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by gongy on 2015/11/13.
 */
@Path("/version")
public interface VersionService {
    /**
     * 获取系统编译时的git版本号
     * @return 获取系统编译时的git版本号
     * @throws IOException
     */
    @GET
    @Path("/build")
    @Produces(MediaType.TEXT_PLAIN)
    String getBuild() throws IOException;
}
