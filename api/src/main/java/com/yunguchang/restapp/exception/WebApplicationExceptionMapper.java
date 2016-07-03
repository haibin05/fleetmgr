package com.yunguchang.restapp.exception;

import com.yunguchang.model.common.ErrorResponse;
import com.yunguchang.restapp.JaxRsActivator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by gongy on 2015/10/22.
 */
@Provider
public class WebApplicationExceptionMapper  implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
        int status;
        if (exception.getResponse() != null) {
            status= exception.getResponse().getStatus();
        }else{
            status=Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }

        Response.ResponseBuilder builder = Response.status(status);

        String message=ExceptionUtils.getRootCauseMessage(exception);
        if (StringUtils.isNotEmpty(message)){
            ErrorResponse errorResponse=new ErrorResponse();
            errorResponse.setMessage(message);
            builder.entity(errorResponse).type(JaxRsActivator.APPLICATION_JSON_UTF8);
        }

        return builder.build();


    }
}
