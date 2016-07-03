package com.yunguchang.model.persistence;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by WHB on 2015-09-20.
 * 考勤管理信息汇总表
 */
@Entity
@javax.persistence.Table(name = "t_rs_kqgl_list")
public class TRsKqglListEntity implements Serializable {

    // pk
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String uuid;

    // 驾驶员id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVERID")
    private TRsDriverinfoEntity driver;

    // 考勤日期：格式-年月

    private String kqDate;

    // 考勤类型

    private String kqType;

    // 请假天数  0：半天  1：一天

    private String dayType;

    // 备注

    private String remark;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public TRsDriverinfoEntity getDriver() {
        return driver;
    }

    public void setDriver(TRsDriverinfoEntity driver) {
        this.driver = driver;
    }

    public String getKqDate() {
        return kqDate;
    }

    public void setKqDate(String kqDate) {
        this.kqDate = kqDate;
    }

    public String getKqType() {
        return kqType;
    }

    public void setKqType(String kqType) {
        this.kqType = kqType;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TRsKqglListEntity that = (TRsKqglListEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (kqDate != null ? !kqDate.equals(that.kqDate) : that.kqDate != null) return false;
        if (kqType != null ? !kqType.equals(that.kqType) : that.kqType != null) return false;
        if (dayType != null ? !dayType.equals(that.dayType) : that.dayType != null) return false;
        return !(remark != null ? !remark.equals(that.remark) : that.remark != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (kqDate != null ? kqDate.hashCode() : 0);
        result = 31 * result + (kqType != null ? kqType.hashCode() : 0);
        result = 31 * result + (dayType != null ? dayType.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        return result;
    }
}
