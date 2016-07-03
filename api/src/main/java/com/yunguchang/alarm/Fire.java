package com.yunguchang.alarm;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.*;
/**
 * Created by gongy on 9/23/2015.
 */
@Qualifier

@Target({METHOD, FIELD, PARAMETER, TYPE})

@Retention(RUNTIME)

public @interface Fire {


}