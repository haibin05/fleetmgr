package com.yunguchang.model.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_sys_role")
public class TSysRoleEntity implements Serializable {
    @Id
    @Column(name = "ROLEID")
    private String roleid;
    private String rolename;
    private String deptid;
    private String roletype;
    private String remark;
    private String locked;

    @ManyToMany(mappedBy="roles")
    private List<TSysUserEntity> users;



    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }


    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }


    public String getDeptid() {
        return deptid;
    }

    public void setDeptid(String deptid) {
        this.deptid = deptid;
    }


    public String getRoletype() {
        return roletype;
    }

    public void setRoletype(String roletype) {
        this.roletype = roletype;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }


    public List<TSysUserEntity> getUsers() {
        if(users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    public void setUsers(List<TSysUserEntity> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TSysRoleEntity that = (TSysRoleEntity) o;

        if (roleid != null ? !roleid.equals(that.roleid) : that.roleid != null) return false;
        if (rolename != null ? !rolename.equals(that.rolename) : that.rolename != null) return false;
        if (deptid != null ? !deptid.equals(that.deptid) : that.deptid != null) return false;
        if (roletype != null ? !roletype.equals(that.roletype) : that.roletype != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (locked != null ? !locked.equals(that.locked) : that.locked != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roleid != null ? roleid.hashCode() : 0;
        result = 31 * result + (rolename != null ? rolename.hashCode() : 0);
        result = 31 * result + (deptid != null ? deptid.hashCode() : 0);
        result = 31 * result + (roletype != null ? roletype.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (locked != null ? locked.hashCode() : 0);
        return result;
    }
}
