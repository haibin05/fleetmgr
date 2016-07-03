package com.yunguchang.gps;

import com.yunguchang.resteasy.RestClient;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.concurrent.Semaphore;

/**
 * Created by 禕 on 2015/9/10.
 */
@ApplicationScoped
public class GpsClientProducer {


    @Inject
    @ConfigProperty(name = "gps.url", defaultValue = GpsClient.GPS_INTERNAL_URL)
    private String gpsUrl;


    @Inject
    @RestClient(poolSize = 10)
    private ResteasyClient gpsClient;


    @Inject
    @RestClient(poolSize = 50)
    private ResteasyClient baiduClient;

    @Produces
    public GpsClient getGpsClient() {
        ResteasyWebTarget rtarget = gpsClient.target(gpsUrl);
        return rtarget.proxy(GpsClient.class);
    }





    @Produces
    public BaiduClient getBaiduClient() {
        ResteasyWebTarget rtarget = baiduClient.target(BaiduClient.BAIDU_URL);
        return rtarget.proxy(BaiduClient.class);

    }

}
