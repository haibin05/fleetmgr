package com.yunguchang.cache;

import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.GeneratedCacheKey;
import java.lang.annotation.Annotation;

/**
 * Created by gongy on 2015/10/5.
 */
public class StaticKeyGenerator  implements CacheKeyGenerator {


    public static class Key implements GeneratedCacheKey {
        private String id;

        public Key(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (id != null ? !id.equals(key.id) : key.id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }


    @Override
    public GeneratedCacheKey generateCacheKey(CacheKeyInvocationContext<? extends Annotation> cacheKeyInvocationContext) {
        CacheKeyName cacheKeyName = cacheKeyInvocationContext.getMethod().getAnnotation(CacheKeyName.class);
        return new Key(cacheKeyName.value());

    }
}
