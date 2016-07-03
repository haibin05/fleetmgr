package com.yunguchang.test.rest;

import com.yunguchang.cdi.Beans;
import com.yunguchang.model.common.Login;
import com.yunguchang.model.common.Token;
import com.yunguchang.rest.AuthService;
import com.yunguchang.restapp.RequireLoginRequestFilter;
import com.yunguchang.test.rest.AbstractRestTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

/**
 * Created by gongy on 2015/11/19.
 */
public abstract class AbstractLoginTest {
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return AbstractRestTest.createDeployment()
                .addAsLibraries(Maven.resolver()
                        .loadPomFromFile("pom.xml")
                        .resolve("commons-beanutils:commons-beanutils")
                        .withTransitivity().as(File.class))

                .addPackages(true,"com.yunguchang.sam")
                .addClasses(
                        // MockUserRepositoryProducer.class,
                        Beans.class,
                        RequireLoginRequestFilter.class
                )
                .addAsWebInfResource("com/yunguchang/test/jboss-web.xml", "jboss-web.xml");


    }

    abstract  protected String getUserName();

    protected String getPassword(){
        return "jx123456";
    }

    protected String getLoginToken(AuthService authService){
        Login login = new Login();
        login.setUserName(getUserName());
        login.setPassword(getPassword());
        Token token = authService.local(login);
        return token.getToken();
    }
}
