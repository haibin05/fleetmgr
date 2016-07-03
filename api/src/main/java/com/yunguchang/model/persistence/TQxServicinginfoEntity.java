package com.yunguchang.model.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by 禕 on 2015/9/28.
 */
@Entity
@javax.persistence.Table(name = "t_qx_servicinginfo",
        indexes = {
                @Index(name = "index2", columnList = "CLXXID"),
                @Index(name = "index3", columnList = "ZT"),
        }
)
public class TQxServicinginfoEntity implements Serializable {
    @Id
    @javax.persistence.Column(name = "UUID")
    private String guid;
    private String wxdh;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="CLXXID")
    private TAzCarinfoEntity car;

    private String khxxid;
    private String zjy;
    private String jdy;
    private String ddy;
    private String jcsj;
    private BigDecimal lcs;
    private String zt;
    private String remark;
    private String zbh;
    private String jcy;
    private String hgzh;
    private String jsdh;
    private String cphm;
    private String xz;
    private String cdzqrsj;
    private String status;
    private String jhfl;
    private String stoptime;
    private String restarttime;
    private String stopuser;
    private Timestamp gdrq;
    private String wxjb;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getWxdh() {
        return wxdh;
    }

    public void setWxdh(String wxdh) {
        this.wxdh = wxdh;
    }

    public TAzCarinfoEntity getCar() {
        return car;
    }

    public void setCar(TAzCarinfoEntity car) {
        this.car = car;
    }

    public String getKhxxid() {
        return khxxid;
    }

    public void setKhxxid(String khxxid) {
        this.khxxid = khxxid;
    }

    public String getZjy() {
        return zjy;
    }

    public void setZjy(String zjy) {
        this.zjy = zjy;
    }

    public String getJdy() {
        return jdy;
    }

    public void setJdy(String jdy) {
        this.jdy = jdy;
    }

    public String getDdy() {
        return ddy;
    }

    public void setDdy(String ddy) {
        this.ddy = ddy;
    }

    public String getJcsj() {
        return jcsj;
    }

    public void setJcsj(String jcsj) {
        this.jcsj = jcsj;
    }

    public BigDecimal getLcs() {
        return lcs;
    }

    public void setLcs(BigDecimal lcs) {
        this.lcs = lcs;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getZbh() {
        return zbh;
    }

    public void setZbh(String zbh) {
        this.zbh = zbh;
    }

    public String getJcy() {
        return jcy;
    }

    public void setJcy(String jcy) {
        this.jcy = jcy;
    }

    public String getHgzh() {
        return hgzh;
    }

    public void setHgzh(String hgzh) {
        this.hgzh = hgzh;
    }

    public String getJsdh() {
        return jsdh;
    }

    public void setJsdh(String jsdh) {
        this.jsdh = jsdh;
    }

    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }

    public String getXz() {
        return xz;
    }

    public void setXz(String xz) {
        this.xz = xz;
    }

    public String getCdzqrsj() {
        return cdzqrsj;
    }

    public void setCdzqrsj(String cdzqrsj) {
        this.cdzqrsj = cdzqrsj;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJhfl() {
        return jhfl;
    }

    public void setJhfl(String jhfl) {
        this.jhfl = jhfl;
    }

    public String getStoptime() {
        return stoptime;
    }

    public void setStoptime(String stoptime) {
        this.stoptime = stoptime;
    }

    public String getRestarttime() {
        return restarttime;
    }

    public void setRestarttime(String restarttime) {
        this.restarttime = restarttime;
    }

    public String getStopuser() {
        return stopuser;
    }

    public void setStopuser(String stopuser) {
        this.stopuser = stopuser;
    }

    public Timestamp getGdrq() {
        return gdrq;
    }

    public void setGdrq(Timestamp gdrq) {
        this.gdrq = gdrq;
    }

    public String getWxjb() {
        return wxjb;
    }

    public void setWxjb(String wxjb) {
        this.wxjb = wxjb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TQxServicinginfoEntity that = (TQxServicinginfoEntity) o;

        if (guid != null ? !guid.equals(that.guid) : that.guid != null) return false;
        if (wxdh != null ? !wxdh.equals(that.wxdh) : that.wxdh != null) return false;
        if (khxxid != null ? !khxxid.equals(that.khxxid) : that.khxxid != null) return false;
        if (zjy != null ? !zjy.equals(that.zjy) : that.zjy != null) return false;
        if (jdy != null ? !jdy.equals(that.jdy) : that.jdy != null) return false;
        if (ddy != null ? !ddy.equals(that.ddy) : that.ddy != null) return false;
        if (jcsj != null ? !jcsj.equals(that.jcsj) : that.jcsj != null) return false;
        if (lcs != null ? !lcs.equals(that.lcs) : that.lcs != null) return false;
        if (zt != null ? !zt.equals(that.zt) : that.zt != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (zbh != null ? !zbh.equals(that.zbh) : that.zbh != null) return false;
        if (jcy != null ? !jcy.equals(that.jcy) : that.jcy != null) return false;
        if (hgzh != null ? !hgzh.equals(that.hgzh) : that.hgzh != null) return false;
        if (jsdh != null ? !jsdh.equals(that.jsdh) : that.jsdh != null) return false;
        if (cphm != null ? !cphm.equals(that.cphm) : that.cphm != null) return false;
        if (xz != null ? !xz.equals(that.xz) : that.xz != null) return false;
        if (cdzqrsj != null ? !cdzqrsj.equals(that.cdzqrsj) : that.cdzqrsj != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (jhfl != null ? !jhfl.equals(that.jhfl) : that.jhfl != null) return false;
        if (stoptime != null ? !stoptime.equals(that.stoptime) : that.stoptime != null) return false;
        if (restarttime != null ? !restarttime.equals(that.restarttime) : that.restarttime != null) return false;
        if (stopuser != null ? !stopuser.equals(that.stopuser) : that.stopuser != null) return false;
        if (gdrq != null ? !gdrq.equals(that.gdrq) : that.gdrq != null) return false;
        return !(wxjb != null ? !wxjb.equals(that.wxjb) : that.wxjb != null);

    }

    @Override
    public int hashCode() {
        int result = guid != null ? guid.hashCode() : 0;
        result = 31 * result + (wxdh != null ? wxdh.hashCode() : 0);
        result = 31 * result + (khxxid != null ? khxxid.hashCode() : 0);
        result = 31 * result + (zjy != null ? zjy.hashCode() : 0);
        result = 31 * result + (jdy != null ? jdy.hashCode() : 0);
        result = 31 * result + (ddy != null ? ddy.hashCode() : 0);
        result = 31 * result + (jcsj != null ? jcsj.hashCode() : 0);
        result = 31 * result + (lcs != null ? lcs.hashCode() : 0);
        result = 31 * result + (zt != null ? zt.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (zbh != null ? zbh.hashCode() : 0);
        result = 31 * result + (jcy != null ? jcy.hashCode() : 0);
        result = 31 * result + (hgzh != null ? hgzh.hashCode() : 0);
        result = 31 * result + (jsdh != null ? jsdh.hashCode() : 0);
        result = 31 * result + (cphm != null ? cphm.hashCode() : 0);
        result = 31 * result + (xz != null ? xz.hashCode() : 0);
        result = 31 * result + (cdzqrsj != null ? cdzqrsj.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (jhfl != null ? jhfl.hashCode() : 0);
        result = 31 * result + (stoptime != null ? stoptime.hashCode() : 0);
        result = 31 * result + (restarttime != null ? restarttime.hashCode() : 0);
        result = 31 * result + (stopuser != null ? stopuser.hashCode() : 0);
        result = 31 * result + (gdrq != null ? gdrq.hashCode() : 0);
        result = 31 * result + (wxjb != null ? wxjb.hashCode() : 0);
        return result;
    }
}
