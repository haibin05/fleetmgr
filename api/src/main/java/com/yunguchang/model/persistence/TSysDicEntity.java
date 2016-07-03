package com.yunguchang.model.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_sys_dic",
        indexes = {
                @Index(name = "index2", columnList = "CLASSCODE", unique = false),
                @Index(name = "index3", columnList = "DATACODE", unique = false),
                @Index(name = "index4", columnList = "CLASSCODE,DATACODE", unique = false),
        }
)
public class TSysDicEntity implements Serializable {

    @Id
    @Column(name = "UUID")
    private String uuid;
    private String classcode;
    private String classvalue;
    private String datacode;
    private String datavalue;
    private String parentid;
    private String parentname;
    private BigDecimal sortno;
    private String status;
    private String classflag;
    private String leaf;
    private String remark;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getClasscode() {
        return classcode;
    }

    public void setClasscode(String classcode) {
        this.classcode = classcode;
    }


    public String getClassvalue() {
        return classvalue;
    }

    public void setClassvalue(String classvalue) {
        this.classvalue = classvalue;
    }


    public String getDatacode() {
        return datacode;
    }

    public void setDatacode(String datacode) {
        this.datacode = datacode;
    }


    public String getDatavalue() {
        return datavalue;
    }

    public void setDatavalue(String datavalue) {
        this.datavalue = datavalue;
    }


    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }


    public String getParentname() {
        return parentname;
    }

    public void setParentname(String parentname) {
        this.parentname = parentname;
    }


    public BigDecimal getSortno() {
        return sortno;
    }

    public void setSortno(BigDecimal sortno) {
        this.sortno = sortno;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getClassflag() {
        return classflag;
    }

    public void setClassflag(String classflag) {
        this.classflag = classflag;
    }


    public String getLeaf() {
        return leaf;
    }

    public void setLeaf(String leaf) {
        this.leaf = leaf;
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

        TSysDicEntity that = (TSysDicEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (classcode != null ? !classcode.equals(that.classcode) : that.classcode != null) return false;
        if (classvalue != null ? !classvalue.equals(that.classvalue) : that.classvalue != null) return false;
        if (datacode != null ? !datacode.equals(that.datacode) : that.datacode != null) return false;
        if (datavalue != null ? !datavalue.equals(that.datavalue) : that.datavalue != null) return false;
        if (parentid != null ? !parentid.equals(that.parentid) : that.parentid != null) return false;
        if (parentname != null ? !parentname.equals(that.parentname) : that.parentname != null) return false;
        if (sortno != null ? !sortno.equals(that.sortno) : that.sortno != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (classflag != null ? !classflag.equals(that.classflag) : that.classflag != null) return false;
        if (leaf != null ? !leaf.equals(that.leaf) : that.leaf != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (classcode != null ? classcode.hashCode() : 0);
        result = 31 * result + (classvalue != null ? classvalue.hashCode() : 0);
        result = 31 * result + (datacode != null ? datacode.hashCode() : 0);
        result = 31 * result + (datavalue != null ? datavalue.hashCode() : 0);
        result = 31 * result + (parentid != null ? parentid.hashCode() : 0);
        result = 31 * result + (parentname != null ? parentname.hashCode() : 0);
        result = 31 * result + (sortno != null ? sortno.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (classflag != null ? classflag.hashCode() : 0);
        result = 31 * result + (leaf != null ? leaf.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        return result;
    }
}
