package com.yunguchang.test.rules;

import com.yunguchang.model.common.Car;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.common.Schedule;
import com.yunguchang.model.common.ScheduleCar;
import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.joda.time.DateTime;
import org.junit.Test;
import org.kie.api.runtime.rule.AgendaFilter;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Created by gongy on 2015/10/2.
 */
public class SimpleRuleTest extends  AbstractRuleTest {

    @Test
    public void test() throws InterruptedException {
        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        DateTime baseTime = new DateTime(2015, 6, 10, 12, 0, 5);
        clock.setStartupTime(baseTime.getMillis());

        Car car01 = new Car("car01");
        basicKSession.insert(car01);
        Car car02 = new Car("car02");
        basicKSession.insert(car02);
        Car car03 = new Car("car03");
        basicKSession.insert(car03);

        Schedule schedule1=new Schedule();
        DateTime scheduleStart = baseTime.plusMinutes(15);
        schedule1.setStart(scheduleStart);
        schedule1.setEnd(baseTime.plusHours(2));
        ScheduleCar scar=new ScheduleCar();
        scar.setCar(car01);
        scar.setSchedule(schedule1);
        schedule1.setScheduleCars(new ScheduleCar[]{scar});

        basicKSession.insert(scar);

        int duration = 15;

        System.out.println(scheduleStart);
        for (int i=0;i<5;i++) {
            Thread.sleep(1000);
            DateTime gpsSampleTime = new DateTime(clock.getCurrentTime()).minusSeconds(5);
            basicKSession.insert(new GpsPoint(120, 30.786726237643, 60, car01, gpsSampleTime));
            //basicKSession.insert(new GpsPoint(120, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
            System.out.println(gpsSampleTime);
            clock.advanceTime(5, TimeUnit.MINUTES);
        }
        Thread.sleep(1000);


    }

    @Override
    protected AgendaFilter getAgendaFilter() {
        return new RuleNameEqualsAgendaFilter("test");
    }
}
