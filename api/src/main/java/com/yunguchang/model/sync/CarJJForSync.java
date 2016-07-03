package com.yunguchang.model.sync;

public class CarJJForSync {

    private String id;
    private String cphm;        // 车牌号-
    private String sscd;        // 所属车队-
    private String jsy;         // 驾驶员-
    private String cllx;        // 车辆类型-CarType
    private String clzt;        // 车辆状态-
    private String gpsazqk;     // GPS安装情况-1：是；0：否
    private String status;      // 状态，默认为1，逻辑删除时修改为0
    private String insertuser;  // 创建人
    private String insertorg;   // 创建部门

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }

    public String getSscd() {
        return sscd;
    }

    public void setSscd(String sscd) {
        this.sscd = sscd;
    }

    public String getJsy() {
        return jsy;
    }

    public void setJsy(String jsy) {
        this.jsy = jsy;
    }

    public String getCllx() {
        return cllx;
    }

    public void setCllx(String cllx) {
        this.cllx = cllx;
    }

    public String getClzt() {
        return clzt;
    }

    public void setClzt(String clzt) {
        this.clzt = clzt;
    }


    public String getGpsazqk() {
        return gpsazqk;
    }

    public void setGpsazqk(String gpsazqk) {
        this.gpsazqk = gpsazqk;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInsertuser() {
        return insertuser;
    }

    public void setInsertuser(String insertuser) {
        this.insertuser = insertuser;
    }

    public String getInsertorg() {
        return insertorg;
    }

    public void setInsertorg(String insertorg) {
        this.insertorg = insertorg;
    }
}
