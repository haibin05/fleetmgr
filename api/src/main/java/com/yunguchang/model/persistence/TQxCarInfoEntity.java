package com.yunguchang.model.persistence;

import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 汽修系列 - 车辆信息
 * Created by 禕 on 2015/9/28.
 */
@Entity
@javax.persistence.Table(name = "t_qx_clxxinfo")
public class TQxCarInfoEntity implements Serializable {
    @Id
    @javax.persistence.Column(name = "GUID")
    private String guid;
    // 车牌号码
    private String cphm;
    // 车辆类型
    private String cllx;
    // 车辆型号
    private String clxh;
    // 驾驶员（外部）
    private String jsy;
    // 驾驶员电话
    private String jsydh;
    // 发动机号
    private String fdjh;
    // 底盘号
    private String dph;
    // 车辆识别码
    private String clsbh;
    // 备注
    private String remark;
    // 客户信息ID
    private String khxxid;
    // 自编号
    private String zbh;
    // 维修状态
    private String state;
    // 维修状态改变时间
    private DateTime stateChangeTime;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }

    public String getCllx() {
        return cllx;
    }

    public void setCllx(String cllx) {
        this.cllx = cllx;
    }

    public String getClxh() {
        return clxh;
    }

    public void setClxh(String clxh) {
        this.clxh = clxh;
    }

    public String getJsy() {
        return jsy;
    }

    public void setJsy(String jsy) {
        this.jsy = jsy;
    }

    public String getJsydh() {
        return jsydh;
    }

    public void setJsydh(String jsydh) {
        this.jsydh = jsydh;
    }

    public String getFdjh() {
        return fdjh;
    }

    public void setFdjh(String fdjh) {
        this.fdjh = fdjh;
    }

    public String getDph() {
        return dph;
    }

    public void setDph(String dph) {
        this.dph = dph;
    }

    public String getClsbh() {
        return clsbh;
    }

    public void setClsbh(String clsbh) {
        this.clsbh = clsbh;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getKhxxid() {
        return khxxid;
    }

    public void setKhxxid(String khxxid) {
        this.khxxid = khxxid;
    }

    public String getZbh() {
        return zbh;
    }

    public void setZbh(String zbh) {
        this.zbh = zbh;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public DateTime getStateChangeTime() {
        return stateChangeTime;
    }

    public void setStateChangeTime(DateTime stateChangeTime) {
        this.stateChangeTime = stateChangeTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TQxCarInfoEntity that = (TQxCarInfoEntity) o;

        if (guid != null ? !guid.equals(that.guid) : that.guid != null) return false;
        if (cphm != null ? !cphm.equals(that.cphm) : that.cphm != null) return false;
        if (cllx != null ? !cllx.equals(that.cllx) : that.cllx != null) return false;
        if (clxh != null ? !clxh.equals(that.clxh) : that.clxh != null) return false;
        if (jsy != null ? !jsy.equals(that.jsy) : that.jsy != null) return false;
        if (jsydh != null ? !jsydh.equals(that.jsydh) : that.jsydh != null) return false;
        if (fdjh != null ? !fdjh.equals(that.fdjh) : that.fdjh != null) return false;
        if (dph != null ? !dph.equals(that.dph) : that.dph != null) return false;
        if (clsbh != null ? !clsbh.equals(that.clsbh) : that.clsbh != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (khxxid != null ? !khxxid.equals(that.khxxid) : that.khxxid != null) return false;
        if (zbh != null ? !zbh.equals(that.zbh) : that.zbh != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        return !(stateChangeTime != null ? !stateChangeTime.equals(that.stateChangeTime) : that.stateChangeTime != null);

    }

    @Override
    public int hashCode() {
        int result = guid != null ? guid.hashCode() : 0;
        result = 31 * result + (cphm != null ? cphm.hashCode() : 0);
        result = 31 * result + (cllx != null ? cllx.hashCode() : 0);
        result = 31 * result + (clxh != null ? clxh.hashCode() : 0);
        result = 31 * result + (jsy != null ? jsy.hashCode() : 0);
        result = 31 * result + (jsydh != null ? jsydh.hashCode() : 0);
        result = 31 * result + (fdjh != null ? fdjh.hashCode() : 0);
        result = 31 * result + (dph != null ? dph.hashCode() : 0);
        result = 31 * result + (clsbh != null ? clsbh.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (khxxid != null ? khxxid.hashCode() : 0);
        result = 31 * result + (zbh != null ? zbh.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (stateChangeTime != null ? stateChangeTime.hashCode() : 0);
        return result;
    }
}
