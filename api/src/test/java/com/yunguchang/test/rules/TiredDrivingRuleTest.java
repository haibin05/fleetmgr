package com.yunguchang.test.rules;

import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.common.AlarmEvent;
import com.yunguchang.model.common.Car;
import com.yunguchang.model.common.GpsPoint;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by gongy on 9/21/2015.
 */
public class TiredDrivingRuleTest extends AbstractRuleTest {
    @Test
    public void testNoGpsStartMovingEvent() throws InterruptedException {
        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        clock.setStartupTime(new DateTime(2015, 6, 10, 12, 0, 5).getMillis());

        Car car01 = new Car("car01");
        basicKSession.insert(car01);
        Car car02 = new Car("car02");
        basicKSession.insert(car02);
        Car car03 = new Car("car03");

        basicKSession.insert(car03);
        clock.advanceTime(5, TimeUnit.SECONDS);


        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 3; i++) {
                basicKSession.insert(new GpsPoint(0, 0, i*j , car01, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
                basicKSession.insert(new GpsPoint(0, 0, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
                clock.advanceTime(15, TimeUnit.SECONDS);
                Thread.sleep(1000);
            }
            clock.advanceTime(30, TimeUnit.MINUTES);
            Thread.sleep(1000);
        }


        Collection results = getObjectsFromSessionByClassName("com.yunguchang.rules.StartMovingEvent");

        assertEquals( 2,results.size());
    }


    @Test
    public void testZeroSpeedStartMovingEvent() throws InterruptedException {
        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        clock.setStartupTime(new DateTime(2015, 6, 10, 12, 0, 5).getMillis());

        Car car01 = new Car("car01");
        basicKSession.insert(car01);
        Car car02 = new Car("car02");
        basicKSession.insert(car02);
        Car car03 = new Car("car03");
        basicKSession.insert(car03);
        clock.advanceTime(5, TimeUnit.SECONDS);
        Thread.sleep(1000);

        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 2; i++) {
                basicKSession.insert(new GpsPoint(0, 0, 0, car01, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
                basicKSession.insert(new GpsPoint(0, 0, 0, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));

                clock.advanceTime(15, TimeUnit.MINUTES);
                //Thread.sleep(1000);

            }
            basicKSession.insert(new GpsPoint(0, 0, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
            basicKSession.insert(new GpsPoint(0, 0, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));

            clock.advanceTime(20, TimeUnit.SECONDS);
            Thread.sleep(1000);
            basicKSession.insert(new GpsPoint(0, 0, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
            basicKSession.insert(new GpsPoint(0, 0, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));

            clock.advanceTime(20, TimeUnit.SECONDS);
            Thread.sleep(1000);
        }

        Collection results = getObjectsFromSessionByClassName("com.yunguchang.rules.StartMovingEvent");

        assertEquals( 2,results.size());
    }


    @Test
    public void testTiredSpeedStartMovingEvent() throws InterruptedException {
        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        clock.setStartupTime(new DateTime(2015, 6, 10, 12, 0, 5).getMillis());


        Car car01 = new Car("car01");
        basicKSession.insert(car01);
        Car car02 = new Car("car02");
        basicKSession.insert(car02);
        Car car03 = new Car("car03");
        basicKSession.insert(car03);
        clock.advanceTime(5, TimeUnit.SECONDS);
        Thread.sleep(1000);

        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 2; i++) {
                DateTime sampleTime = new DateTime(clock.getCurrentTime()).minusSeconds(5);
                System.out.println(sampleTime +" 0");
                basicKSession.insert(new GpsPoint(0, 0, 0, car01, sampleTime));
                basicKSession.insert(new GpsPoint(0, 0, 0, car02, sampleTime));

                clock.advanceTime(15, TimeUnit.MINUTES);


            }
            Thread.sleep(1000);
            for (int i = 0; i <30; i++) {
                DateTime sampleTime = new DateTime(clock.getCurrentTime()).minusSeconds(5);
                System.out.println(sampleTime+" 60");
                basicKSession.insert(new GpsPoint(0, 0, 60, car01, sampleTime));
                basicKSession.insert(new GpsPoint(0, 0, 60, car02, sampleTime));
                //clock.advanceTime(5, TimeUnit.SECONDS);
                Thread.sleep(1000);
                clock.advanceTime(10, TimeUnit.MINUTES);
            }
            Thread.sleep(1000);


            clock.advanceTime(20, TimeUnit.SECONDS);
            Thread.sleep(1000);
        }
        verify(spiedAlarmEventSource,times(4)).fire(matchArg(Alarm.TIRED));
        verify(spiedClearAlarmEventSource,times(2)).fire(matchArg(Alarm.TIRED));

        reset(spiedAlarmEventSource);
        reset(spiedClearAlarmEventSource);
        clock.advanceTime(30, TimeUnit.MINUTES);
        Thread.sleep(1000);
        verify(spiedClearAlarmEventSource,times(2)).fire(matchArg(Alarm.TIRED));
        verify(spiedAlarmEventSource,times(0)).fire(matchArg(Alarm.TIRED));

        Collection results = getObjectsFromSessionByClassName("com.yunguchang.rules.StartMovingEvent");

        assertEquals(2,results.size());
    }


}
