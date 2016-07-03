package com.yunguchang.rest.impl;

import com.yunguchang.rest.VersionService;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * 系统版本查询接口
 */

@RequestScoped
@Stateful
public class VersionServiceImpl implements VersionService {
    @Context
    private ServletContext application;

    @Inject
    private com.yunguchang.service.VersionServiceImpl versionService;

    @Override@GET
    @Path("/build")
    @Produces(MediaType.TEXT_PLAIN)
    public String getBuild() throws IOException {
        return versionService.getBuild(application);
    }



}
