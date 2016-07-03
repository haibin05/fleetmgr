package com.yunguchang.restapp.exception;

import com.yunguchang.rest.SecurityUtil;

import javax.ejb.EJBAccessException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE;

/**
 * Created by gongy on 2015/10/21.
 */

@Provider
public class EJBAccessExceptionMapper implements ExceptionMapper<EJBAccessException> {
    @Context
    private SecurityContext securityContext;


    @Override
    public Response toResponse(EJBAccessException exception) {
        if (securityContext.getUserPrincipal() == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header(WWW_AUTHENTICATE, "Bearer")
                    .build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
