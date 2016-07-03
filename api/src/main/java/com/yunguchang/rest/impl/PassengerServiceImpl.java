package com.yunguchang.rest.impl;

import com.yunguchang.data.PassengerRepository;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.User;
import com.yunguchang.model.persistence.TBusMainUserInfoEntity;
import com.yunguchang.rest.PassengerService;
import com.yunguchang.rest.SecurityUtil;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/10/26.
 */

@RequestScoped
@Stateful
public class PassengerServiceImpl implements PassengerService {
    @Inject
    private PassengerRepository passengerRepository;


    @Override
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public List<User> listAllPassengers(
            @QueryParam("name") String name,
            @QueryParam("keyword") String keyword,
            @QueryParam("$offset") Integer offset,
            @QueryParam("$limit") Integer limit,
            @Context SecurityContext securityContext) {

        List<TBusMainUserInfoEntity> passengeEntities = passengerRepository.getAllPassengersByCoordinator(name, keyword, offset, limit, SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<User> results = new ArrayList<>();
        for (TBusMainUserInfoEntity passengeEntity : passengeEntities) {
            results.add(EntityConvert.fromEntity(passengeEntity));
        }
        return results;

    }

    @Override
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public User getPassengerById(@PathParam("id") String id, @Context SecurityContext securityContext) {
        TBusMainUserInfoEntity passengeEntity = passengerRepository.getPassengerById(id, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if(passengeEntity == null) {
            return null;
        }
        return EntityConvert.fromEntity(passengeEntity);

    }

}
