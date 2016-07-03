package com.yunguchang.restapp;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by gongy on 2015/11/12.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireLogin {
}
