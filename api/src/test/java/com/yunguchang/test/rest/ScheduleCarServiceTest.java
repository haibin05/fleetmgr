package com.yunguchang.test.rest;

import com.yunguchang.model.common.*;
import com.yunguchang.rest.ScheduleCarService;
import com.yunguchang.rest.ScheduleService;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by gongy on 8/25/2015.
 */

@RunWith(Arquillian.class)
public class ScheduleCarServiceTest extends AbstractRestTest{



//    @Test
//    public void testCreateCreateSchedule(@ArquillianResteasyResource("api") ScheduleCarService scheduleCarService) {
//        ScheduleCar scheduleCar = scheduleCarService.getScheduleCarById("002633cc41364a9ca41d7c0d0df410f7",null);
//        assertNotNull(scheduleCar);
//        String carId= scheduleCar.getCar().getId();
//        Car car=new Car();
//        car.setId("037d639c668c496386a79930c768675c");
//
//        scheduleCar.setCar(car);
//
//
//
//       // scheduleCarService.u(scheduleCar.getId(),scheduleCar);
//
//
//    }

//    @Test
//    public void testUpdateScheduleCarStatus(@ArquillianResteasyResource("api") ScheduleCarService scheduleCarService){
//        ScheduleCarStatusModel model = new ScheduleCarStatusModel();
//        model.setStatus(ScheduleStatus.AWAITING);
//        model=scheduleCarService.updateScheduleCarStatus("00429a63275e4691ae4edeac5161f1ac",model,null);
//        assertEquals(model.getStatus(),ScheduleStatus.AWAITING);
//        model.setStatus(ScheduleStatus.FINISHED);
//        model=scheduleCarService.updateScheduleCarStatus("00429a63275e4691ae4edeac5161f1ac",model,null);
//        assertEquals(model.getStatus(),ScheduleStatus.FINISHED);
//
//    }

    @Test
    public void testUpdateScheduleCarRecord(@ArquillianResteasyResource("api") ScheduleCarService scheduleCarService){
        Record record=new Record();
        record.setStart(new DateTime(2015,9,9,10,1));
        record.setStartMile(100.0);
        record=scheduleCarService.updateScheduleCarRecord("002e6abb6a68484ba130d75d4b167f70",record,null);
        System.out.println(record.getId());
        Record record2=new Record();
        record2.setEnd(new DateTime(2015, 9, 9, 11, 1));
        record2.setEndMile(120.0);
        record2=scheduleCarService.updateScheduleCarRecord("002e6abb6a68484ba130d75d4b167f70",record2,null);
        System.out.println(record2.getId());
        assertEquals(record.getId(),record2.getId());
        Record record3 = scheduleCarService.getScheduleCarRecord("002e6abb6a68484ba130d75d4b167f70", null);
        assertEquals(record2.getId(),record3.getId());
    }

    @Test
    public void testGetScheduleCarById(@ArquillianResteasyResource("api") ScheduleCarService scheduleCarService){
        ScheduleCar scheduleCar = scheduleCarService.getScheduleCarById("f08aeb8ca5d24bf19cf7aa3626f84d99", null);
        assertNotNull(scheduleCar);
        assertNotNull(scheduleCar.getCar());
        assertNotNull(scheduleCar.getDriver());
        assertNotNull(scheduleCar.getSchedule());
        assertTrue(scheduleCar.getSchedule().getApplications().length>0);
    }
}
