package com.yunguchang.test.rest.filter;

import com.yunguchang.model.common.*;
import com.yunguchang.rest.AuthService;
import com.yunguchang.test.rest.AbstractLoginTest;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Created by gongy on 2015/11/12.
 */

@RunWith(Arquillian.class)
abstract  public class AbstractFilterRestTest extends AbstractLoginTest {

    @Test
    public void testFilterCars(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/cars") WebTarget webTarget)
            throws Exception {

        Response response = webTarget.request().header("Authorization", "Bearer " +getLoginToken(authService)).get();;
        assertEquals(200, response.getStatus());
        Car[] cars = response.readEntity(Car[].class);
        assert (cars.length>0);


    }





    @Test
    public void testFilterAlarms(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/alarms") WebTarget webTarget)
            throws Exception {

        Response response = webTarget.request().header("Authorization", "Bearer " +getLoginToken(authService)).get();;
        assertEquals(200, response.getStatus());
        AlarmEvent[] alarms = response.readEntity(AlarmEvent[].class);
        assert (alarms.length>0);


    }

    @Test
    public void testFilterScheduleCars(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/scheduleCars") WebTarget webTarget)
            throws Exception {

        Response response = webTarget.request().header("Authorization", "Bearer " + getLoginToken(authService)).get();;
        assertEquals(200, response.getStatus());
        ScheduleCar[] scheduleCars = response.readEntity(ScheduleCar[].class);
        assert (scheduleCars.length>0);


    }


    @Test
    public void testFilterApplications(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/applications") WebTarget webTarget)
            throws Exception {

        Response response = webTarget.request().header("Authorization", "Bearer " + getLoginToken(authService)).get();;
        assertEquals(200, response.getStatus());
        Application[] applications = response.readEntity(Application[].class);
        assert (applications.length>0);


    }


    @Test
    public void testFilterSchedule(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/schedules") WebTarget webTarget)
            throws Exception {

        Response response = webTarget.request().header("Authorization", "Bearer " + getLoginToken(authService)).get();;
        assertEquals(200, response.getStatus());
        Schedule[] schedules = response.readEntity(Schedule[].class);
        assert (schedules.length>0);


    }

    @Test
    public void testFilterDriver(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/drivers") WebTarget webTarget)
            throws Exception {

        Response response = webTarget.request().header("Authorization", "Bearer " + getLoginToken(authService)).get();;
        assertEquals(200, response.getStatus());
        Schedule[] schedules = response.readEntity(Schedule[].class);
        assert (schedules.length>0);


    }


    @Test
    public void testFilterPassengers(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/passengers") WebTarget webTarget)
            throws Exception {

        Response response = webTarget.request().header("Authorization", "Bearer " + getLoginToken(authService)).get();;
        assertEquals(200, response.getStatus());
        Schedule[] schedules = response.readEntity(Schedule[].class);
        //assert (schedules.length>0);


    }

}

