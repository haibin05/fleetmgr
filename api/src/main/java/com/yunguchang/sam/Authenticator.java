package com.yunguchang.sam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by gongy on 9/7/2015.
 */
public interface Authenticator {
    public PrincipalExt authenticate(HttpServletRequest req, HttpServletResponse resp);



}
