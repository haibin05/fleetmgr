package com.yunguchang.test.rest;

import com.yunguchang.model.common.Application;
import com.yunguchang.model.common.User;
import com.yunguchang.rest.ApplicationService;
import com.yunguchang.rest.AuthService;
import com.yunguchang.rest.PassengerService;
import com.yunguchang.test.rest.AbstractRestTest;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by gongy on 8/25/2015.
 */

@RunWith(Arquillian.class)
public class PassengerServiceTest extends AbstractRestTest {

    @Test
    public void testGetAllPassengers(@ArquillianResteasyResource("api") AuthService authService,@ArquillianResteasyResource("api") PassengerService passengerService) {
        //String token=super.getLoginToken(authService);
        List<User> users = passengerService.listAllPassengers("张",null,5,10,null);
        assertEquals(10, users.size() );
        for (User user : users) {
            System.out.printf(Arrays.deepToString(user.getNamePinyin()));
        }

    }


//    @Override
//    protected String getUserName() {
//        return "00180671";
//    }
}
