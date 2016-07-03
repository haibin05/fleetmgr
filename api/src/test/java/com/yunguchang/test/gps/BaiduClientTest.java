package com.yunguchang.test.gps;

import com.yunguchang.gps.*;
import com.yunguchang.resteasy.ResteasyClientProducer;
import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;
/**
 * Created by gongy on 9/10/2015.
 */
@RunWith(Arquillian.class)
public class BaiduClientTest {



    @Test
    public void testGeoCoder(BaiduClient baiduClient) {
        GeoCoderResult response= baiduClient.geocoder("39.983424,116.322987", "json", "kXXrgwW1h0H5LIYPjz1XOHWY");
        assertNotNull(response);
        assertNotNull(response.getResult().getAddressComponent().getCity());

    }

    @Test
    public void testDriving(BaiduClient baiduClient){
        DrivingResult response= baiduClient.driving("driving","30.770210549816,120.71277780183","30.529112145793,120.68602294001","嘉兴市","嘉兴市",12,"json","Qv3ObVtIt91cf06lXPGsQbE6");
        assertNotNull(response);
        assertNotNull(response.getResult().getRoutes()[0].getSteps()[0].getPath());


        DrivingResult response2 = baiduClient.driving("driving", "30.754724511425,120.5547343356","30.758880813162,120.55158437736", "嘉兴市", "嘉兴市",12, "json", "Qv3ObVtIt91cf06lXPGsQbE6");
        double d =GpsUtil.getDistance(30.754724511425,120.5547343356,30.758880813162,120.55158437736);
    }


    @Test
    public void testGeoconv(BaiduClient baiduClient){
        GeoconvResult response= baiduClient.geoconv("114.21892734521,29.575429778924;114.21892734521,29.575429778924",1,5, "Qv3ObVtIt91cf06lXPGsQbE6");
        assertNotNull(response.getResult()[0]);
    }


}
