package com.yunguchang.model.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * 车辆行驶记录(司机填写)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class SyncRecord {
    private String id;
    private String schedule;        // 调度单
    private String car;            // 车辆编号
    private String driver;           // 驾驶员编号
    private DateTime starttime;     // 实际开始时间
    private DateTime endtime;       // 实际结束时间
    private Double startmile;       // 开始里程表
    private Double endmile;         // 结束里程表
    private Double mileage;         // 里程数
    private Double trancost;        // 过境费
    private Double staycost;        // 住宿费
    private Double stopcost;        // 停车费
    private Double refuelcost;      // 现金加油费
    private String remark;              // 备注
    private String operateuser;         // 操作人
    private DateTime operatedate;       // 操作日期
    private DateTime updatedate;        // 更新日期
    private String relano;            // 调度分配编号
    private Double costsum;            // 全部费用

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Date getStarttime() {
        if(starttime == null) {
            return null;
        }
        return starttime.toDate();
    }

    public void setStarttime(DateTime starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        if(endtime == null) {
            return null;
        }
        return endtime.toDate();
    }

    public void setEndtime(DateTime endtime) {
        this.endtime = endtime;
    }

    public Double getStartmile() {
        return startmile;
    }

    public void setStartmile(Double startmile) {
        this.startmile = startmile;
    }

    public Double getEndmile() {
        return endmile;
    }

    public void setEndmile(Double endmile) {
        this.endmile = endmile;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public Double getTrancost() {
        return trancost;
    }

    public void setTrancost(Double trancost) {
        this.trancost = trancost;
    }

    public Double getStaycost() {
        return staycost;
    }

    public void setStaycost(Double staycost) {
        this.staycost = staycost;
    }

    public Double getStopcost() {
        return stopcost;
    }

    public void setStopcost(Double stopcost) {
        this.stopcost = stopcost;
    }

    public Double getRefuelcost() {
        return refuelcost;
    }

    public void setRefuelcost(Double refuelcost) {
        this.refuelcost = refuelcost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperateuser() {
        return operateuser;
    }

    public void setOperateuser(String operateuser) {
        this.operateuser = operateuser;
    }

    public Date getOperatedate() {
        return operatedate.toDate();
    }

    public void setOperatedate(DateTime operatedate) {
        this.operatedate = operatedate;
    }

    public Date getUpdatedate() {
        return updatedate.toDate();
    }

    public void setUpdatedate(DateTime updatedate) {
        this.updatedate = updatedate;
    }

    public String getRelano() {
        return relano;
    }

    public void setRelano(String relano) {
        this.relano = relano;
    }

    public Double getCostsum() {
        return costsum;
    }

    public void setCostsum(Double costsum) {
        this.costsum = costsum;
    }
}
