package com.yunguchang.test.rest.filter;

import com.yunguchang.model.common.Application;
import com.yunguchang.model.common.Login;
import com.yunguchang.model.common.Token;
import com.yunguchang.rest.AuthService;
import com.yunguchang.test.rest.AbstractLoginTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Created by 禕 on 2015/11/18.
 */
@RunWith(Arquillian.class)
public class ApplicationPlusTest {
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return AbstractLoginTest.createDeployment();
    }




    public void testFilterApplications(
            String userId,
             AuthService authService,
            WebTarget webTarget)
            throws Exception {
        Login login = new Login();
        login.setUserName(userId);
        login.setPassword( "jx123456");
        Token token = authService.local(login);

        Response response = webTarget.request().header("Authorization", "Bearer " + token.getToken()).get();;
        assertEquals(200, response.getStatus());
        Application[] applications = response.readEntity(Application[].class);
        assert (applications.length>0);


    }
    @Test
    public void testFilterApplicationsByCoordinator(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/applications") WebTarget webTarget) throws Exception {
        testFilterApplications("00740163",authService,webTarget);
    }


    @Test
    public void testFilterApplicationsByApprover(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/applications") WebTarget webTarget) throws Exception {
        testFilterApplications("00181253",authService,webTarget);
    }

}
