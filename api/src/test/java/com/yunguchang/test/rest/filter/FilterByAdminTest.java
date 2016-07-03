package com.yunguchang.test.rest.filter;

import com.yunguchang.model.common.ReturningDepotEvent;
import com.yunguchang.model.common.User;
import com.yunguchang.rest.AuthService;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Created by 禕 on 2015/11/18.
 */
public class FilterByAdminTest extends AbstractFilterRestTest {
    @Override
    public String getUserName() {
        return "00180512";
    }


    @Test
    public void testFilterApplications(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/passengers") WebTarget webTarget)
            throws Exception {

        Response response = webTarget.request().header("Authorization", "Bearer " +getLoginToken(authService)).get();;
        assertEquals(200, response.getStatus());
        User[] users = response.readEntity(User[].class);
        assert (users.length>0);


    }


    @Test
    public void testFilterBusReturn(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/depots/carsInDepot") WebTarget webTarget)
            throws Exception {

        Response response = webTarget
            .queryParam("start","2016-01-12T16:00:00.000Z")
                .queryParam("end","2016-01-13T15:59:00.000Z")
                        .request()

                        .header("Authorization", "Bearer " + getLoginToken(authService))

                .get()
                ;;
        assertEquals(200, response.getStatus());
        ReturningDepotEvent[] events = response.readEntity(ReturningDepotEvent[].class);
        assert (events.length>0);


    }
    @Test
    public void testFilterBusNoReturn(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/depots/carsNotInDepot") WebTarget webTarget)
            throws Exception {

        Response response = webTarget
                .queryParam("date", "2016-01-12T16:00:00.000Z")
                .request()

                .header("Authorization", "Bearer " + getLoginToken(authService))

                .get()
                ;;
        assertEquals(200, response.getStatus());
        ReturningDepotEvent[] events = response.readEntity(ReturningDepotEvent[].class);
        assert (events.length>0);


    }


}
