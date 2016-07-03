package com.yunguchang.test.cdi;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by 禕 on 2015/9/7.
 */
@Stereotype
@Alternative
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface MockAlternative {
}
