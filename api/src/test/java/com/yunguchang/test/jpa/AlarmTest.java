package com.yunguchang.test.jpa;

import com.yunguchang.data.AlarmRepository;
import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.persistence.TBusAlarmEntity;
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
public class AlarmTest extends AbstractJpaTest {
    @Inject
    private AlarmRepository alarmRepository;


    @Test
    public  void testGetAlarmByType(){
        List<TBusAlarmEntity> results = alarmRepository.getAlarmByStartEndType(null, null, Alarm.SPEEDING,  null, null);
        assertTrue(results.size()>0);
    }


}
