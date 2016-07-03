package com.yunguchang.restapp.excel;

import com.yunguchang.model.common.AlarmEvent;
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
public class AlarmWorkbookExcelWriter extends AbstractExcelWriter<AlarmEvent> {
    public AlarmWorkbookExcelWriter() {
        super(AlarmEvent.class);
    }

    @Override
    public Workbook create(List<AlarmEvent> alarms) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("告警");
        Row row = sheet.createRow(0);
//            车牌号	时间	告警类型	地点 经度	纬度	速度（KM/h
        int headColumn=0;
        row.createCell(headColumn++).setCellValue("车牌号");
        row.createCell(headColumn++).setCellValue("时间");
        row.createCell(headColumn++).setCellValue("告警类型");
        row.createCell(headColumn++).setCellValue("地点描述");
        row.createCell(headColumn++).setCellValue("经度");
        row.createCell(headColumn++).setCellValue("纬度");
        row.createCell(headColumn++).setCellValue("速度(KM/h)");
        int rowIndex = 1;
        CellStyle cellStyle = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();
        cellStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("yyyy-mm-d hh:mm:ss"));
        for (AlarmEvent alarmEvent : alarms) {
            int column = 0;
            row = sheet.createRow(rowIndex);
            row.createCell(column++).setCellValue(alarmEvent.getCar().getBadge());
            Cell dateTimeCell = row.createCell(column++);
            dateTimeCell.setCellValue(alarmEvent.getStart().toDate());
            dateTimeCell.setCellStyle(cellStyle);
            row.createCell(column++).setCellValue(alarmEvent.getAlarm().getLocalString());
            if (alarmEvent.getGpsPoint() != null) {
                row.createCell(column++).setCellValue(alarmEvent.getGpsPoint().getAddress());
                row.createCell(column++).setCellValue(alarmEvent.getGpsPoint().getLng());
                row.createCell(column++).setCellValue(alarmEvent.getGpsPoint().getLat());
                row.createCell(column++).setCellValue(alarmEvent.getGpsPoint().getSpeed());
            }
            rowIndex++;
        }
        return wb;
    }


}
