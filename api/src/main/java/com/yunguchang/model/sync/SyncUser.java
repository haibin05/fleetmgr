package com.yunguchang.model.sync;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * Created by gongy on 2015/10/20.
 */
@JsonSerialize
public class SyncUser {
    private String id;          // UUID
    private String userNo;      // 用户编号
    private String password;   // 密码
    private String name;        // 名字
    private String deptid;      // BMID
    private String sex;         // 性别(0:未知;1:男;2:女)
    private String userType;   // 人员类型(0:管理员;1:公司领导;2:公司父总司;3:绩效管理员；4:公司员工;5:)
    private String mobile;      // 手机
    private String phone;       // 短号
    private String email;       // 邮箱
    private String enabled;     // 启用状态(1:启用;0:停用)
    private String remark;      // 备注

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 用户姓名
     *
     * @return
     */
    @DocumentationExample("周生生")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 部门Id
     *
     * @return
     */
    @DocumentationExample("0010701")
    public String getDeptid() {
        return deptid;
    }

    public void setDeptid(String deptid) {
        this.deptid = deptid;
    }

    /**
     * 联系方式
     *
     * @return
     */
    @DocumentationExample("13501001234")
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

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
