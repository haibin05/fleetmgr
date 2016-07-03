package com.yunguchang.restapp.exception;

import com.yunguchang.logger.Logger;
import com.yunguchang.model.common.ErrorResponse;
import com.yunguchang.restapp.JaxRsActivator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jboss.resteasy.spi.ReaderException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;




@Provider
public class ReaderExceptionMapper  implements javax.ws.rs.ext.ExceptionMapper<ReaderException> {

    @Inject
    private Logger logger;
    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(ReaderException failure) {
        if (failure.isLoggable())
            logger.error("Failed executing " + request.getMethod() + " " + request.getPathInfo(), failure);
        else logger.debug("Failed executing " + request.getMethod() + " " + request.getPathInfo(), failure);

        if (failure.getResponse() != null) {
            return failure.getResponse();
        }
        else {
            Response.ResponseBuilder builder = Response.status(failure.getErrorCode());
            String message= ExceptionUtils.getRootCauseMessage(failure);
            if (StringUtils.isNotEmpty(message)){
                ErrorResponse errorResponse=new ErrorResponse();
                errorResponse.setMessage(message);
                builder.entity(errorResponse).type(JaxRsActivator.APPLICATION_JSON_UTF8);
            }
            return builder.build();
        }


    }
}