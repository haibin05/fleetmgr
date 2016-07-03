package com.yunguchang.restapp.exception;

import com.yunguchang.model.common.ErrorResponse;
import com.yunguchang.restapp.JaxRsActivator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ejb.EJBException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE;

/**
 * Created by 禕 on 2015/10/24.
 */
@Provider
public class EjbExceptionMapper implements ExceptionMapper<EJBException> {
    @Override
    public Response toResponse(EJBException exception) {
        Throwable rootCause = ExceptionUtils.getRootCause(exception);
        if (rootCause == null) {
            rootCause = exception;
        }
        Response.ResponseBuilder builder;
        if (rootCause instanceof EntityNotFoundException) {
            builder = Response.status(Response.Status.NOT_FOUND);
        } else if (rootCause instanceof NotAuthorizedException) {
            builder = Response.status(Response.Status.UNAUTHORIZED)
                    .header(WWW_AUTHENTICATE, "Bearer");
        } else if (rootCause instanceof IllegalArgumentException) {
            builder = Response.status(Response.Status.BAD_REQUEST);
        } else {
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        }

        String message = ExceptionUtils.getRootCauseMessage(exception);
        if (StringUtils.isNotEmpty(message)) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(message);
            builder.entity(errorResponse).type(JaxRsActivator.APPLICATION_JSON_UTF8);
        }
        return builder.build();
    }
}
