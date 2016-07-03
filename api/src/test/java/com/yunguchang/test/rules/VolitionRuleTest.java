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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kie.api.runtime.rule.FactHandle;
import org.mockito.ArgumentCaptor;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;

@RunWith(JUnit4.class)
public class VolitionRuleTest extends  AbstractRuleTest{




    
    @Test
    public void testVolitionClearByState() throws InterruptedException {

        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        clock.setStartupTime(new DateTime(2015, 6, 10, 12, 0, 5).getMillis());


        Car car01 = new Car("car01");
        Fleet fleet01=new Fleet();
        car01.setFleet(fleet01);
        Depot depot01=new Depot();
        depot01.setLat(30.786726237643);
        depot01.setLng(120.57187758132);


        car01.setDepot(depot01);
        FactHandle car01Handle = basicKSession.insert(car01);
        Car car02 = new Car("car02");

        car02.setFleet(fleet01);
        car02.setDepot(depot01);

        FactHandle car02Handle=basicKSession.insert(car02);

        for (int j=0;j<2;j++) {
            car01.setVolition(4);
            car02.setVolition(4);

            basicKSession.update(car01Handle,car01);
            basicKSession.update(car02Handle,car02);


            double lng=120.60386760642;

            for (int i = 0; i < 10; i++) {
                clock.advanceTime(1, TimeUnit.MINUTES);
                Thread.sleep(1000);
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
                System.out.println(lng + " " + GpsUtil.getDistance(30.786726237643, lng, 30.786726237643, 120.57187758132));
                lng += 0.002;
            }
            car01.setVolition(0);
            car02.setVolition(0);
            basicKSession.update(car01Handle,car01);
            basicKSession.update(car02Handle,car02);
            clock.advanceTime(1, TimeUnit.MINUTES);
            Thread.sleep(1000);

        }
        verify(this.spiedClearAlarmEventSource,times(4)).fire(any(AlarmEvent.class));
        verify(this.spiedClearAlarmEventSource,times(4)).fire(any(AlarmEvent.class));



    }


    @Test
    public void testVolitionClearByNoGps() throws InterruptedException {
        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        clock.setStartupTime(new DateTime(2015, 6, 10, 12, 0, 5).getMillis());


        Car car01 = new Car("car01");
        Fleet fleet01=new Fleet();
        car01.setFleet(fleet01);
        Depot depot01=new Depot();
        depot01.setLat(30.786726237643);
        depot01.setLng(120.57187758132);


        car01.setDepot(depot01);
        FactHandle car01Handle = basicKSession.insert(car01);
        Car car02 = new Car("car02");

        car02.setFleet(fleet01);
        car02.setDepot(depot01);

        FactHandle car02Handle=basicKSession.insert(car02);

        for (int j=0;j<2;j++) {
            car01.setVolition(4);
            car02.setVolition(4);

            basicKSession.update(car01Handle,car01);
            basicKSession.update(car02Handle,car02);


            double lng=120.60386760642;

            for (int i = 0; i < 10; i++) {
                clock.advanceTime(1, TimeUnit.MINUTES);
                Thread.sleep(1000);
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
                System.out.println(lng + " " + GpsUtil.getDistance(30.786726237643, lng, 30.786726237643, 120.57187758132));
                lng += 0.002;
            }
            clock.advanceTime(25, TimeUnit.MINUTES);
            Thread.sleep(1000);
        }
        verify(this.spiedAlarmEventSource,times(4)).fire(any(AlarmEvent.class));
        verify(this.spiedClearAlarmEventSource,times(4)).fire(any(AlarmEvent.class));



    }


}