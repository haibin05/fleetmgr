package com.yunguchang.test.gps;

import com.yunguchang.test.cdi.MockAlternative;
import com.yunguchang.data.GpsRepository;
import com.yunguchang.data.VehicleRepository;

import javax.enterprise.inject.Produces;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 * Created by gongy on 9/10/2015.
 */
public class MockVehicleRepositoryProducer {



    @Produces
    @MockAlternative
    public VehicleRepository createVehicleRepo() {
        return mock(VehicleRepository.class);

    }
}
