package com.yunguchang.model.sync;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * 驾驶员信息
 */
@JsonSerialize
public class SyncDriver {
    private String eid;
    private String name;
    private String sex;
    private String mobile;
    private String phone;       // 短号
    private String orgid;
    private String position;
    private String drivecartype; // 驾照类型
    private String driverType; // 驾驶员类型

    /**
     * 车队Guid
     */
    @DocumentationExample("72fb7a16e7d345fe9ff2594996232618")
    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    /**
     * 驾驶员姓名
     */
    @DocumentationExample("徐金华")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 驾驶员工号
     */
    @DocumentationExample("2038")
    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    /**
     * 驾驶员通讯方式
     */
    @DocumentationExample("13516736682")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 岗位
     */
    @DocumentationExample("一车队驾驶员")
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * 短号
     */
    @DocumentationExample("9988")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 驾照类型
     */
    @DocumentationExample("1")
    public String getDrivecartype() {
        return drivecartype;
    }

    public void setDrivecartype(String drivecartype) {
        this.drivecartype = drivecartype;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
