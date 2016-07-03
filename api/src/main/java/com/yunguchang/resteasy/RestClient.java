package com.yunguchang.resteasy;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 import static java.lang.annotation.ElementType.*;

/**
 * Created by gongy on 2016/1/13.
 */
 @Retention(RetentionPolicy.RUNTIME)
 @Target({METHOD, FIELD, PARAMETER, TYPE})
     public @interface RestClient {
    @Nonbinding int poolSize() default 10;
}
