package com.yunguchang.test.gps;

import com.yunguchang.gps.GpsUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

/**
 * Created by 禕 on 2015/9/13.
 */

@RunWith(MockitoJUnitRunner.class)
public class GetDistanceTest {

    @Test
    public void testGetDistance() {
        double distance = GpsUtil.getDistance(30.38039048631, 120.40183151152, 30.380391320002, 120.40183042207);
        double distance2 = GpsUtil.getDistance(30.800421789245, 120.58268118412, 30.786726237643, 120.57187758132);

        assertTrue(distance < 1);
        assertTrue(distance2 > 1);

        double lng=120.63386760642;
        for (int i=0;i<30;i++){
            System.out.println(i);
            System.out.println( GpsUtil.getDistance(30.786726237643,lng , 30.786726237643, 120.57187758132));
            lng-=0.002;
        }

//        for (int i=0;i<5;i++){
//            System.out.println(i);
//            System.out.println( GpsUtil.getDistance(30.786726237643,lng , 30.786726237643, 120.57187758132));
//            lng-=0.005;
//        }

    }

    @Test
    public void testPrintDistance(){



        System.out.println(GpsUtil.getDistance(        30.769753736896,120.71272294519	,30.769916	,120.712831
        ));
        System.out.println(GpsUtil.getDistance(        30.785621020761,120.74602456572	,30.784351	,120.746948
        ));

        System.out.println(GpsUtil.getDistance(        30.785457722959,120.74599340756	,30.784351	,120.746948
        ));
        System.out.println(GpsUtil.getDistance(        30.791900067295,120.7278555459	,30.782994	,120.729933
        ));
        System.out.println(GpsUtil.getDistance(        30.785468584016,120.74621048853	,30.784351	,120.746948
        ));
        System.out.println(GpsUtil.getDistance(        30.785720984712,120.74628214343	,30.784351	,120.746948
        ));
        System.out.println(GpsUtil.getDistance(        30.665408080356,120.86389935834	,30.672946	,120.810651
        ));

        System.out.println(GpsUtil.getDistance(        30.632882896464,120.73139419375	,30.777679	,120.691907
        ));

        System.out.println(GpsUtil.getDistance(        30.786821868109,120.7048290101	,30.734939	,120.781126
        ));

        System.out.println(GpsUtil.getDistance(        30.769725981411,120.71207835323	,30.734939	,120.781126
        ));









    }

    @Test
    public void md5(){
        System.out.println( DigestUtils.md5Hex("0"));
    }
}
