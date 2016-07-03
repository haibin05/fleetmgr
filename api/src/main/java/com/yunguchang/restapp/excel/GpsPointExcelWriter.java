package com.yunguchang.restapp.excel;

import com.yunguchang.model.common.GpsPoint;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_SPREAD_SHEET;

/**
 * Created by gongy on 2015/11/22.
 */
@Provider
@Produces(APPLICATION_SPREAD_SHEET)
public class GpsPointExcelWriter extends  AbstractExcelWriter<GpsPoint>{
    public GpsPointExcelWriter() {
        super(GpsPoint.class);
    }

    @Override
    protected Workbook create(List<GpsPoint> elementList) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("gps");
        Row row = sheet.createRow(0);
//            车牌号	时间	告警类型	经度	纬度	速度（KM/h
        row.createCell(0).setCellValue("采样时间");
        row.createCell(1).setCellValue("存储时间");
        row.createCell(2).setCellValue("经度");
        row.createCell(3).setCellValue("纬度");
        row.createCell(4).setCellValue("速度(KM/h)");
        int rowIndex = 1;
        CellStyle cellStyle = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();
        cellStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("yyyy-mm-d hh:mm:ss"));
        for (GpsPoint point : elementList) {
            row = sheet.createRow(rowIndex);
            Cell sampleTimeCell = row.createCell(0);
            sampleTimeCell.setCellValue(point.getSampleTime().toDate());
            sampleTimeCell.setCellStyle(cellStyle);
            Cell persistTimeCell = row.createCell(1);
            persistTimeCell.setCellValue(point.getPersistTime().toDate());
            persistTimeCell.setCellStyle(cellStyle);

            row.createCell(2).setCellValue(point.getLng());
            row.createCell(3).setCellValue(point.getLat());
            row.createCell(4).setCellValue(point.getSpeed());

            rowIndex++;
        }
        return wb;
    }
}
