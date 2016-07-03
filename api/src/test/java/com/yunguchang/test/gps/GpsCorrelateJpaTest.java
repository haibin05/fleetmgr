package com.yunguchang.test.gps;

import com.yunguchang.model.common.Car;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import com.yunguchang.model.persistence.TGpsPointEntity;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.test.cdi.MockAlternative;
import com.yunguchang.data.GpsRepository;
import com.yunguchang.data.VehicleRepository;
import com.yunguchang.gps.GpsCorrelate;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by gongy on 9/9/2015.
 */
@RunWith(Arquillian.class)
public class GpsCorrelateJpaTest {

    @Inject
    private GpsCorrelate gpsCorrelate;

    @Inject
    private GpsRepository gpsRepository;

    @Inject
    private VehicleRepository vehicleRepository;


    @Deployment(testable = false)
    public static Archive<?> createDeployment() {

        return
                ShrinkWrap.create(WebArchive.class)
                        .addAsLibraries(Maven.resolver()
                                .loadPomFromFile("pom.xml")
                                .importRuntimeDependencies()
                                .resolve()
                                .withTransitivity().as(File.class))
                        .addPackages(true, "com.yunguchang.gps")
                        .addClass(GpsRepository.class)
                        .addClass(VehicleRepository.class)
                        .addClass(GpsPoint.class)
                        .addClass(MockVehicleRepositoryProducer.class)
                        .addClass(MockAlternative.class)
                        .addClass(TGpsPointEntity.class)
                        .addClass(TAzCarinfoEntity.class)
                        .addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-web.xml"))
                        .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"))
                        .addAsWebInfResource("arquillian-ds.xml")
                        .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                        .addAsWebInfResource("com/yunguchang/test/mock-beans.xml", "beans.xml");


    }

    @Before
    public void setUp() {
        TAzCarinfoEntity mockTAzCarinfoEntity1 = mock(TAzCarinfoEntity.class);
        TAzCarinfoEntity mockTAzCarinfoEntity2 = mock(TAzCarinfoEntity.class);
        when(vehicleRepository.getCarByBadge("vehicleNo1")).thenReturn(mockTAzCarinfoEntity1);
        when(vehicleRepository.getCarByBadge("vehicleNo2")).thenReturn(mockTAzCarinfoEntity2);
        when(mockTAzCarinfoEntity1.getId()).thenReturn("carId1");
        when(mockTAzCarinfoEntity2.getId()).thenReturn("carId2");
}



    @Test
    public void test() {

        for (int i = 0; i < 20; i++) {
            GpsPoint gpsPoint1 = new GpsPoint( i, i, i,new Car("vehicleNo1"), new DateTime());
            GpsPoint gpsPoint2 = new GpsPoint( i*2, i*2, i*2,new Car("vehicleNo2"), new DateTime());
            DateTime now = new DateTime(2015, 8, 9, 10, 10);
            now=now.plusSeconds( i * 15+2);
            gpsCorrelate.putGpsPoints(asList(new GpsPoint[]{gpsPoint1, gpsPoint2}), now);

        }
//        verify(gpsRepository,times(1)).save(argThat(new ArgumentMatcher<List<TGpsPointEntity>>(){
//
//            @Override
//            public boolean matches(Object arg) {
//                List<TGpsPointEntity> points= (List<TGpsPointEntity>) arg;
//                if (points.size()!=2){
//                    return false;
//                }
//
//                TGpsPointEntity tGpsPointEntity1=null;
//                TGpsPointEntity tGpsPointEntity2=null;
//                for (TGpsPointEntity point : points) {
//                    if( point.getCar().getId().equals("carId1")){
//                       tGpsPointEntity1=point;
//                    }
//                    if( point.getCar().getId().equals("carId2")){
//                        tGpsPointEntity2=point;
//                    }
//                }
//                if (null== tGpsPointEntity1) {
//                    return false;
//                }
//
//
//
//
//                if (null== tGpsPointEntity2) {
//                    return false;
//                }
//                assertEquals(tGpsPointEntity1.getCar().getId(),"carId1");
//                assertEquals(tGpsPointEntity2.getCar().getId(),"carId2");
//
//
//                for (int i=0;i<20;i++){
//                    try {
//                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity1, "lat" + (i+1)), Double.valueOf(i));
//                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity1, "lng" +(i+1)), Double.valueOf(i));
//                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity1, "speed"+(i+1)), Double.valueOf(i));
//                    } catch (Exception e) {
//                        return false;
//
//                    }
//
//                }
//
//
//                for (int i=0;i<20;i++){
//                    try {
//                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity2, "lat" + (i+1)), Double.valueOf(i*2));
//                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity2, "lng" +(i+1)), Double.valueOf(i*2));
//                        assertEquals(PropertyUtils.getSimpleProperty(tGpsPointEntity2, "speed"+(i+1)), Double.valueOf(i*2));
//                    } catch (Exception e) {
//                        return false;
//
//                    }
//
//                }
//
//                return true;
//            }
//        }));
    }
}
