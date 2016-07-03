package com.yunguchang.test.jpa;

import com.yunguchang.data.VehicleRepository;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import com.yunguchang.sam.PrincipalExt;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by 禕 on 2015/10/24.
 */
@RunWith(Arquillian.class)
public class FilterTest extends  AbstractJpaTest{
    @Inject
    private VehicleRepository vehicleRepository;
    @Test
    public void testFindWithNullPermission() {
        TAzCarinfoEntity car = vehicleRepository.getCarById("2a59c66d605d42858258c6aae7571a26", null);
        assertNotNull(car);

//        List<TRsDriverinfoEntity> results = driverRepository.queryDrivers(null, null, null);
//        assertTrue(results.size() > 0);
    }

    @Test
    public void testFindWithPermission() {
        TAzCarinfoEntity car = vehicleRepository.getCarById("2a59c66d605d42858258c6aae7571a26", new PrincipalExt(){
            @Override
            public String getName() {
                return "no exists";
            }
        });
        assertNull(car);
//        List<TRsDriverinfoEntity> results = driverRepository.queryDrivers(null, null, null);
//        assertTrue(results.size() > 0);
    }
}
