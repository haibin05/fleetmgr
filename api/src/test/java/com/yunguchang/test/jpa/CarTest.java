package com.yunguchang.test.jpa;

import com.yunguchang.data.VehicleRepository;
import com.yunguchang.model.common.CarState;
import com.yunguchang.model.common.Depot;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import junit.framework.Assert;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by 禕 on 2015/10/11.
 */
@RunWith(Arquillian.class)
public class CarTest extends AbstractJpaTest {
    @Inject
    private VehicleRepository vehicleRepository;


    @Test
    public void testListCar() {
        List<TAzCarinfoEntity> cars = vehicleRepository.listAllCars();
        assertTrue(cars.size() > 0);
        assertNotNull(cars.get(0).getDriver());
    }


    @Test
    public void testGetCarById() {
        TAzCarinfoEntity car = vehicleRepository.getCarById("c986634252e8484eac5eca25eb5035f5",null);
        assertNotNull(car.getDriver());

    }

    @Test
    public void testListCarByFleet() {
        List<TAzCarinfoEntity> cars = vehicleRepository.listAllCars(null, null, "一车队", null, null, null,null,null, null,null, null);
        assertTrue(cars.size() > 0);
    }


    @Test
    public void testListCarByAwaitingRepaireStatus() {
        List<TAzCarinfoEntity> cars = vehicleRepository.listAllCars(null, null, "一车队", null, null, CarState.AWAITING_REPAIRE,null, null,null, null,null);
        //assertTrue(cars.size() > 0);
    }


    @Test
    public void testListCarByIdleStatus() {
        List<TAzCarinfoEntity> cars = vehicleRepository.listAllCars(null, null, "一车队", null, null, CarState.IDLE,null,null, null,null, null);
        //assertTrue(cars.size() > 0);
    }


    @Test
    public void testUpdateDepotByCar() {
        Depot depot=new Depot();
        depot.setLng(120);
        depot.setLat(30);
        depot.setName("停车厂");
        TAzCarinfoEntity car = vehicleRepository.updateDepotOfCar("2a59c66d605d42858258c6aae7571a26", depot);
        assertNotNull(car);

    }


}
