package com.yunguchang.model.sync;

import java.util.Date;

public class ScheduleCarForSync {

    private String uuId;        // UUID
    private String relaNo;      // 调度分配编号
    private String carId;       // 车辆ID
    private String cphm;       // 车辆ID
    private String driverId;    // 驾驶员ID
    private String driverName;    // 驾驶员姓名
    private String driverMobile;    // 驾驶员手机
    private String schCarNo;    // 派车单编号
    private Integer status;     // 当前状态
    private Integer cancelSeason;     // 当前状态
    private String cancelUser;     // 当前状态
    private Date cancelTime;     // 当前状态
    private Integer cancelType;     // 当前状态

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getRelaNo() {
        return relaNo;
    }

    public void setRelaNo(String relaNo) {
        this.relaNo = relaNo;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverMobile() {
        return driverMobile;
    }

    public void setDriverMobile(String driverMobile) {
        this.driverMobile = driverMobile;
    }

    public String getSchCarNo() {
        return schCarNo;
    }

    public void setSchCarNo(String schCarNo) {
        this.schCarNo = schCarNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCancelSeason() {
        return cancelSeason;
    }

    public void setCancelSeason(Integer cancelSeason) {
        this.cancelSeason = cancelSeason;
    }

    public String getCancelUser() {
        return cancelUser;
    }

    public void setCancelUser(String cancelUser) {
        this.cancelUser = cancelUser;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Integer getCancelType() {
        return cancelType;
    }

    public void setCancelType(Integer cancelType) {
        this.cancelType = cancelType;
    }
}
