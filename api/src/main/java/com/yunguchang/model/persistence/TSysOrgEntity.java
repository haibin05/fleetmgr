package com.yunguchang.model.persistence;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by gongy on 8/31/2015.
 *  机构信息表T_SYS_ORG
 */
@Entity
@Table(name = "t_sys_org")
public class TSysOrgEntity implements Serializable {
    @Id
    @Column(name = "ORGID")
    private String orgid;

    private String orgname;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="parentid")
    @NotFound(action = NotFoundAction.IGNORE)
    private TSysOrgEntity parent;

    private String customid;

    private Integer sortno;

    private String leaf;

    private String remark;

    private String enabled;

    private String model;

    private String orgtype;

    private String gsid;

    private String ddtj;




    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public TSysOrgEntity getParent() {
        return parent;
    }

    public void setParent(TSysOrgEntity parent) {
        this.parent = parent;
    }

    public String getCustomid() {
        return customid;
    }

    public void setCustomid(String customid) {
        this.customid = customid;
    }

    public Integer getSortno() {
        return sortno;
    }

    public void setSortno(Integer sortno) {
        this.sortno = sortno;
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

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOrgtype() {
        return orgtype;
    }

    public void setOrgtype(String orgtype) {
        this.orgtype = orgtype;
    }

    public String getGsid() {
        return gsid;
    }

    public void setGsid(String gsid) {
        this.gsid = gsid;
    }

    public String getDdtj() {
        return ddtj;
    }

    public void setDdtj(String ddtj) {
        this.ddtj = ddtj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TSysOrgEntity that = (TSysOrgEntity) o;

        if (orgid != null ? !orgid.equals(that.orgid) : that.orgid != null) return false;
        if (orgname != null ? !orgname.equals(that.orgname) : that.orgname != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (customid != null ? !customid.equals(that.customid) : that.customid != null) return false;
        if (sortno != null ? !sortno.equals(that.sortno) : that.sortno != null) return false;
        if (leaf != null ? !leaf.equals(that.leaf) : that.leaf != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (enabled != null ? !enabled.equals(that.enabled) : that.enabled != null) return false;
        if (model != null ? !model.equals(that.model) : that.model != null) return false;
        if (orgtype != null ? !orgtype.equals(that.orgtype) : that.orgtype != null) return false;
        if (gsid != null ? !gsid.equals(that.gsid) : that.gsid != null) return false;
        return !(ddtj != null ? !ddtj.equals(that.ddtj) : that.ddtj != null);

    }

    @Override
    public int hashCode() {
        int result = orgid != null ? orgid.hashCode() : 0;
        result = 31 * result + (orgname != null ? orgname.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (customid != null ? customid.hashCode() : 0);
        result = 31 * result + (sortno != null ? sortno.hashCode() : 0);
        result = 31 * result + (leaf != null ? leaf.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (orgtype != null ? orgtype.hashCode() : 0);
        result = 31 * result + (gsid != null ? gsid.hashCode() : 0);
        result = 31 * result + (ddtj != null ? ddtj.hashCode() : 0);
        return result;
    }
}
