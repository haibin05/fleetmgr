package com.yunguchang.sam;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;
import java.util.Collections;

/**
 * Created by 禕 on 2015/9/6.
 */
public class FleetServerAuthContext implements ServerAuthContext {

    private ServerAuthModule serverAuthModule;

    public FleetServerAuthContext(CallbackHandler handler) throws AuthException {
        serverAuthModule = new FleetServerAuthModule();
        serverAuthModule.initialize(null, null, handler,
                Collections.<String, String>emptyMap());
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo,
                                      Subject clientSubject, Subject serviceSubject) throws AuthException {
        return serverAuthModule.validateRequest(messageInfo, clientSubject,
                serviceSubject);
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo,
                                     Subject serviceSubject) throws AuthException {
        return serverAuthModule.secureResponse(messageInfo, serviceSubject);
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject)
            throws AuthException {
        serverAuthModule.cleanSubject(messageInfo, subject);
    }

}