package com.yunguchang.test.jpa;

import com.yunguchang.data.ApplicationRepository;
import com.yunguchang.data.ScheduleRepository;
import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.TBusEvaluateinfoEntity;
import com.yunguchang.model.persistence.TBusScheduleRelaEntity;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
/**
 * Created by 禕 on 2015/10/11.
 */
@RunWith(Arquillian.class)
public class ScheduleTest extends AbstractJpaTest {
    @Inject
    private ApplicationRepository applicationRepository;

    @Inject
    private ScheduleRepository scheduleRepository;

    @Test
    public void testGetRate() {
        TBusEvaluateinfoEntity result = applicationRepository.getRateByByApplicationIdAndCarId("1705347", "f5bd1f5d5eea45d8b9952cdf9ac7b610", null);
        assertNotNull(result);
    }

    @Test
    public  void testGetScheduleOfCar(){
        List<TBusScheduleRelaEntity> result = scheduleRepository.getSchedulesByCarIdAndStartAndEnd("472f7e538cc64646a4120f8859816dff", null, null, null);
        assertTrue(result.size()>0);
    }

    @Test
    public void testCreateSchedule(){
//        {"applications":[{"id":"81ea4752db694053b98ee48371a941e4"},{"id":"1706266"}],"scheduleCars":[{"car":{"id":"ccb02ff5ba024c9c87b1bc04967e5f56"},"driver":{"id":"1396711"}}]}
        Schedule schedule=new Schedule();
        Application application1=new  Application();
        application1.setId("81ea4752db694053b98ee48371a941e4");
        Application application2=new  Application();
        application2.setId("1706266");
        schedule.setApplications(
                new Application[]{application1,application2}
        );

        ScheduleCar sc=new ScheduleCar();
        Car car=new Car();
        car.setId("ccb02ff5ba024c9c87b1bc04967e5f56");
        Driver driver=new Driver();
        driver.setId("1396711");
        sc.setCar(car);
        sc.setDriver(driver);
        schedule.setScheduleCars(new ScheduleCar[]{sc});
        TBusScheduleRelaEntity result = scheduleRepository.createSchedule(schedule, null);
        assertNotNull(result.getUuid());
    }
}
