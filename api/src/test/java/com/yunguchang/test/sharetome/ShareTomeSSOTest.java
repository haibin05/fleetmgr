package com.yunguchang.test.sharetome;

import com.yunguchang.sharetome.ShareTomeMessageBroker;
import com.yunguchang.sharetome.ShareTomeMessageService;
import com.yunguchang.sharetome.ShareTomeToken;
import com.yunguchang.sharetome.SharetomeProfile;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by gongy on 2015/12/2.
 */
@RunWith(Arquillian.class)
public class ShareTomeSSOTest {
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    //2f45bb2e-0bdf-4617-bea1-5bcf74218333

    @Test
    public void testSSO(){
        ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder();
        clientBuilder.disableTrustManager();
        ResteasyClient client = clientBuilder.build();

        ResteasyWebTarget rtarget = client.target(ShareTomeMessageService.SHARE_TOME_URL);
        ShareTomeMessageService shareTomeMessageService = rtarget.proxy(ShareTomeMessageService.class);
        ShareTomeToken token = shareTomeMessageService.getToken(ShareTomeMessageBroker.robotName, ShareTomeMessageBroker.robotPwd,  "xietong110_web", "xietong110_web_secret", null, "password");
        System.out.println(token.getAccessToken());
        SharetomeProfile profile = shareTomeMessageService.getProfile(token.getAccessToken(), "2f45bb2e-0bdf-4617-bea1-5bcf74218333");
        assertTrue(profile.isSuccess());
    }

}
