package com.yunguchang.model.persistence;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Where;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by gongy on 9/27/2015.
 */
@Entity
@javax.persistence.Table(name = "t_az_wzxx",
        indexes = {
                @Index(name = "FK_7hvw0iw91e9wfjt3769wri640", columnList = "clid", unique = false),
        }
)
@Where(clause="STATUS='1'")

/**
 * 违章情况
 */
public class TAzWzxxEntity implements Serializable {
    @Id
    @javax.persistence.Column(name = "ID")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLID")
    private TAzCarinfoEntity car;
    //private String cphm;
    //private String sscd;
    //private String hpzl;
    private DateTime wfrq;
    private String wfdd;
    private String wfxw;
    private String clzt;
    private String cjjg;
    private String insertuser;
    private String insertorg;
    private DateTime insertdate;
    private String updateuser;
    private String updateorg;
    private DateTime updatedate;
    private String status;
    private String tq;
    private String bz;
    private String jsy;
    private DateTime clsj;
    private String wzlx;
    private Double khdf;
    private String jsyid;
    private String sscdid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public TAzCarinfoEntity getCar() {
        return car;
    }

    public void setCar(TAzCarinfoEntity car) {
        this.car = car;
    }

    public void setWfrq(DateTime wfrq) {
        this.wfrq = wfrq;
    }

    public void setInsertdate(DateTime insertdate) {
        this.insertdate = insertdate;
    }

    public void setUpdatedate(DateTime updatedate) {
        this.updatedate = updatedate;
    }

    public void setClsj(DateTime clsj) {
        this.clsj = clsj;
    }

    public void setKhdf(Double khdf) {
        this.khdf = khdf;
    }



    public String getWfdd() {
        return wfdd;
    }

    public void setWfdd(String wfdd) {
        this.wfdd = wfdd;
    }

    public String getWfxw() {
        return wfxw;
    }

    public void setWfxw(String wfxw) {
        this.wfxw = wfxw;
    }

    public String getClzt() {
        return clzt;
    }

    public void setClzt(String clzt) {
        this.clzt = clzt;
    }

    public String getCjjg() {
        return cjjg;
    }

    public void setCjjg(String cjjg) {
        this.cjjg = cjjg;
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



    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }

    public String getUpdateorg() {
        return updateorg;
    }

    public void setUpdateorg(String updateorg) {
        this.updateorg = updateorg;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTq() {
        return tq;
    }

    public void setTq(String tq) {
        this.tq = tq;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getJsy() {
        return jsy;
    }

    public void setJsy(String jsy) {
        this.jsy = jsy;
    }



    public String getWzlx() {
        return wzlx;
    }

    public void setWzlx(String wzlx) {
        this.wzlx = wzlx;
    }



    public String getJsyid() {
        return jsyid;
    }

    public void setJsyid(String jsyid) {
        this.jsyid = jsyid;
    }

    public String getSscdid() {
        return sscdid;
    }

    public void setSscdid(String sscdid) {
        this.sscdid = sscdid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TAzWzxxEntity that = (TAzWzxxEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (car != null ? !car.equals(that.car) : that.car != null) return false;
        if (wfrq != null ? !wfrq.equals(that.wfrq) : that.wfrq != null) return false;
        if (wfdd != null ? !wfdd.equals(that.wfdd) : that.wfdd != null) return false;
        if (wfxw != null ? !wfxw.equals(that.wfxw) : that.wfxw != null) return false;
        if (clzt != null ? !clzt.equals(that.clzt) : that.clzt != null) return false;
        if (cjjg != null ? !cjjg.equals(that.cjjg) : that.cjjg != null) return false;
        if (insertuser != null ? !insertuser.equals(that.insertuser) : that.insertuser != null) return false;
        if (insertorg != null ? !insertorg.equals(that.insertorg) : that.insertorg != null) return false;
        if (insertdate != null ? !insertdate.equals(that.insertdate) : that.insertdate != null) return false;
        if (updateuser != null ? !updateuser.equals(that.updateuser) : that.updateuser != null) return false;
        if (updateorg != null ? !updateorg.equals(that.updateorg) : that.updateorg != null) return false;
        if (updatedate != null ? !updatedate.equals(that.updatedate) : that.updatedate != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (tq != null ? !tq.equals(that.tq) : that.tq != null) return false;
        if (bz != null ? !bz.equals(that.bz) : that.bz != null) return false;
        if (jsy != null ? !jsy.equals(that.jsy) : that.jsy != null) return false;
        if (clsj != null ? !clsj.equals(that.clsj) : that.clsj != null) return false;
        if (wzlx != null ? !wzlx.equals(that.wzlx) : that.wzlx != null) return false;
        if (khdf != null ? !khdf.equals(that.khdf) : that.khdf != null) return false;
        if (jsyid != null ? !jsyid.equals(that.jsyid) : that.jsyid != null) return false;
        return !(sscdid != null ? !sscdid.equals(that.sscdid) : that.sscdid != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (car != null ? car.hashCode() : 0);
        result = 31 * result + (wfrq != null ? wfrq.hashCode() : 0);
        result = 31 * result + (wfdd != null ? wfdd.hashCode() : 0);
        result = 31 * result + (wfxw != null ? wfxw.hashCode() : 0);
        result = 31 * result + (clzt != null ? clzt.hashCode() : 0);
        result = 31 * result + (cjjg != null ? cjjg.hashCode() : 0);
        result = 31 * result + (insertuser != null ? insertuser.hashCode() : 0);
        result = 31 * result + (insertorg != null ? insertorg.hashCode() : 0);
        result = 31 * result + (insertdate != null ? insertdate.hashCode() : 0);
        result = 31 * result + (updateuser != null ? updateuser.hashCode() : 0);
        result = 31 * result + (updateorg != null ? updateorg.hashCode() : 0);
        result = 31 * result + (updatedate != null ? updatedate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (tq != null ? tq.hashCode() : 0);
        result = 31 * result + (bz != null ? bz.hashCode() : 0);
        result = 31 * result + (jsy != null ? jsy.hashCode() : 0);
        result = 31 * result + (clsj != null ? clsj.hashCode() : 0);
        result = 31 * result + (wzlx != null ? wzlx.hashCode() : 0);
        result = 31 * result + (khdf != null ? khdf.hashCode() : 0);
        result = 31 * result + (jsyid != null ? jsyid.hashCode() : 0);
        result = 31 * result + (sscdid != null ? sscdid.hashCode() : 0);
        return result;
    }
}
