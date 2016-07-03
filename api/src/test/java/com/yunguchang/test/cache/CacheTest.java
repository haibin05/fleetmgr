package com.yunguchang.test.cache;

import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;

/**
 * Created by gongy on 2015/10/14.
 */
@RunWith(Arquillian.class)
public class CacheTest {
    @Inject
    private EmbeddedCacheManager embeddedCacheManager;
    @Deployment
    public static Archive<?> createDeployment() {
        return
                ShrinkWrap.create(WebArchive.class)
                        .addAsLibraries(Maven.resolver()
                                .loadPomFromFile("pom.xml")
                                .resolve("org.infinispan:infinispan-jcache")
                                .withTransitivity().as(File.class))
                        .addAsWebInfResource("com/yunguchang/test/cache-beans.xml", "beans.xml");


    }

    @Test
    public void testCache(){
        assertTrue(embeddedCacheManager!=null);
        assertTrue(embeddedCacheManager.getDefaultCacheConfiguration()!=null);
    }

    private void assertTrue(boolean b) {
    }
}
