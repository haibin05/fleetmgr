package com.yunguchang.restapp.exception;

import com.yunguchang.model.common.ErrorResponse;
import com.yunguchang.restapp.JaxRsActivator;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongy on 2015/11/4.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ResteasyViolationException> {


    @Override
    public Response toResponse(ResteasyViolationException exception) {
        Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
        List<ResteasyConstraintViolation> violations = exception.getViolations();
        ErrorResponse errorResponse = new ErrorResponse();;
        for (ResteasyConstraintViolation violation : violations) {

            errorResponse.addMessage(violation.getMessage());
        }


        builder.entity(errorResponse).type(JaxRsActivator.APPLICATION_JSON_UTF8);

        return builder.build();
    }
}
