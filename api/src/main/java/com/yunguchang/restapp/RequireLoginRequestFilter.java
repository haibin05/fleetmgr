package com.yunguchang.restapp;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE;

@RequireLogin
@Provider
public class RequireLoginRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {
        if (requestContext.getSecurityContext() == null || requestContext.getSecurityContext().getUserPrincipal() == null) {
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .header(WWW_AUTHENTICATE, "Bearer")
                    .build());
        }
    }


}