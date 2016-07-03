package com.yunguchang.restapp.exception;

import com.yunguchang.model.common.ErrorResponse;
import com.yunguchang.restapp.JaxRsActivator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by 禕 on 2015/10/24.
 */
@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {
    @Override
    public Response toResponse(EntityNotFoundException exception) {
        Response.ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND);
        String message= ExceptionUtils.getRootCauseMessage(exception);
        if (StringUtils.isNotEmpty(message)){
            ErrorResponse errorResponse=new ErrorResponse();
            errorResponse.setMessage(message);
            builder.entity(errorResponse).type(JaxRsActivator.APPLICATION_JSON_UTF8);
        }
        return builder.build();
    }
}
