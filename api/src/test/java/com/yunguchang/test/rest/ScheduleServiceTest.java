package com.yunguchang.test.rest;

import com.yunguchang.model.common.*;
import com.yunguchang.rest.ApplicationService;
import com.yunguchang.rest.ScheduleService;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by gongy on 8/25/2015.
 */

@RunWith(Arquillian.class)
public class ScheduleServiceTest extends AbstractRestTest{



    @Test
    public void testCreateCreateSchedule(@ArquillianResteasyResource("api") ScheduleService scheduleService) {
        Schedule schedule=new Schedule();
        Application application=new Application();
        application.setId("004e112db5934730946c6ad62544e914");

        schedule.setApplications(new Application[]{application});
        ScheduleCar scheduleCar=new ScheduleCar();

        Car car=new Car();
        car.setId("011eb9b5a3784119a48fb30eb817af77");
        Driver driver=new Driver();
        driver.setId("1065392");
        scheduleCar.setCar(car);
        scheduleCar.setDriver(driver);

        schedule.setScheduleCars(new ScheduleCar[]{scheduleCar});



        assertNull(schedule.getId());
        schedule=scheduleService.createSchedule(schedule,null);
        assertNotNull(schedule.getId());
        assertEquals(schedule.getScheduleCars()[0].getCar().getId(),"011eb9b5a3784119a48fb30eb817af77");

    }




    @Test
    public void testGetSchedule(@ArquillianResteasyResource("api") ScheduleService scheduleService){
        List<Schedule> results = scheduleService.listAllSchedules(null, null, "002a45f3dbe94d4c83303864516dffb7", 20, 0, null, null);
        assertTrue(results.size()>0);
        assertNotNull(results.get(0).getSender());
        assertNotNull(results.get(0).getReceiver());
        assertTrue(results.get(0).getApplications().length > 0);
        assertNotNull(results.get(0).getApplications()[0].getCoordinator());
        scheduleService.listAllSchedules(null, null, "002a45f3dbe94d4c83303864516dffb7", 20, 0, null, null);
        assertTrue(results.size()>0);
        scheduleService.listAllSchedules(null, "d9e0797c737a446d98b43b713a9ab2a1", null,20, 0, null, null);
        assertTrue(results.size() > 0);
        scheduleService.listAllSchedules("1209321", null, null,20, 0, null, null);
        assertTrue(results.size()>0);


    }



}
