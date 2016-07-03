package com.yunguchang.test.rest;

import com.yunguchang.cdi.Beans;
import com.yunguchang.model.common.Login;
import com.yunguchang.model.common.Token;
import com.yunguchang.rest.AuthService;
import com.yunguchang.rest.DriverService;
import com.yunguchang.rest.PingService;
import com.yunguchang.restapp.RequireLoginRequestFilter;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by gongy on 9/7/2015.
 */
@RunWith(Arquillian.class)
public class AuthServiceTest {
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return AbstractRestTest.createDeployment()
                .addAsLibraries(Maven.resolver()
                        .loadPomFromFile("pom.xml")
                        .resolve("org.mockito:mockito-all", "commons-beanutils:commons-beanutils")
                        .withTransitivity().as(File.class))
                .addPackages(true, "com.yunguchang.sam")
                .addClasses(
                            // MockUserRepositoryProducer.class,
                        Beans.class,
                        RequireLoginRequestFilter.class
                        )
                .addAsWebInfResource("com/yunguchang/test/jboss-web.xml", "jboss-web.xml");


    }


    @Test
    public void testPermitPing(
            @ArquillianResteasyResource("api") AuthService authService,
            @ArquillianResteasyResource("api/ping/permitPing") WebTarget webTarget)
            throws Exception {
        Login login = new Login();
        login.setUserName("00180671");
        login.setPassword("jx123456");
        Token token = authService.local(login);
        Response response = webTarget.request().header("Authorization", "Bearer " + token.getToken()).get();
        ;
        assertEquals(200, response.getStatus());

    }


    @Test(expected = NotAuthorizedException.class)
    public void testPermitPingWithoutPermission(
            @ArquillianResteasyResource("api") PingService pingRESTService)
            throws Exception {
        pingRESTService.permitPing(null);
    }


    @Test(expected = NotAuthorizedException.class)
    public void testGetDriversWithoutPermission(
            @ArquillianResteasyResource("api") DriverService driverService)
            throws Exception {
        driverService.listAllDrivers(null,null,null,null,null,null);
    }

//    @Test
//    @Header(name = "Authorization", value = "Bearer TOKEN_STRING")
//    public void testGetLoginUser(
//            @ArquillianResteasyResource("api") SamService samService
//           )
//            throws Exception {
//        String response = samService.getLoginUser(null);
//        assertEquals(response,"mockuser");
//    }

}
