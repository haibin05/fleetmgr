package com.yunguchang.test.rest;

import com.yunguchang.model.common.Driver;
import com.yunguchang.model.common.DriverInfo;
import com.yunguchang.model.common.User;
import com.yunguchang.rest.DriverService;
import com.yunguchang.rest.PassengerService;
import junit.framework.Assert;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by gongy on 8/25/2015.
 */

@RunWith(Arquillian.class)
public class DriverServiceTest extends AbstractRestTest{

    @Test
    public void testGetAllDrivers(@ArquillianResteasyResource("api") DriverService driverService) {

        List<Driver> drivers = driverService.listAllDrivers(null, "138", null,5,10,null);
        assertEquals(drivers.size(), 10);
        for (Driver driver : drivers) {
            System.out.printf(Arrays.deepToString(driver.getNamePinyin()));
        }

    }


    @Test
    public void testDriverState(@ArquillianResteasyResource("api") DriverService driverService) {

        DriverInfo state = driverService.getDriverState("1065496", null, new DateTime(2015, 8, 17, 9, 30), null);
        assertNotNull(state);
        assertNotNull(state.getCar());
        assertNotNull(state.getSchedule());
    }

}
