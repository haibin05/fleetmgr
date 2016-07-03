package com.yunguchang.rest.impl;

import com.yunguchang.cache.CacheKeyName;
import com.yunguchang.cache.StaticKeyGenerator;
import com.yunguchang.data.FleetRepository;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.Fleet;
import com.yunguchang.model.persistence.TSysOrgEntity;
import com.yunguchang.rest.FleetService;
import com.yunguchang.rest.SecurityUtil;

import javax.cache.annotation.CacheResult;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 9/1/2015.
 */

@RequestScoped
@Stateful
public class FleetServiceImpl implements FleetService {

    @Inject
    private FleetRepository fleetRepository;

    @Override@GET
    @Produces(APPLICATION_JSON_UTF8)
    public List<Fleet> listFleet(
            @QueryParam("keyword") String keyword,
            @Context SecurityContext securityContext) {
        List<TSysOrgEntity> results = fleetRepository.listFleet(keyword, SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<Fleet> fleetList = new ArrayList<>();
        for (TSysOrgEntity result : results) {
            fleetList.add(EntityConvert.fromEntity(result));
        }
        return fleetList;
    }

}
