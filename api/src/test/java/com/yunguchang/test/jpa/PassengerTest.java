package com.yunguchang.test.jpa;

import com.yunguchang.data.AlarmRepository;
import com.yunguchang.data.PassengerRepository;
import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.persistence.TBusAlarmEntity;
import com.yunguchang.model.persistence.TBusMainUserInfoEntity;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by 禕 on 2015/10/11.
 */
@RunWith(Arquillian.class)
public class PassengerTest extends AbstractJpaTest {
    @Inject
    private PassengerRepository passengerRepository;


    @Test
    public  void testGePassenger(){
        List<TBusMainUserInfoEntity> results = passengerRepository.getAllPassengersByCoordinator(null, null, null, null, null);
        assertTrue(results.size()>0);
    }


}
