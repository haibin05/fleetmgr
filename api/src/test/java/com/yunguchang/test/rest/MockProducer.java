package com.yunguchang.test.rest;

import com.yunguchang.gps.BaiduClient;
import com.yunguchang.test.cdi.MockAlternative;
import org.mockito.ArgumentMatcher;

import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by gongy on 2015/10/30.
 */
public class MockProducer {
    @Produces
    @MockAlternative
    BaiduClient createBaiDuClient() {

        BaiduClient baiduClient = mock(BaiduClient.class);
//        when(authenticator.authenticate(argThat(new ArgumentMatcher<HttpServletRequest>() {
//            @Override
//            public boolean matches(Object argument) {
//                HttpServletRequest req = (HttpServletRequest) argument;
//                return "Bearer TOKEN_STRING".equals(req.getHeader("Authorization"));
//            }
//        }), any(HttpServletResponse.class)))
//                .thenReturn(true);
//        when(authenticator.getUserId())
//                .thenReturn("test");
//        when(authenticator.getRoles())
//                .thenReturn(Collections.EMPTY_LIST);
        return baiduClient;


    }
}
