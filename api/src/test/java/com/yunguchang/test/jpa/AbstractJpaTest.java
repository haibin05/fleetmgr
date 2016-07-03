package com.yunguchang.test.jpa;

import com.yunguchang.permission.PermissionUtil;
import com.yunguchang.sam.JwtUtil;
import com.yunguchang.sam.PrincipalExt;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

/**
 * Created by 禕 on 2015/10/11.
 */

public class AbstractJpaTest {
    @Deployment
    public static WebArchive createDeployment() {
        return
                ShrinkWrap.create(WebArchive.class)
                        .addAsLibraries(Maven.resolver()
                                .loadPomFromFile("pom.xml")
                                .resolve("joda-time:joda-time",
                                        "org.jadira.usertype:usertype.core",
                                        "com.google.guava:guava",
                                        "com.belerweb:pinyin4j")
                                .withTransitivity().as(File.class))
                        .addClasses(AbstractJpaTest.class, PermissionUtil.class, PrincipalExt.class,JwtUtil.class)
                        .addPackages(true, "com.yunguchang.hibernate",
                                "com.yunguchang.model",
                                "com.yunguchang.data",
                                "com.yunguchang.logger"
                                )
                        .addAsWebInfResource("arquillian-ds.xml")
                        .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                        .addAsResource("META-INF/test-persistence.xml",
                                "META-INF/persistence.xml");

    }
}
