package com.yunguchang.test.sharetome;

import com.yunguchang.model.common.ReasonType;
import com.yunguchang.sharetome.ShareTomeMessage;
import com.yunguchang.sharetome.ShareTomeMessageResponse;
import com.yunguchang.sharetome.ShareTomeMessageService;
import com.yunguchang.sharetome.ShareTomeToken;
import com.yunguchang.utils.tools.FreeMarkerUtil;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by gongy on 9/10/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShareTomeClientTest {

    @Test
    public void testSendMessage() throws GeneralSecurityException, IOException {
        Configuration freeMarkerCfg = new Configuration();
        System.out.println(System.getProperty("user.dir"));
        freeMarkerCfg.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/../frontend/mobile/app/templates"));
        System.out.println(System.getProperty("user.dir") + "/../frontend/mobile/app/templates");
        freeMarkerCfg.setObjectWrapper(new DefaultObjectWrapper());
        // Post : [jxdlgps@jxdl.com] -> driver
        // Post : [jxdlgps@jxdl.com] -> mainUser

        ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder();
        clientBuilder.disableTrustManager();
        ResteasyClient client = clientBuilder.build();

        ResteasyWebTarget rtarget = client.target(ShareTomeMessageService.SHARE_TOME_URL);
        ShareTomeMessageService shareTomeMessageService = rtarget.proxy(ShareTomeMessageService.class);
//        ShareTomeToken token = shareTomeMessageService.getToken("jxdlgps@jxdl.com@@@jxdlgps", "123456", "xietong110_web", "xietong110_web_secret", "password");
//        ShareTomeToken token = shareTomeMessageService.getToken("haibin.wang@xietong110.com@@@000", "1qazxsw2", "xietong110_web", "xietong110_web_secret", "password");
        ShareTomeToken token = shareTomeMessageService.getToken("35151029@qq.com@@@000", "liu6431521", "xietong110_web", "xietong110_web_secret", null, "password");
        if(null == token.getAccessToken()) {
            System.out.println("发送消息失败");
            return ;
        }
        System.out.println(token.getAccessToken());
        assertNotNull(token);

        ShareTomeMessage shareTomeMessage = new ShareTomeMessage();
        shareTomeMessage.setTitle("张三 张江软件软Y2号楼");

        ShareTomeMessage.PostTarget sender = new ShareTomeMessage.PostTarget();
//        sender.setId("jxdlgps");
//        sender.setId("35151029@qq.com");
        sender.setId("haibin.wang@xietong110.com");
        sender.setNickName("嘉兴电力消息");
        ShareTomeMessage.PostTarget receiver = new ShareTomeMessage.PostTarget();
//        receiver.setId("1009269");
        receiver.setId("haibin.wang@xietong110.com");
        receiver.setNickName("陆永明");

        List<ShareTomeMessage.PostTarget> userList = new ArrayList<>();
        userList.add(sender);
        userList.add(receiver);
        // 创建消息
        shareTomeMessage.setPostTargets(userList);
        ShareTomeMessageResponse messageResponse = shareTomeMessageService.sendMessageByOpenAPI(token.getFullBearerToken(), shareTomeMessage);
        if(!messageResponse.isSuccess()) {
            System.out.println("发送消息失败");
            return ;
        }
        // 1289821410641920
        String postId = messageResponse.getData().get("postId").toString();

        // 添加回复（订单简介）
        ShareTomeMessage shareTomeAppendMessage = new ShareTomeMessage();
        ShareTomeMessage.DispatcherPostContent dispatcherContent = new ShareTomeMessage.DispatcherPostContent();
//        dispatcherContent.setStatus(ScheduleStatus.APPLY);
//        dispatcherContent.setApplyId("01104e2c0f774edd9e7e3758bacc8dfb");
//        dispatcherContent.setOrigin("上海北");
//        dispatcherContent.setStart(DateTime.now());
//        dispatcherContent.setDestination("嘉兴南");
//        dispatcherContent.setEnd(DateTime.now());
//        dispatcherContent.setPassengers(2);
//        ReasonType reasonType = ReasonType.BUSINESS;
//        dispatcherContent.setReason(reasonType);

        Map<String, Object> context = new HashMap<>();
        context.put("r", dispatcherContent);

        String content = FreeMarkerUtil.generateContent(context, "application_zh_CN.ftl");
        shareTomeAppendMessage.setContent(content);

        messageResponse = shareTomeMessageService.replyPostByOpenAPI(token.getFullBearerToken(), postId, shareTomeAppendMessage);
        messageResponse = shareTomeMessageService.replyPostByOpenAPI(token.getFullBearerToken(), postId, shareTomeAppendMessage);

        assertTrue(messageResponse.isSuccess());
        assertEquals(0, messageResponse.getErrorCode());

        String shareTomePostUrl = messageResponse.getData().get("returnUrl").toString();
        System.out.println(shareTomePostUrl);

        Long replyDate = Long.valueOf(messageResponse.getData().get("replyDate").toString());
        System.out.println(new DateTime(replyDate).toString());
    }
}
