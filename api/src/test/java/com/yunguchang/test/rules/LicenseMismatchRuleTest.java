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
public class LicenseMismatchRuleTest extends AbstractRuleTest {


    @Test
    public void testLicenseMismatchClearByFinishSchedule() throws InterruptedException {

        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        DateTime baseTime = new DateTime(2015, 6, 10, 12, 0, 5);
        clock.setStartupTime(baseTime.getMillis());


        Car car01 = new Car("car01");
        car01.setLicenseCarType("license2Car");
        Fleet fleet01 = new Fleet();
        car01.setFleet(fleet01);
        Depot depot01 = new Depot();
        depot01.setLat(30.786726237643);
        depot01.setLng(120.57187758132);

        car01.setDepot(depot01);
        FactHandle car01Handle = basicKSession.insert(car01);
        Car car02 = new Car("car02");
        car02.setLicenseCarType("license2Car");

        car02.setFleet(fleet01);
        car02.setDepot(depot01);

        FactHandle car02Handle = basicKSession.insert(car02);

        {
            Schedule schedule1 = new Schedule();
            schedule1.setStart(baseTime);
            schedule1.setEnd(baseTime.plusMinutes(10));
            ScheduleCar scar01 = new ScheduleCar();
            scar01.setCar(car01);
            scar01.setSchedule(schedule1);

            Driver driver01 = new Driver();
            driver01.setInternalLicenseClassCode("license1");
            scar01.setDriver(driver01);

            ScheduleCar scar02 = new ScheduleCar();
            scar02.setCar(car02);
            scar02.setSchedule(schedule1);
            schedule1.setScheduleCars(new ScheduleCar[]{scar01, scar02});

            Driver driver02 = new Driver();
            driver02.setInternalLicenseClassCode("license1");
            scar02.setDriver(driver02);

            basicKSession.insert(scar01);
            basicKSession.insert(scar02);
            System.out.println(new DateTime(schedule1.getStart()));

        }

        {
            Schedule schedule1 = new Schedule();
            schedule1.setStart(baseTime.plusMinutes(20));
            schedule1.setEnd(baseTime.plusMinutes(30));
            ScheduleCar scar01 = new ScheduleCar();
            scar01.setCar(car01);
            scar01.setSchedule(schedule1);

            Driver driver01 = new Driver();
            driver01.setInternalLicenseClassCode("license2");
            scar01.setDriver(driver01);

            ScheduleCar scar02 = new ScheduleCar();
            scar02.setCar(car02);
            scar02.setSchedule(schedule1);
            schedule1.setScheduleCars(new ScheduleCar[]{scar01, scar02});

            Driver driver02 = new Driver();
            driver02.setInternalLicenseClassCode("license2");
            scar02.setDriver(driver02);

            basicKSession.insert(scar01);
            basicKSession.insert(scar02);
            System.out.println(new DateTime(schedule1.getStart()));

        }

        {
            LicenseVehicleMapping mapping = new LicenseVehicleMapping("license2", "license2Car");
            basicKSession.insert(mapping);
            // mapping.setInternalLincenseClassCode("license2","");
        }
        for (int j = 0; j < 2; j++) {


            double lng = 120.60386760642;

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

        verify(this.spiedAlarmEventSource, times(2)).fire( matchArg(Alarm.LICENSE_MISMATCH));
        verify(this.spiedClearAlarmEventSource, times(2)).fire(matchArg(Alarm.LICENSE_MISMATCH));



    }


    @Test
    public void testLicenseMismatch() throws InterruptedException {

        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        DateTime baseTime = new DateTime(2015, 6, 10, 12, 0, 5);
        clock.setStartupTime(baseTime.getMillis());


        Car car01 = new Car("car01");
        car01.setLicenseCarType("license2Car");
        Fleet fleet01 = new Fleet();
        car01.setFleet(fleet01);
        Depot depot01 = new Depot();
        depot01.setLat(30.786726237643);
        depot01.setLng(120.57187758132);


        car01.setDepot(depot01);
        FactHandle car01Handle = basicKSession.insert(car01);
        Car car02 = new Car("car02");
        car02.setLicenseCarType("license2Car");

        car02.setFleet(fleet01);
        FactHandle car02Handle = basicKSession.insert(car02);

        Schedule schedule1 = new Schedule();


        schedule1.setStart(baseTime);
        schedule1.setEnd(baseTime.plusMinutes(30));
        ScheduleCar scar01 = new ScheduleCar();
        scar01.setCar(car01);
        scar01.setSchedule(schedule1);

        Driver driver01 = new Driver();
        driver01.setInternalLicenseClassCode("license1");
        scar01.setDriver(driver01);

        ScheduleCar scar02 = new ScheduleCar();
        scar02.setCar(car02);
        scar02.setSchedule(schedule1);
        schedule1.setScheduleCars(new ScheduleCar[]{scar01, scar02});

        Driver driver02 = new Driver();
        driver02.setInternalLicenseClassCode("license1");
        scar02.setDriver(driver02);

        FactHandle scar01Handle = basicKSession.insert(scar01);
        FactHandle scar02Handle = basicKSession.insert(scar02);
        System.out.println(new DateTime(schedule1.getStart()));


        Schedule schedule2 = new Schedule();

        schedule2.setStart(baseTime.plusMinutes(20));
        schedule2.setEnd(baseTime.plusMinutes(30));
        ScheduleCar scar03 = new ScheduleCar();
        scar03.setCar(car01);
        scar03.setSchedule(schedule2);

        Driver driver03 = new Driver();
        driver03.setInternalLicenseClassCode("license2");
        scar03.setDriver(driver03);

        ScheduleCar scar04 = new ScheduleCar();
        scar04.setCar(car02);
        scar04.setSchedule(schedule2);
        schedule2.setScheduleCars(new ScheduleCar[]{scar03, scar04});

        Driver driver04 = new Driver();
        driver04.setInternalLicenseClassCode("license2");
        scar04.setDriver(driver04);

        basicKSession.insert(scar03);
        basicKSession.insert(scar04);
        System.out.println(new DateTime(schedule2.getStart()));


        {
            LicenseVehicleMapping mapping = new LicenseVehicleMapping("license2", "license2Car");
            basicKSession.insert(mapping);
            // mapping.setInternalLincenseClassCode("license2","");
        }
        for (int j = 0; j < 2; j++) {


            double lng = 120.60386760642;

            for (int i = 0; i < 4; i++) {
                clock.advanceTime(1, TimeUnit.MINUTES);
                Thread.sleep(1000);
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
                System.out.println(new DateTime(clock.getCurrentTime()));
                lng += 0.002;
            }


//            scar01.setDriver(driver03);
//            basicKSession.update(scar01Handle,scar01);
//            scar02.setDriver(driver04);
//            basicKSession.update(scar02Handle,scar02);
//
//
//            for (int i = 0; i < 10; i++) {
//                clock.advanceTime(1, TimeUnit.MINUTES);
//                Thread.sleep(1000);
//                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
//                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
//                System.out.println(new DateTime(clock.getCurrentTime()));
//                lng += 0.002;
//            }

            clock.advanceTime(10, TimeUnit.MINUTES);
            Thread.sleep(1000);

        }
        verify(this.spiedAlarmEventSource, times(1)).fire(matchArg(Alarm.LICENSE_MISMATCH));
//        verify(this.spiedClearAlarmEventSource, times(1)).fire(matchArg(Alarm.LICENSE_MISMATCH));


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