package com.yunguchang.sharetome;

import com.yunguchang.gps.GpsClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Created by gongy on 2015/12/21.
 */
public class ShareTomeClientProducer {
    @Inject
    private ResteasyClient client;

    @Produces
    public ShareTomeMessageService getShareTomeClient() {
        ResteasyWebTarget rtarget = client.target(ShareTomeMessageService.SHARE_TOME_URL);
        return rtarget.proxy(ShareTomeMessageService.class);
    }
}
