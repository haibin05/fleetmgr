package com.yunguchang.test.gps;

import com.yunguchang.data.GpsRepository;
import com.yunguchang.data.VehicleRepository;
import com.yunguchang.gps.BaiduClient;
import com.yunguchang.gps.Coord;
import com.yunguchang.gps.GeoconvResult;
import com.yunguchang.gps.GpsCorrelate;
import com.yunguchang.model.common.Car;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.persistence.TGpsPointEntity;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by gongy on 9/9/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class GpsCorrelateTest {

    @InjectMocks
    private GpsCorrelate gpsCorrelate;

    @Mock
    private GpsRepository gpsRepository;

    @Mock
    private VehicleRepository vehicleRepository;


    @Mock
    private BaiduClient baiduClient;


    @Before
    public void setUp() {
        TAzCarinfoEntity mockTAzCarinfoEntity1 = mock(TAzCarinfoEntity.class);
        TAzCarinfoEntity mockTAzCarinfoEntity2 = mock(TAzCarinfoEntity.class);
        when(vehicleRepository.getCarByBadge("vehicleNo1")).thenReturn(mockTAzCarinfoEntity1);
        when(vehicleRepository.getCarByBadge("vehicleNo2")).thenReturn(mockTAzCarinfoEntity2);
        TGpsPointEntity tGpsPointEntity1=mock(TGpsPointEntity.class);
        //when()
        //when(gpsRepository.getGpsRecordByCarIdAndPointTime(anyString(),any())).thenReturn()
        when(mockTAzCarinfoEntity1.getId()).thenReturn("carId1");
        when(mockTAzCarinfoEntity2.getId()).thenReturn("carId2");
        GeoconvResult mockGeoconvResult=mock(GeoconvResult.class);
        when(baiduClient.geoconv(anyString(),anyInt(),anyInt(),anyString())).thenReturn(mockGeoconvResult);
        when(mockGeoconvResult.getResult()).thenReturn(new Coord[]{});
}



    @Test
    public void test() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        for (int i = 0; i < 20; i++) {
            Car vehicleNo1 = new Car("vehicleNo1");
            vehicleNo1.setBadge("vehicleNo1");
            GpsPoint gpsPoint1 = new GpsPoint( i, i, i, vehicleNo1, new DateTime());
            Car vehicleNo2 = new Car("vehicleNo2");
            vehicleNo2.setBadge("vehicleNo2");
            GpsPoint gpsPoint2 = new GpsPoint( i*2, i*2, i*2, vehicleNo2, new DateTime());
            DateTime now = new DateTime(2015, 8, 9, 10, 10);
            now=now.plusSeconds( i * 15+2);
            gpsCorrelate.putGpsPoints(asList(new GpsPoint[]{gpsPoint1, gpsPoint2}), now);

        }
//        verify(gpsRepository,times(2)).mergePoint(gpsEntity, argThat(new ArgumentMatcher<GpsPoint>() {
//
//                    @Override
//                    public boolean matches(Object argument) {
//                        return false;
//                    }
//
////            @Override
////            public boolean matches(GpsPoint point, DateTime persistTime) {
////                List<TGpsPointEntity> points = (List<TGpsPointEntity>) arg;
////                if (points.size() != 2) {
////                    return false;
////                }
////
////                TGpsPointEntity tGpsPointEntity1 = null;
////                TGpsPointEntity tGpsPointEntity2 = null;
////                for (TGpsPointEntity point : points) {
////                    if (point.getCar().getId().equals("carId1")) {
////                        tGpsPointEntity1 = point;
////                    }
////                    if (point.getCar().getId().equals("carId2")) {
////                        tGpsPointEntity2 = point;
////                    }
////                }
////                if (null == tGpsPointEntity1) {
////                    return false;
////                }
////
////
////                if (null == tGpsPointEntity2) {
////                    return false;
////                }
////                assertEquals(tGpsPointEntity1.getCar().getId(), "carId1");
////                assertEquals(tGpsPointEntity2.getCar().getId(), "carId2");
////
////
////                for (int i = 0; i < 20; i++) {
////                    try {
////                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity1, "lat" + (i + 1)), Double.valueOf(i));
////                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity1, "lng" + (i + 1)), Double.valueOf(i));
////                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity1, "speed" + (i + 1)), Double.valueOf(i));
////                    } catch (Exception e) {
////                        return false;
////
////                    }
////
////                }
////
////
////                for (int i = 0; i < 20; i++) {
////                    try {
////                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity2, "lat" + (i + 1)), Double.valueOf(i * 2));
////                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity2, "lng" + (i + 1)), Double.valueOf(i * 2));
////                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity2, "speed" + (i + 1)), Double.valueOf(i * 2));
////                    } catch (Exception e) {
////                        return false;
////
////                    }
////
////                }
////
////                return true;
////            }
//                })
//
//                , argThat(new ArgumentMatcher<DateTime>() {
//                    @Override
//                    public boolean matches(Object argument) {
//                        return false;
//                    }
//                })
//
//        );
    }
}
