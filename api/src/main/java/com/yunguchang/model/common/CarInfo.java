package com.yunguchang.model.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 当前车辆信息
 */
@JsonSerialize
public class CarInfo {
    private Driver driver;
    private Fleet fleet;
    private GpsPoint gpsPoint;
    private Schedule schedule;
    private CarState carState;

    /**
     * 当前车辆司机
     * 如果有调度单，那么和调度单的司机一致
     * 如果没有调度单，那是车辆的默认司机
     *
     * @return 当前车辆司机
     */
    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Fleet getFleet() {
        return fleet;
    }

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
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
     * 当前本车的调度任务
     *
     * @return 当前本车的调度任务
     */
    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public CarState getCarState() {
        return carState;
    }

    public void setCarState(CarState carState) {
        this.carState = carState;
    }
}
