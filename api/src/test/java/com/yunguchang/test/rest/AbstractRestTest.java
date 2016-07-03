package com.yunguchang.test.rest;

import com.yunguchang.baidu.KeyPool;
import com.yunguchang.gps.BaiduClient;
import com.yunguchang.gps.GpsClient;
import com.yunguchang.gps.GpsClientProducer;
import com.yunguchang.permission.PermissionUtil;
import com.yunguchang.rest.SecurityUtil;
import com.yunguchang.rest.SyncService;
import com.yunguchang.rest.impl.FakeServiceImpl;
import com.yunguchang.rest.impl.SyncServiceImpl;
import com.yunguchang.restapp.*;
import com.yunguchang.restapp.exception.EJBAccessExceptionMapper;
import com.yunguchang.resteasy.RestClient;
import com.yunguchang.resteasy.ResteasyClientProducer;
import com.yunguchang.sharetome.*;
import com.yunguchang.test.cdi.MockAlternative;
import com.yunguchang.test.jpa.AbstractJpaTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

/**
 * Created by gongy on 2015/10/30.
 */
public class AbstractRestTest {
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return AbstractJpaTest.createDeployment()
                .addClass(AbstractRestTest.class)
                .addPackages(true,
                        "com.yunguchang.rest",
                        "com.yunguchang.service",
                        "com.yunguchang.sharetome")
                .addAsLibraries(Maven.resolver()
                        .loadPomFromFile("pom.xml")
                        .resolve("com.nimbusds:nimbus-jose-jwt",
                                "org.apache.deltaspike.core:deltaspike-core-api",
                                "org.apache.deltaspike.core:deltaspike-core-impl",
                                "org.apache.commons:commons-lang3",
                                "commons-beanutils:commons-beanutils",
                                "commons-codec:commons-codec",
                                "joda-time:joda-time",
                                "com.fasterxml.jackson.datatype:jackson-datatype-joda"
                        )
                        .withTransitivity().as(File.class))
                .addClasses(JaxRsActivator.class,
                        CustomBooleanParamConverterProvider.class,
                        CustomDatetimeParamConverterProvider.class,
                        CustomOrderbyParamConverterProvider.class,
                        ObjectMapperContextResolver.class,
                        EJBAccessExceptionMapper.class,
                        RequireLogin.class,
                        SecurityUtil.class,
                        MockProducer.class,
                        MockAlternative.class,
                        BaiduClient.class,
                        ShareTomeEvent.class,
                        PermissionUtil.class,
                        ShareTomeMessage.class,
                        MessageCreator.class,
                        ShareTomeMessageService.class,
                        ShareTomeClientProducer.class,
                        GpsClient.class,
                        KeyPool.class,
                        ResteasyClientProducer.class,
                        GpsClientProducer.class,
                        RestClient.class,
                        ShareTomeToken.class,
                        ShareTomeMessageResponse.class


                )
                .deleteClasses(FakeServiceImpl.class,
                        SyncService.class,
                        SyncServiceImpl.class)

                .addAsWebInfResource("com/yunguchang/test/mock-beans.xml", "beans.xml")
                .addAsWebInfResource("com/yunguchang/test/jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
                ;


    }
}
