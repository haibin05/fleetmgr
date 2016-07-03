package com.yunguchang.test.jpa;

import com.yunguchang.data.DriverRepository;
import com.yunguchang.model.common.DriverStatus;
import com.yunguchang.model.persistence.TRsDriverinfoEntity;
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
public class DriverTest extends AbstractJpaTest  {
    @Inject
    private DriverRepository driverRepository;


    @Test
    public void testListAllDrivers() {
        List<TRsDriverinfoEntity> results = driverRepository.queryDrivers(null, null, null, null, null, null);
        assertTrue(results.size() > 0);
    }


    @Test
    public void testListAllIDLEDrivers() {
        List<TRsDriverinfoEntity> results = driverRepository.queryDrivers(DriverStatus.IDLE, null, null, null, null, null);
        assertTrue(results.size() > 0);
    }


    @Test
    public void testListAllDriversOfFleet() {
        List<TRsDriverinfoEntity> results = driverRepository.queryDrivers(null, null, "0010701", null, null, null);
        assertTrue(results.size() > 0);
    }
    @Test
    public void testListDriversWithOtherStatus() {
        driverRepository.queryDrivers(DriverStatus.WORK, null, null, null, null, null);
        driverRepository.queryDrivers(DriverStatus.LEAVE, null, null, null, null, null);
    }

}
