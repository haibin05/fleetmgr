package com.yunguchang.model.common;

/**
 * Created by WHB on 2015-09-18.
 */
public class DriverInfo {

    // 是否在执勤
    private boolean onDuty;

    // 执勤时信息
    private Car car;
    private GpsPoint gpsPoint;
    private Schedule schedule;

    /**
     * 是否在执勤
     *
     * @return
     */
    public boolean isOnDuty() {
        return onDuty;
    }

    public void setOnDuty(boolean onDuty) {
        this.onDuty = onDuty;
    }

    /**
     * 当前使用车辆
     * 如果有调度单，那么和调度单的车辆信息一致
     * 如果没有调度单，那么显示为空，不实用默认
     *
     * @return 当前车辆
     */
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    /**
     * 当前GPS信息
     *
     * @return 当前GPS信息
     */
    public GpsPoint getGpsPoint() {
        return gpsPoint;
    }

    public void setGpsPoint(GpsPoint gpsPoint) {
        this.gpsPoint = gpsPoint;
    }

    /**
     * 当前驾驶员的调度任务
     *
     * @return 当前驾驶员的调度任务
     */
    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
