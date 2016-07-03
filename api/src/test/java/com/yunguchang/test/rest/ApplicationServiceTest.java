package com.yunguchang.test.rest;

import com.yunguchang.model.common.*;
import com.yunguchang.rest.ApplicationService;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.BadRequestException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by gongy on 8/25/2015.
 */

@RunWith(Arquillian.class)
public class ApplicationServiceTest extends AbstractRestTest{

    @Test
    public void testGetAllApplications(@ArquillianResteasyResource("api") ApplicationService applicationService) {

        OrderByParam.OrderBy orderBy1 =new OrderByParam.OrderBy();
        orderBy1.setAsc(true);
        orderBy1.setFiled("start");
        OrderByParam.OrderBy orderBy2 =new OrderByParam.OrderBy();
        orderBy2.setAsc(false);
        orderBy2.setFiled("end");
        List<Application> applications = applicationService.listAllApplications("00180507","04",null,null,new DateTime(2015,6,1,0,0),null,null,10,10, new OrderByParam( new OrderByParam.OrderBy[]{orderBy1, orderBy2}), null);
        assertTrue(applications.size()==10);

    }

    @Test
    public void testCreateAndUpdateApplications(@ArquillianResteasyResource("api") ApplicationService applicationService) {
        DateTime start= new DateTime(2015,10,10,12,0);
        DateTime end= start.plusHours(4);
        Application application= new Application();
        application.setStart(start);
        application.setEnd(end);
        application.setOrigin("origin1");
        application.setDestination("dest1");
        KeyValue keyValue=new KeyValue();
        keyValue.setKey("01");
        application.setReason(keyValue);
        application.setPassengers(3);
        User passenger=new User();
        passenger.setUserId("02deec05bf6445a1b25fb9dd8fc15887");
        application.setPassenger(passenger);

        User coordinator=new User();
        coordinator.setUserId("00180059");
        application.setCoordinator(coordinator);


        assertNull(application.getId());
        application
                = applicationService.createApplication(application , null);
        assertNotNull(application.getId());
        assertEquals(application.getOrigin(),"origin1");

        application.setOrigin("origin2");
//        application=applicationService.updateApplication(application.getId(),application,null);
//        assertEquals(application.getOrigin(),"origin2");

    }


    @Test
    public void testCreateAnFindApplications(@ArquillianResteasyResource("api") ApplicationService applicationService) {
        DateTime start= DateTime.now();
        DateTime end= start.plusHours(4);
        Application application= new Application();
        application.setStart(start);
        application.setEnd(end);
        application.setOrigin("origin1");
        application.setDestination("dest1");
        application.setComment("comment1");
        KeyValue keyValue=new KeyValue();
        keyValue.setKey("01");
        application.setReason(keyValue);
        application.setPassengers(3);
        User passenger=new User();
        passenger.setUserId("02deec05bf6445a1b25fb9dd8fc15887");
        application.setPassenger(passenger);

        User coordinator=new User();
        coordinator.setUserId("00180059");
        application.setCoordinator(coordinator);


        assertNull(application.getId());
        application
                = applicationService.createApplication(application , null);
        assertNotNull(application.getId());
        assertEquals(application.getOrigin(),"origin1");
        assertEquals(application.getComment(),"comment1");


        OrderByParam.OrderBy orderBy=new OrderByParam.OrderBy();
        orderBy.setFiled("start");
        orderBy.setAsc(false);
        OrderByParam orderByParam=new OrderByParam(orderBy);
        List<Application> applications = applicationService.listAllApplications(null, null, null, null, null, null, null,null, null, orderByParam, null);
        for (Application application1 : applications) {
            if (application1.getId().equals(application.getId())){
                return;
            }
        }


        fail("not found new creation application");
    }

    @Test(expected = BadRequestException.class)
    public void testCreateInvalidApplications(@ArquillianResteasyResource("api") ApplicationService applicationService) {
        Application application= new Application();
        DateTime start= new DateTime(2015,10,10,12,0);
        DateTime end= start.plusHours(4);
        application.setStart(start);
        application.setEnd(end);
        application
                = applicationService.createApplication(application , null);
    }

    @Test
    public void testGetCandidateCars(@ArquillianResteasyResource("api") ApplicationService applicationService){
        List<Car> result = applicationService.listAllCandidateCars("008f0d6f86cc41aeab0fb10787bc6b24,000dbad2bdda4d46be0c0a157480f08c", null, null, null, null);
        for (Car car : result) {
            System.out.println(car.getBadge());
            System.out.println(car.getId());
        }
    }


    @Test
    public void testGetCandidateDriver(@ArquillianResteasyResource("api") ApplicationService applicationService){
        List<Driver> result = applicationService.listAllCandidateDrivers("008f0d6f86cc41aeab0fb10787bc6b24,000dbad2bdda4d46be0c0a157480f08c", "6b490d428f824033ae0cdc36d388848a", null, null, null, null);
        for (Driver driver : result) {
            System.out.println(driver.getName());
        }
    }



    @Test
    public void testUpdateApplicationStatus(@ArquillianResteasyResource("api") ApplicationService applicationService){
        ApplicationStatus applicationStatus=new ApplicationStatus();
        applicationStatus.setStatus("8");
        Application application = applicationService.updateApplicationStatus("008f0d6f86cc41aeab0fb10787bc6b24", applicationStatus, null);
        assertEquals(application.getStatus().getKey(),"8");


        applicationStatus.setStatus("7");
        application = applicationService.updateApplicationStatus("008f0d6f86cc41aeab0fb10787bc6b24", applicationStatus, null);
        assertEquals(application.getStatus().getKey(),"7");

    }
    @Test
    public void testUpdateApplicationRate(@ArquillianResteasyResource("api") ApplicationService applicationService){
        RateOfApplication rateOfApplication=new RateOfApplication();
        rateOfApplication.setRegularApplication(false);
        Application application = applicationService.updateRateOfApplication("0084876cae2046909e586178345c6475", rateOfApplication, null);
        assertFalse(application.isRegular());
        assertNotNull(application.getIrregularReason());

        rateOfApplication.setRegularApplication(true);
        Application application2 = applicationService.updateRateOfApplication("0084876cae2046909e586178345c6475", rateOfApplication, null);
        assertTrue(application2.isRegular());
        assertNull(application2.getIrregularReason());
    }

    @Test
    public void testRateOfDriver(@ArquillianResteasyResource("api") ApplicationService applicationService){
        RateOfDriver rateOfDriver=new RateOfDriver();
        rateOfDriver.setCarRate(3);
        applicationService.updateRateOfDriver("000b50b3abe14a35b47f9b55b835e0c5","d9a3dd502a1646e6ac3fa68df4ed16c8",rateOfDriver,null);
    }


    @Test
    public void testRateOfPassenger(@ArquillianResteasyResource("api") ApplicationService applicationService){
        RateOfPassenger rateOfPassenger=new RateOfPassenger();
        rateOfPassenger.setPassengerRate(3);
        applicationService.updateRateOfPassenger("000b50b3abe14a35b47f9b55b835e0c5", "d9a3dd502a1646e6ac3fa68df4ed16c8", rateOfPassenger, null);
    }
}
