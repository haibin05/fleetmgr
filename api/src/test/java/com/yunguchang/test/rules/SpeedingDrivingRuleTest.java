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


import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.common.AlarmEvent;
import com.yunguchang.model.common.Car;
import com.yunguchang.model.common.GpsPoint;
import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kie.api.runtime.rule.AgendaFilter;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(JUnit4.class)
public class SpeedingDrivingRuleTest extends  AbstractRuleTest{


    @Test
    public void testFreeSpeedingEventNoGps() throws InterruptedException {
        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        clock.setStartupTime(new DateTime(2015, 6, 10, 12, 0, 5).getMillis());

        Car car01 = new Car("car01");
        basicKSession.insert(car01);
        Car car02 = new Car("car02");
        basicKSession.insert(car02);
        Car car03 = new Car("car03");
        basicKSession.insert(car03);

        int duration = 15;
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 2; i++) {
                DateTime dateTime = new DateTime(clock.getCurrentTime()).minusSeconds(5);
                basicKSession.insert(new GpsPoint(0, 0, 60, car01, dateTime));
                basicKSession.insert(new GpsPoint(0, 0, 60, car02, dateTime));
                System.out.println(dateTime + " " + 60);
                clock.advanceTime(duration, TimeUnit.SECONDS);
                Thread.sleep(1000);

            }
            for (int i = 0; i < 8; i++) {
                DateTime dateTime = new DateTime(clock.getCurrentTime()).minusSeconds(5);
                basicKSession.insert(new GpsPoint(0, 0, 130, car01, dateTime));
                basicKSession.insert(new GpsPoint(0, 0, 130, car02, dateTime));
                System.out.println(dateTime + " " + 130);
                clock.advanceTime(duration, TimeUnit.SECONDS);
                Thread.sleep(1000);

            }




            clock.advanceTime(4, TimeUnit.MINUTES);
            Thread.sleep(1000);
            basicKSession.insert(new GpsPoint(0, 0, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
            clock.advanceTime(30, TimeUnit.SECONDS);
            Thread.sleep(1000);
        }
        verify(spiedAlarmEventSource,times(4)).fire(matchArg(Alarm.SPEEDING));
        verify(spiedClearAlarmEventSource,times(4)).fire(matchArg(Alarm.SPEEDING));

    }


    @Test
    public void testFreeSpeedingEventNormalSpeed() throws InterruptedException {
        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        clock.setStartupTime(new DateTime(2015, 6, 10, 12, 0, 5).getMillis());

        Car car01 = new Car("car01");
        basicKSession.insert(car01);
        Car car02 = new Car("car02");
        basicKSession.insert(car02);
        Car car03 = new Car("car03");
        basicKSession.insert(car03);

        int duration = 15;
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 2; i++) {
                DateTime dateTime = new DateTime(clock.getCurrentTime()).minusSeconds(5);
                basicKSession.insert(new GpsPoint(0, 0, 60, car01, dateTime));
                basicKSession.insert(new GpsPoint(0, 0, 60, car02, dateTime));
                System.out.println(dateTime + " " + 60);
                clock.advanceTime(duration, TimeUnit.SECONDS);
                Thread.sleep(1000);

            }
            for (int i = 0; i < 8; i++) {
                DateTime dateTime = new DateTime(clock.getCurrentTime()).minusSeconds(5);
                basicKSession.insert(new GpsPoint(0, 0, 130, car01, dateTime));
                basicKSession.insert(new GpsPoint(0, 0, 130, car02, dateTime));
                System.out.println(dateTime + " " + 130);
                clock.advanceTime(duration, TimeUnit.SECONDS);
                Thread.sleep(1000);

            }

            for (int i = 0; i < 6; i++) {
                DateTime dateTime = new DateTime(clock.getCurrentTime()).minusSeconds(5);
                basicKSession.insert(new GpsPoint(0, 0, 60, car01, dateTime));
                basicKSession.insert(new GpsPoint(0, 0, 60, car02, dateTime));
                System.out.println(dateTime + " " + 60);
                clock.advanceTime(duration, TimeUnit.SECONDS);
                Thread.sleep(1000);

            }


            //verify(spiedAlarmEventSource,times(2)).fire(any(AlarmEvent.class));


            clock.advanceTime(4, TimeUnit.MINUTES);
            Thread.sleep(1000);
            basicKSession.insert(new GpsPoint(0, 0, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
            clock.advanceTime(30, TimeUnit.SECONDS);
            Thread.sleep(1000);
        }
        verify(spiedAlarmEventSource,times(4)).fire(any(AlarmEvent.class));
        verify(spiedClearAlarmEventSource,times(4)).fire(any(AlarmEvent.class));
    }

    @Override
    protected AgendaFilter getAgendaFilter() {
        return new RuleNameMatchesAgendaFilter(".*speeding.*");
    }
}