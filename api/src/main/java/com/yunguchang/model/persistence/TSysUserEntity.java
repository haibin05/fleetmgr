package com.yunguchang.model.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_sys_user",
        indexes = {
                @Index(name = "fk_q3e0wat36ngl7vohwow695u66", columnList = "deptid", unique = false),
        }
)
public class TSysUserEntity implements Serializable {
    @Id
    @Column(name = "USERID")
    private String userid;
    private String username;
    private String password;
    private String sex;
//    private String deptid;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="deptid")
    private TSysOrgEntity department;
    private String locked;
    private String usertype;
    private String enabled;
    private String mobile;
    private String phone;
    private String email;
    private String remark;
    private String userno;

    @ManyToMany
    @JoinTable(
            name="t_sys_user_role",
            uniqueConstraints={
                    @UniqueConstraint(columnNames = {"id"})
            },
            joinColumns={@JoinColumn(name="userid")},
            inverseJoinColumns={@JoinColumn(name="roleid")})
    private List<TSysRoleEntity> roles;


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<TSysRoleEntity> getRoles() {
        if(roles == null) {
            roles = new ArrayList<>();
        }
        return roles;
    }

    public void setRoles(List<TSysRoleEntity> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

//
//    public String getDeptid() {
//        return deptid;
//    }
//
//    public void setDeptid(String deptid) {
//        this.deptid = deptid;
//    }


    public TSysOrgEntity getDepartment() {
        return department;
    }

    public void setDepartment(TSysOrgEntity department) {
        this.department = department;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }


    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }


    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TSysUserEntity that = (TSysUserEntity) o;

        if (userid != null ? !userid.equals(that.userid) : that.userid != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (sex != null ? !sex.equals(that.sex) : that.sex != null) return false;
//        if (deptid != null ? !deptid.equals(that.deptid) : that.deptid != null) return false;
        if (department != null ? !department.equals(that.department) : that.department != null) return false;
        if (locked != null ? !locked.equals(that.locked) : that.locked != null) return false;
        if (usertype != null ? !usertype.equals(that.usertype) : that.usertype != null) return false;
        if (enabled != null ? !enabled.equals(that.enabled) : that.enabled != null) return false;
        if (mobile != null ? !mobile.equals(that.mobile) : that.mobile != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (userno != null ? !userno.equals(that.userno) : that.userno != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userid != null ? userid.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
//        result = 31 * result + (deptid != null ? deptid.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (locked != null ? locked.hashCode() : 0);
        result = 31 * result + (usertype != null ? usertype.hashCode() : 0);
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (userno != null ? userno.hashCode() : 0);
        return result;
    }
}
