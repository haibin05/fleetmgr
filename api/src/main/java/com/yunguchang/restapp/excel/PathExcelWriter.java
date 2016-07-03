package com.yunguchang.restapp.excel;

import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.common.PathWithAlarm;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_SPREAD_SHEET;

/**
 * Created by gongy on 2015/10/22.
 */
@Provider
@Produces(APPLICATION_SPREAD_SHEET)
public class PathExcelWriter extends AbstractExcelWriter<PathWithAlarm> {

    public PathExcelWriter() {
        super(PathWithAlarm.class);
    }

    @Override
    public Workbook create(List<PathWithAlarm> pathWithAlarmList) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("路径");
        Row row = sheet.createRow(0);
//        车牌号	时间	地点描述	经度	纬度	速度（KM/h）
//        浙F123456	2015/9/10 12:23	江苏省苏州市张家港市苏虞张公路 国一制纸张家港公司东574米	120.6184588	31.78825823	30
        int headColumn=0;
        row.createCell(headColumn++).setCellValue("车牌号");
        row.createCell(headColumn++).setCellValue("时间");
        row.createCell(headColumn++).setCellValue("地点描述");
        row.createCell(headColumn++).setCellValue("经度");
        row.createCell(headColumn++).setCellValue("纬度");
        row.createCell(headColumn++).setCellValue("速度(KM/h)");
        int rowIndex = 1;
        CellStyle cellStyle = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();
        cellStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("yyyy-mm-d hh:mm:ss"));
        for (PathWithAlarm path : pathWithAlarmList) {
            //如果不是第一行,停止的路径坐标就是前一个路径的最后一点.我们已经输出过了
            if (!path.isMoving() && rowIndex != 1) {
                continue;
            }
            for (GpsPoint gpsPoint : path.getGpsPoints()) {
                int column=0;
                row = sheet.createRow(rowIndex);
                row.createCell(column++).setCellValue(path.getCar().getBadge());
                Cell dateTimeCell = row.createCell(column++);
                dateTimeCell.setCellValue(gpsPoint.getSampleTime().toDate());
                dateTimeCell.setCellStyle(cellStyle);
                row.createCell(column++).setCellValue(gpsPoint.getAddress());
                row.createCell(column++).setCellValue(gpsPoint.getLng());
                row.createCell(column++).setCellValue(gpsPoint.getLat());
                row.createCell(column++).setCellValue(gpsPoint.getSpeed());

                rowIndex++;
            }

        }
        return wb;
    }


}
