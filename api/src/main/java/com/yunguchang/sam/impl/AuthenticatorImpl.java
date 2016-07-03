package com.yunguchang.sam.impl;

import com.yunguchang.data.UserRepository;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.common.User;
import com.yunguchang.sam.Authenticator;
import com.yunguchang.sam.JwtUtil;
import com.yunguchang.sam.PrincipalExt;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by gongy on 9/7/2015.
 */
@RequestScoped
public class AuthenticatorImpl implements Authenticator {
    @Inject
    private JwtUtil jwtUtil;


    //            '????'
//            '?????'
//            '??????'
//            '???'
//            '??'
//            '??????'
//            '??'
//            '????'
//            '????'
//            '???'
//            '???'
//            '?????'
//            '?????'
//            '?????'
//            '??'
//            '?????'
//            '????'
//            '????'
//            '???_????'
//            '????'
//            '???'
//            '??'
    @Inject
    private UserRepository userRepository;
    @Inject
    private Logger logger;


    public PrincipalExt authenticate(HttpServletRequest req, HttpServletResponse resp) {

        String token = null;
        String authorization = req.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {

            token = authorization.substring("Bearer ".length());
        }

        if (token == null) {
            token = req.getParameter("access_token");
        }

        if (token == null) {
            return null;
        }


        User user = jwtUtil.getUser(token);
        if (user==null){
            logger.invalidToken(token);
            return null;
        }
        PrincipalExt principalExt = new PrincipalExt();
        return principalExt
                .setUser(user);


        //rolesList.add("users");


    }


}
