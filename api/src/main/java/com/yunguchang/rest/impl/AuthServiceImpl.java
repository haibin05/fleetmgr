package com.yunguchang.rest.impl;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.data.DriverRepository;
import com.yunguchang.data.UserRepository;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.Login;
import com.yunguchang.model.common.Roles;
import com.yunguchang.model.common.Token;
import com.yunguchang.model.common.User;
import com.yunguchang.model.persistence.TRsDriverinfoEntity;
import com.yunguchang.model.persistence.TSysUserEntity;
import com.yunguchang.rest.AuthService;
import com.yunguchang.sam.JwtUtil;
import com.yunguchang.sharetome.ShareTomeMessageBroker;
import com.yunguchang.sharetome.ShareTomeMessageService;
import com.yunguchang.sharetome.ShareTomeToken;
import com.yunguchang.sharetome.SharetomeProfile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.math.BigDecimal;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * 登录模块
 */

@RequestScoped
@Stateful
@PermitAll
public class AuthServiceImpl implements AuthService {
    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private UserRepository userRepository;

    @Inject
    private DriverRepository driverRepository;

    @Inject
    private ShareTomeMessageService shareTomeMessageService;

    @Override
    @Path("/local")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Token.class)
    public Token local(Login login) {
        User user = null;
        if (StringUtils.isBlank(login.getUserName()) || StringUtils.isEmpty(login.getPassword())){
            throw new NotAuthorizedException("Bearer"); //this is the required challenge parameter rather than message
        }
        TSysUserEntity userEntity = userRepository.validUserByEidAndPassword(login.getUserName(), DigestUtils.md5Hex(login.getPassword()));
        if (userEntity != null) {
            user = EntityConvert.fromEntityWithRoles(userEntity);
        }
        if (user == null) {
            TRsDriverinfoEntity driverEntity = driverRepository.validDriverByMobile(login.getUserName());
            if (driverEntity != null) {
                user = EntityConvert.fromEntityWithRoles(driverEntity);
            }
        }


        if (user != null) {
            Token token = new Token();
            user.setNamePinyin(null);
            token.setToken(jwtUtil.sign(user));
            return token;
        } else {
            throw new NotAuthorizedException("Bearer"); //this is the required challenge parameter rather than message
        }


    }

    @Override
    public Token shareTome(Token login) {
        ShareTomeToken shareToken = shareTomeMessageService.getToken(ShareTomeMessageBroker.robotName, ShareTomeMessageBroker.robotPwd,
                ShareTomeMessageService.client_id, ShareTomeMessageService.client_secret, null, ShareTomeMessageService.grant_type_password);

        SharetomeProfile profile = shareTomeMessageService.getProfile(shareToken.getAccessToken(), login.getToken());

        User user = null;
        if (profile.isSuccess()) {
            TSysUserEntity userEntity = userRepository.getUserByEId(profile.getData());
            if (userEntity != null) {
                user = EntityConvert.fromEntityWithRoles(userEntity);
            }
            if (user == null) {
                String userId;
                if(profile.getData().matches("[0-9.E]*")) {
                    userId = new BigDecimal(profile.getData()).toString();
                } else {
                    userId = profile.getData();
                }
                TRsDriverinfoEntity driverEntity = driverRepository.validDriverByMobile(userId.toString());
                if (driverEntity != null) {
                    user = EntityConvert.fromEntityWithRoles(driverEntity);
                }
            }
        }


        //TODO: 上生产前删除此代码
        if (user == null) {
            user = new User();
            user.setUserId("admin");
            user.setUserName("admin");
            user.setEid("admin");
            user.setRoles(Roles.ADMIN);
            Token token = new Token();
            user.setNamePinyin(null);
            token.setToken(jwtUtil.sign(user));
        }


        if (user != null) {
            Token token = new Token();
            user.setNamePinyin(null);
            token.setToken(jwtUtil.sign(user));
            return token;
        } else {
            throw new NotAuthorizedException("Bearer"); //this is the required challenge parameter rather than message
        }
    }


}
