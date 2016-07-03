package com.yunguchang.model.sync;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by haibin on 2016/1/6.
 */
public class SyncEvaluateInfo {

    private String uuId;
    private String userId;
    private String applyNo;
    private String carId;
    private String driverId;
    private Integer carScore;
    private Integer driverScore;
    private String evalStatus;
    private String evalDate;
    private String remark;
    private Integer teamscore;
    private String teamreason;
    private String driverreason;
    private String carreason;
    private String type;        // 类型(0为用车评价，1为车队评价)
    private String relano;
    private String responseinfo;    // NEW
    private String gdzt;//归档状态

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Integer getCarScore() {
        return carScore;
    }

    public void setCarScore(Integer carScore) {
        this.carScore = carScore;
    }

    public Integer getDriverScore() {
        return driverScore;
    }

    public void setDriverScore(Integer driverScore) {
        this.driverScore = driverScore;
    }

    public String getEvalStatus() {
        return evalStatus;
    }

    public void setEvalStatus(String evalStatus) {
        this.evalStatus = evalStatus;
    }

    public Date getEvalDate() {
        return new DateTime(evalDate).toDate();
    }

    public void setEvalDate(String evalDate) {
        this.evalDate = evalDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getTeamscore() {
        return teamscore;
    }

    public void setTeamscore(Integer teamscore) {
        this.teamscore = teamscore;
    }

    public String getTeamreason() {
        return teamreason;
    }

    public void setTeamreason(String teamreason) {
        this.teamreason = teamreason;
    }

    public String getDriverreason() {
        return driverreason;
    }

    public void setDriverreason(String driverreason) {
        this.driverreason = driverreason;
    }

    public String getCarreason() {
        return carreason;
    }

    public void setCarreason(String carreason) {
        this.carreason = carreason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelano() {
        return relano;
    }

    public void setRelano(String relano) {
        this.relano = relano;
    }

    public String getResponseinfo() {
        return responseinfo;
    }

    public void setResponseinfo(String responseinfo) {
        this.responseinfo = responseinfo;
    }

    public String getGdzt() {
        return gdzt;
    }

    public void setGdzt(String gdzt) {
        this.gdzt = gdzt;
    }
}
