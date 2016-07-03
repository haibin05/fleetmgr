package com.yunguchang.cache;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import java.io.IOException;

/**
 * Created by gongy on 2015/10/5.
 */

public class EmbeddedCacheManagerProducer {

    @Produces
    @ApplicationScoped
    public EmbeddedCacheManager getDefaultEmbeddedCacheManager(Configuration defaultConfiguration) throws IOException {
        return new DefaultCacheManager("infinispan.xml");
    }

    private void stopCacheManager(@Disposes EmbeddedCacheManager defaultEmbeddedCacheManager) {
        defaultEmbeddedCacheManager.stop();
    }
}