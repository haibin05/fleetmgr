package com.yunguchang.service;

import com.yunguchang.cache.CacheKeyName;
import com.yunguchang.cache.StaticKeyGenerator;

import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Created by gongy on 2015/10/5.
 */
@ApplicationScoped
public class VersionServiceImpl {
    @CacheResult(cacheKeyGenerator = StaticKeyGenerator.class)
    @CacheKeyName("version.build")
    public String getBuild(ServletContext application) throws IOException {
        InputStream inputStream = application.getResourceAsStream("/META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(inputStream);
        Attributes attributes = manifest.getMainAttributes();
        inputStream.close();
        return attributes.getValue("Implementation-Build");
    }

}
