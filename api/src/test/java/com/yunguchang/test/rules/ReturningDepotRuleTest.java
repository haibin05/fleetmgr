/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.yunguchang.test.rules;


import com.yunguchang.gps.GpsUtil;
import com.yunguchang.model.common.*;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.enterprise.event.Event;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(JUnit4.class)
public class ReturningDepotRuleTest extends  AbstractRuleTest{


    private Event returningDepotEventSource;

    @Before
    public void setUp(){
        super.setUp();
        returningDepotEventSource = mock(Event.class);
        basicKSession.setGlobal("returningDepotEventSource", returningDepotEventSource);
    }
    
    @Test
    public void testReturningDepot() throws InterruptedException {
        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        clock.setStartupTime(new DateTime(2015, 6, 10, 12, 0, 5).getMillis());


        Car car01 = new Car("car01");
        Depot depot01=new Depot();
        depot01.setLat(30.786726237643);
        depot01.setLng(120.57187758132);
        car01.setDepot(depot01);

        basicKSession.insert(car01);
        Car car02 = new Car("car02");
        car02.setDepot(depot01);

        basicKSession.insert(car02);

        for (int j=0;j<2;j++) {
            double lng=120.60386760642;

            for (int i = 0; i < 10; i++) {
                clock.advanceTime(15, TimeUnit.SECONDS);
                Thread.sleep(1000);
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
                System.out.println(lng + " " + GpsUtil.getDistance(30.786726237643, lng, 30.786726237643, 120.57187758132));
                lng -= 0.002;
            }

            lng=120.60386760642;

            for (int i = 0; i < 10; i++) {
                clock.advanceTime(15, TimeUnit.SECONDS);
                Thread.sleep(1000);
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
                System.out.println(lng + " " + GpsUtil.getDistance(30.786726237643, lng, 30.786726237643, 120.57187758132));
                lng -= 0.002;
            }


            clock.advanceTime(15, TimeUnit.MINUTES);
            Thread.sleep(1000);
        }
        verify(returningDepotEventSource,times(4)).fire(any(ReturningDepotEvent.class));



    }




}