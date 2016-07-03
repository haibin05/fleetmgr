package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import com.yunguchang.gps.GpsUtil;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 车辆行驶轨迹路径
 * 每个路径有开始时间,结束时间,车辆行驶告警
 */
@JsonSerialize
public class PathWithAlarm {
    private String id;
    private Schedule schedule;
    private Alarm[] alarms;
    private DateTime start;
    private DateTime end;
    private List<GpsPoint> gpsPoints = new ArrayList<>();
    private Car car;
    private boolean moving;
    private List<GpsPoint> adjustedPoints;
    private Driver driver;

    /**
     * 轨迹id
     *
     * @return 轨迹id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 轨迹所属车辆
     *
     * @return 轨迹所属车辆
     */
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    /**
     * 轨迹所属车辆当时执行的任务
     *
     * @return 轨迹所属车辆当时执行的任务
     */
    public Schedule getSchedule() {
        return schedule;
    }


    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * 此段轨迹产生的报警
     *
     * @return 此段轨迹产生的报警
     */
    public Alarm[] getAlarms() {
        return alarms;
    }

    public void setAlarms(Alarm[] alarms) {
        this.alarms = alarms;
    }

    /**
     * 轨迹开始时间
     *
     * @return 轨迹开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @DocumentationExample("2015-08-22, 12:05")
    public DateTime getStart() {
        return start;
    }


    public void setStart(DateTime start) {
        this.start = start;
    }

    /**
     * 轨迹结束时间
     *
     * @return 轨迹结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @DocumentationExample("2015-08-22, 12:15")
    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    /**
     * 轨迹的gps监控数据
     *
     * @return 轨迹的gps监控数据
     */
    public List<GpsPoint> getGpsPoints() {
        return gpsPoints;
    }

    public void setGpsPoints(List<GpsPoint> gpsPoints) {
        this.gpsPoints = gpsPoints;
    }

    /**
     * 当前轨迹是停车状态
     * 如果是停车状态,则只有一个GPS点位
     *
     * @return
     */
    @DocumentationExample
    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    /**
     * 修正后的行车轨迹
     *
     * @return
     */
    public List<GpsPoint> getAdjustedPoints() {
        return adjustedPoints;
    }

    public void setAdjustedPoints(List<GpsPoint> adjustedPoints) {
        this.adjustedPoints = adjustedPoints;
    }

    /**
     * 行驶该轨迹司机
     *
     * @return
     */
    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public double getKm(){
        if(gpsPoints==null || gpsPoints.size()==1){
            return  0;
        }
        double km=0;
        Iterator<GpsPoint> iter = gpsPoints.iterator();
        GpsPoint prePoint = iter.next();
        while(iter.hasNext()){
            GpsPoint point = iter.next();
            km += GpsUtil.getDistance(point.getLat(),point.getLng(),prePoint.getLat(),prePoint.getLng())/1000;
            prePoint=point;
        }
        return km;
    }


}
