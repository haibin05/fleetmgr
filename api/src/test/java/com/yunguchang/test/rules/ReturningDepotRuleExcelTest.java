package com.yunguchang.test.rules;

import com.yunguchang.gps.GpsUtil;
import com.yunguchang.model.common.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import javax.enterprise.event.Event;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Created by gongy on 2015/11/22.
 */
public class ReturningDepotRuleExcelTest extends  AbstractRuleTest{
private Event returningDepotEventSource;

        @Before
        public void setUp(){
            super.setUp();
            returningDepotEventSource = mock(Event.class);
            basicKSession.setGlobal("returningDepotEventSource", returningDepotEventSource);
        }


    @Test
    public void testReturningDepot() throws InterruptedException, IOException {
        assertNotNull(basicKSession);
        PseudoClockScheduler clock = basicKSession.getSessionClock();
        DateTime clockTime = new DateTime(2015, 11, 11, 0, 0, 0);
        clock.setStartupTime(clockTime.getMillis());


            Car car01 = new Car("car01");
            Depot depot01 = new Depot();
            depot01.setLat(30.799249);
            depot01.setLng(120.734462);
            car01.setDepot(depot01);

            basicKSession.insert(car01);

        Car car02 = new Car("car02");
        Depot depot02 = new Depot();
        depot02.setLat(30.799249);
        depot02.setLng(120.734462);
        car02.setDepot(depot02);

        basicKSession.insert(car02);


        InputStream excel = this.getClass().getClassLoader().getResourceAsStream("gpsPoints.xlsx");
        Workbook wb = new XSSFWorkbook(excel);
        Sheet sheet = wb.getSheetAt(0);

        Iterator<Row> rowIter = sheet.rowIterator();
        rowIter.next();
        while(rowIter.hasNext()){
            Row row = rowIter.next();
            DateTime sampleTime = new DateTime( row.getCell(0).getDateCellValue());
            //DateTime persistTime = new DateTime( row.getCell(1).getDateCellValue());
            double lng = row.getCell(2).getNumericCellValue();
            double lat=row.getCell(3).getNumericCellValue();
            double speed=row.getCell(4).getNumericCellValue();

            GpsPoint point=new GpsPoint(lng,lat,speed,car01,sampleTime);
            GpsPoint poin2t=new GpsPoint(lng,lat,speed,car02,sampleTime);

            int advancedSeconds = Seconds.secondsBetween(clockTime, sampleTime).getSeconds();
            basicKSession.insert(point);
            basicKSession.insert(poin2t);


            clock.advanceTime(advancedSeconds, TimeUnit.SECONDS);

            Thread.sleep(1000);
            clockTime=sampleTime;
        }
        clock.advanceTime(15,TimeUnit.MINUTES);
        Thread.sleep(1000);

        verify(returningDepotEventSource,times(2)).fire(argThat(new ArgumentMatcher<AlarmEvent>() {
            @Override
            public boolean matches(Object o) {
                if (o instanceof ReturningDepotEvent) {
                    return true;
                }
                return false;
            }
        }));

//        for (int j=0;j<2;j++) {
//            double lng=120.60386760642;
//
//            for (int i = 0; i < 10; i++) {
//                clock.advanceTime(15, TimeUnit.SECONDS);
//                Thread.sleep(1000);
//                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
//                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
//                System.out.println(lng + " " + GpsUtil.getDistance(30.786726237643, lng, 30.786726237643, 120.57187758132));
//                lng -= 0.002;
//            }
//
//            lng=120.60386760642;
//
//            for (int i = 0; i < 10; i++) {
//                clock.advanceTime(15, TimeUnit.SECONDS);
//                Thread.sleep(1000);
//                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car01, new DateTime(clock.getCurrentTime()).minusSeconds(4)));
//                basicKSession.insert(new GpsPoint(lng, 30.786726237643, 60, car02, new DateTime(clock.getCurrentTime()).minusSeconds(5)));
//                System.out.println(lng + " " + GpsUtil.getDistance(30.786726237643, lng, 30.786726237643, 120.57187758132));
//                lng -= 0.002;
//            }
//
//
//            clock.advanceTime(15, TimeUnit.MINUTES);
//            Thread.sleep(1000);
//        }
//        verify(returningDepotEventSource,times(4)).fire(any(ReturningDepotEvent.class));



    }
}
