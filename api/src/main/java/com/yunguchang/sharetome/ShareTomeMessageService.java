package com.yunguchang.sharetome;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by gongy on 2015/11/17.
 */
@Path("/sns")
public interface ShareTomeMessageService {
    String SHARE_TOME_URL = System.getenv("SHARE_TOME_URL") == null ? "https://app.xietong110.com" : System.getenv("SHARE_TOME_URL");
    String CALLBACK_URL = System.getenv("CALLBACK_URL") == null ? "http://xietong110.com" : System.getenv("CALLBACK_URL");
    String client_id = "feetMgrApp";
    String client_secret = "feetMgrApp_secret";
    String grant_type_password = "password";
    String grant_type_refresh_token = "refresh_token";

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/oauth/token")
    public ShareTomeToken getToken(@FormParam("username") String userName,
                                   @FormParam("password") String password,
                                   @FormParam("client_id") String clientId,
                                   @FormParam("client_secret") String clientSecret,
                                   @FormParam("refresh_token") String refresh_token,
                                   @FormParam("grant_type") String grantType);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/openapi/user/register")
    public ShareTomeMessageResponse createNewUser(@HeaderParam("Authorization") String authorization, Object userEntity);

    @POST
    @Path("/openapi/post/send")
    @Consumes(MediaType.APPLICATION_JSON)
    public ShareTomeMessageResponse sendMessageByOpenAPI(@HeaderParam("Authorization") String authorization, Object postBody);

    @POST
    @Path("/openapi/post/{postId}/replyComment")
    @Consumes(MediaType.APPLICATION_JSON)
    public ShareTomeMessageResponse replyPostByOpenAPI(@HeaderParam("Authorization") String authorization,
                                                       @PathParam("postId") String postId, Object replyContent);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/openapi/post/{postId}/member/add")
    public ShareTomeMessageResponse addPostMembers(@HeaderParam("Authorization") String authorization,
                                               @PathParam(value = "postId")String postId, List<String> userIds);
    @GET
    @Path("/openapi/app/identify/{token}")
    public SharetomeProfile getProfile(@QueryParam("access_token") String accessToken, @PathParam("token") String token);

}
