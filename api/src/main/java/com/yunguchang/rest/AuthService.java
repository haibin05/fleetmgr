package com.yunguchang.rest;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.model.common.Login;
import com.yunguchang.model.common.Token;
import com.yunguchang.restapp.RequireLogin;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/11/10.
 */
@Path("/auth")
public interface AuthService {
    /**
     * 本地登录模块。使用用户名密码登录
     *
     * @param login 登录信息
     * @return
     */
    @Path("/local")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Token.class)
    Token local(Login login);

    @Path("/shareTome")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Token.class)
    Token shareTome(Token login);
}
