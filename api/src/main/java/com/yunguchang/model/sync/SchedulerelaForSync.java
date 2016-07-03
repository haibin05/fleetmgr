package com.yunguchang.model.sync;

import java.util.Date;

public class SchedulerelaForSync {

    private String uuId;        // 主键ID
    private String sscd;        // 所属车队
    private Date startTime;     // 派车时间
    private Date endTime;       // 派车预计结束时间
    private String senduser;    // 发调人
    private String reciveuser; // 收调人
    private Integer status;     // 当前状态   0:未生效 1:待出车  2:返回    3: 取消调度
    private String startPoint;  //出发地点
    private String ways;         //行车路线
    private Integer peoplenum;  //乘车人数

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getSscd() {
        return sscd;
    }

    public void setSscd(String sscd) {
        this.sscd = sscd;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSenduser() {
        return senduser;
    }

    public void setSenduser(String senduser) {
        this.senduser = senduser;
    }

    public String getReciveuser() {
        return reciveuser;
    }

    public void setReciveuser(String reciveuser) {
        this.reciveuser = reciveuser;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getWays() {
        return ways;
    }

    public void setWays(String ways) {
        this.ways = ways;
    }

    public Integer getPeoplenum() {
        return peoplenum;
    }

    public void setPeoplenum(Integer peoplenum) {
        this.peoplenum = peoplenum;
    }

}
