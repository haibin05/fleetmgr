package com.yunguchang.test.gps;

import com.yunguchang.gps.GpsUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by gongy on 2016/1/27.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReturnDepotExcelTest {
    @Test
    public void testFromExcel() throws IOException {
        InputStream excel = this.getClass().getClassLoader().getResourceAsStream("returndepot.xlsx");
        Workbook wb = new XSSFWorkbook(excel);
        Sheet sheet = wb.getSheetAt(0);

        Iterator<Row> rowIter = sheet.rowIterator();
        rowIter.next();
        while(rowIter.hasNext()){
            Row row = rowIter.next();
            int col=0;
            double lat= row.getCell(col++).getNumericCellValue();
            double lng= row.getCell(col++).getNumericCellValue();
            String carid=row.getCell(col++).getStringCellValue();
            DateTime pointTime = new DateTime( row.getCell(col++).getDateCellValue());
            double depotLat = row.getCell(col++).getNumericCellValue();
            double depotLng = row.getCell(col++).getNumericCellValue();
            double distance=GpsUtil.getDistance(lat,lng,depotLat,depotLng);
            if (distance<500){
                System.out.println(distance);
                System.out.println(carid);
            }


        }
    }
}
