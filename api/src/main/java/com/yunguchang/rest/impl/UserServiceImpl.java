package com.yunguchang.rest.impl;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.data.UserRepository;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.User;
import com.yunguchang.model.persistence.TSysUserEntity;
import com.yunguchang.rest.SecurityUtil;
import com.yunguchang.rest.UserService;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 8/28/2015.
 */

@RequestScoped
@Stateful
public class UserServiceImpl implements UserService {

    @Inject
    private UserRepository userRepository;

    @Override@GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public List<User> listAllUsers(
            @Context SecurityContext securityContext
    ) {
        List<TSysUserEntity> userEntities = userRepository.getAllUsers(SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<User> results=new ArrayList<>();
        for (TSysUserEntity userEntity : userEntities) {
            results.add(EntityConvert.fromEntity(userEntity));
        }
        return results;
    }




    @Override@GET
    @NoCache
    @Path("/me")
    @Produces(APPLICATION_JSON_UTF8)
    @PermitAll
    @TypeHint(User.class)

    public User lookupCurrentUser(@Context SecurityContext securityContext) {
        User user =SecurityUtil.getUserOrNull(securityContext);
        if (user!=null){
            return user;
        }else{
            throw new NotAuthorizedException("Bearer");
        }



    }

}
