package com.yunguchang.sam;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.yunguchang.cdi.Beans.getReferenceOrNull;
import static javax.security.auth.message.AuthStatus.SEND_SUCCESS;
import static javax.security.auth.message.AuthStatus.SUCCESS;

/**
 * Created by 禕 on 2015/9/6.
 */
public class FleetServerAuthModule implements ServerAuthModule {

    private CallbackHandler handler;
    private Class<?>[] supportedMessageTypes =
            new Class[]{HttpServletRequest.class, HttpServletResponse.class};

    @Override
    public void initialize(MessagePolicy requestPolicy,
                           MessagePolicy responsePolicy, CallbackHandler handler,
                           @SuppressWarnings("rawtypes") Map options) throws AuthException {
        this.handler = handler;
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo,
                                      Subject clientSubject, Subject serviceSubject) throws AuthException {


        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        HttpServletResponse respose = (HttpServletResponse) messageInfo.getResponseMessage();

        Callback[] callbacks;

        Authenticator authenticator = getReferenceOrNull(Authenticator.class);
        PrincipalExt principalExt =null;
        if (authenticator != null ) {
             principalExt = authenticator.authenticate(request, respose);
        }
        if (principalExt !=null){
            callbacks = new Callback[]{new CallerPrincipalCallback(clientSubject, principalExt),
                    new GroupPrincipalCallback(clientSubject, principalExt.getUser().getRoles())};
        } else {
            // The JASPIC protocol for "do nothing"
            callbacks = new Callback[]{new CallerPrincipalCallback(clientSubject, (Principal) null)};
        }


        try {
            handler.handle(callbacks);
        } catch (IOException | UnsupportedCallbackException e) {
            throw (AuthException) new AuthException().initCause(e);
        }

        return SUCCESS;


    }

    /**
     * A compliant implementation should return HttpServletRequest and
     * HttpServletResponse, so the delegation class {@link ServerAuthContext}
     * can choose the right SAM to delegate to. In this example there is only
     * one SAM and thus the return value actually doesn't matter here.
     */
    @Override
    public Class<?>[] getSupportedMessageTypes() {
        return supportedMessageTypes;
    }

    /**
     * WebLogic 12c calls this before Servlet is called, Geronimo v3 after,
     * JBoss EAP 6 and GlassFish 3.1.2.2 don't call this at all. WebLogic
     * (seemingly) only continues if SEND_SUCCESS is returned, Geronimo
     * completely ignores return value.
     */
    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo,
                                     Subject serviceSubject) throws AuthException {
        return SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject)
            throws AuthException {

    }
}