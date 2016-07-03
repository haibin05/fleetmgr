package com.yunguchang.test.jpa;

import com.yunguchang.data.GpsRepository;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import com.yunguchang.model.persistence.TGpsPointEntity;
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
public class GpsTest extends AbstractJpaTest  {
    @Inject
    private GpsRepository gpsRepository;


    @Test
    public void testLastGpsPointOfEachCar() {
        List<TGpsPointEntity> gpsPoints = gpsRepository.getLastGpsPointOfEachCar();

        assertTrue(gpsPoints.size() > 0);
    }

    @Test
    public void testUnAdjustedPoints(){
        gpsRepository.getCarIdToAdjusted();
    }


    @Test
    public void testNoGpsPoints(){
        List<TAzCarinfoEntity> results = gpsRepository.getCarsNoGpsMoreThan(10);
        assertTrue(results.size()>0);
    }

}
