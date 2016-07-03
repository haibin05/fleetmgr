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

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class NoScheduleRuleTest extends  AbstractRuleTest{




    
    @Test
    public void testNoScheduleClearByMeetSchedule() throws InterruptedException {

        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        DateTime baseTime = new DateTime(2015, 6, 10, 12, 0, 5);
        clock.setStartupTime(baseTime.getMillis());


        Car car01 = new Car("car01");
        Fleet fleet01=new Fleet();
        car01.setFleet(fleet01);
        Depot depot01=new Depot();
        depot01.setLat(30.800835033741);
        depot01.setLng(120.73386760642);


        car01.setDepot(depot01);
        FactHandle car01Handle = basicKSession.insert(car01);
        Car car02 = new Car("car02");

        car02.setFleet(fleet01);
        car02.setDepot(depot01);

        FactHandle car02Handle=basicKSession.insert(car02);

        {
            Schedule schedule1 = new Schedule();
            schedule1.setStart(baseTime.minusMinutes(15));
            schedule1.setEnd(baseTime.minusMinutes(5));
            ScheduleCar scar01 = new ScheduleCar();
            scar01.setCar(car01);
            scar01.setSchedule(schedule1);

            ScheduleCar scar02 = new ScheduleCar();
            scar02.setCar(car02);
            scar02.setSchedule(schedule1);
            schedule1.setScheduleCars(new ScheduleCar[]{scar01, scar02});

            basicKSession.insert(scar01);
            basicKSession.insert(scar02);
            System.out.println(new DateTime(schedule1.getStart()));

        }

//        {
//            Schedule schedule1 = new Schedule();
//            schedule1.setStart(baseTime.plusMinutes(45));
//            schedule1.setEnd(baseTime.plusMinutes(55));
//            ScheduleCar scar01 = new ScheduleCar();
//            scar01.setCar(car01);
//            scar01.setSchedule(schedule1);
//
//            ScheduleCar scar02 = new ScheduleCar();
//            scar02.setCar(car02);
//            scar02.setSchedule(schedule1);
//            schedule1.setScheduleCars(new ScheduleCar[]{scar01, scar02});
//
//            basicKSession.insert(scar01);
//            basicKSession.insert(scar02);
//            System.out.println(new DateTime(schedule1.getStart()));
//
//        }

        for (int j=0;j<2;j++) {


            double lng=120.60386760642;

            for (int i = 0; i < 20; i++) {
                clock.advanceTime(1, TimeUnit.MINUTES);
                Thread.sleep(1000);
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
                System.out.println(new DateTime(clock.getCurrentTime()));
                lng += 0.002;
            }
            clock.advanceTime(10, TimeUnit.MINUTES);
            Thread.sleep(1000);

        }
        verify(this.spiedAlarmEventSource,times(2)).fire(matchArg(Alarm.NOSCHEDULE));



    }



    @Test
    public void testNoAnyScheduleClearByNoGps() throws InterruptedException {

        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        DateTime baseTime = new DateTime(2015, 6, 10, 12, 0, 5);
        clock.setStartupTime(baseTime.getMillis());


        Car car01 = new Car("car01");
        Fleet fleet01=new Fleet();
        car01.setFleet(fleet01);
        Depot depot01=new Depot();
        depot01.setLat(30.800835033741);
        depot01.setLng(120.73386760642);


        car01.setDepot(depot01);
        FactHandle car01Handle = basicKSession.insert(car01);
        Car car02 = new Car("car02");

        car02.setFleet(fleet01);
        car02.setDepot(depot01);

        FactHandle car02Handle=basicKSession.insert(car02);


        for (int j=0;j<1;j++) {


            double lng=120.60386760642;

            for (int i = 0; i < 20; i++) {
                clock.advanceTime(15, TimeUnit.SECONDS);
                Thread.sleep(1000);
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
                System.out.println(new DateTime(clock.getCurrentTime()));
                lng += 0.002;
            }


        }

        clock.advanceTime(20, TimeUnit.MINUTES);
        Thread.sleep(1000);
        verify(this.spiedAlarmEventSource,times(2)).fire(any(AlarmEvent.class));
        verify(this.spiedClearAlarmEventSource,times(2)).fire(any(AlarmEvent.class));



    }


//    @Test
//    public void testVolitionClearByNoGps() throws InterruptedException {
//        assertNotNull(basicKSession);
//        PseudoClockScheduler clock = basicKSession.getSessionClock();
//        clock.setStartupTime(new DateTime(2015, 6, 10, 12, 0, 5).getMillis());
//
//
//        Car car01 = new Car("car01");
//        Fleet fleet01=new Fleet();
//        car01.setFleet(fleet01);
//        Depot depot01=new Depot();
//        depot01.setLat(30.786726237643);
//        depot01.setLng(120.57187758132);
//
//
//        fleet01.setDepot(depot01);
//        FactHandle car01Handle = basicKSession.insert(car01);
//        Car car02 = new Car("car02");
//
//        car02.setFleet(fleet01);
//        FactHandle car02Handle=basicKSession.insert(car02);
//
//        for (int j=0;j<2;j++) {
//            car01.setVolition(4);
//            car02.setVolition(4);
//
//            basicKSession.update(car01Handle,car01);
//            basicKSession.update(car02Handle,car02);
//
//
//            double lng=120.60386760642;
//
//            for (int i = 0; i < 10; i++) {
//                clock.advanceTime(1, TimeUnit.MINUTES);
//                Thread.sleep(1000);
//                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
//                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
//                System.out.println(lng + " " + GpsUtil.getDistance(30.786726237643, lng, 30.786726237643, 120.57187758132));
//                lng += 0.002;
//            }
//            clock.advanceTime(25, TimeUnit.MINUTES);
//            Thread.sleep(1000);
//        }
//        verify(this.spiedAlarmEventSource,times(4)).fire(any(AlarmEvent.class));
//        verify(this.spiedClearAlarmEventSource,times(4)).fire(any(AlarmEvent.class));
//
//
//
//    }


}