package com.yunguchang.model.common;

import org.joda.time.DateTime;

/**
 * Created by haibin on 2016/1/29.
 */
public class StatisticsInfo {
    // 车辆数
    private Integer countOfAllCar;
    // 安装GPS车辆数
    private Integer countOfGpsCar;
    // 当日任务数量
    private Integer countOfSchedule;
    // 当日告警数量
    private Integer countOfAlarm;
    // 当前出车数量
    private Integer countOfOutCar;
    // 昨日9:00未回厂数量
    private Integer countOfNotReturnCar;
    // 统计时间
    private DateTime inventoryTime;

    public Integer getCountOfAllCar() {
        return countOfAllCar;
    }

    public void setCountOfAllCar(Integer countOfAllCar) {
        this.countOfAllCar = countOfAllCar;
    }

    public Integer getCountOfGpsCar() {
        return countOfGpsCar;
    }

    public void setCountOfGpsCar(Integer countOfGpsCar) {
        this.countOfGpsCar = countOfGpsCar;
    }

    public Integer getCountOfSchedule() {
        return countOfSchedule;
    }

    public void setCountOfSchedule(Integer countOfSchedule) {
        this.countOfSchedule = countOfSchedule;
    }

    public Integer getCountOfAlarm() {
        return countOfAlarm;
    }

    public void setCountOfAlarm(Integer countOfAlarm) {
        this.countOfAlarm = countOfAlarm;
    }

    public Integer getCountOfOutCar() {
        return countOfOutCar;
    }

    public void setCountOfOutCar(Integer countOfOutCar) {
        this.countOfOutCar = countOfOutCar;
    }

    public Integer getCountOfNotReturnCar() {
        return countOfNotReturnCar;
    }

    public void setCountOfNotReturnCar(Integer countOfNotReturnCar) {
        this.countOfNotReturnCar = countOfNotReturnCar;
    }

    public DateTime getInventoryTime() {
        return inventoryTime;
    }

    public void setInventoryTime(DateTime inventoryTime) {
        this.inventoryTime = inventoryTime;
    }
}
