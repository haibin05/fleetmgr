package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

import java.util.Arrays;
import java.util.List;


/**
 * 用户
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class User {
    private String userId;          //
    private String userName;        // 用车人姓名
    private String mobile;          // 用车人手机号
    private String eid;             // 用车人ID
    private String orgId;           // 用车人部门ID
    private UserType userType=UserType.DRIVER;
    private String[] namePinyin;

    private String[] roles=new String[]{};

    public enum UserType {
        PASSENGER,          // 乘客
        COORDINATOR,       // 申请人
        AUDITOR,            // 审核人
        DISPATCHER,        // 调度员
        DRIVER,             // 驾驶员
        MANAGER;            // 领导（管理员）
    }

    public User(){
        //System.out.println();
    }
    public User(UserType userType){
        this.userType = userType;
    }
    /**
     * 用户id
     *
     * @return 用户id
     */
    @DocumentationExample("00180011")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 用户姓名
     *
     * @return 用户姓名
     */
    @DocumentationExample("朱生生")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 手机号码
     *
     * @return 手机号码
     */
    @DocumentationExample("1358600000")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String[] getRoles() {
        return roles;
    }

    @JsonIgnore
    public List<String> getRolesAsList() {
        return Arrays.asList(roles);
    }



    public void setRoles(String ... roles) {
        this.roles = roles;
    }

    public UserType getUserType() {
        return userType;
    }

    public boolean isPassenger() {
        return this.userType.equals(UserType.PASSENGER);
    }
    public boolean isAuditor() {
        return this.userType.equals(UserType.AUDITOR);
    }
    public boolean isCoordinator() {
        return this.userType.equals(UserType.COORDINATOR);
    }
    public boolean isDispatcher() {
        return this.userType.equals(UserType.DISPATCHER);
    }
    public boolean isDriver() {
        return this.userType.equals(UserType.DRIVER);
    }
    public boolean isManager() {
        return this.userType.equals(UserType.MANAGER);
    }

    /**
     * 工号
     * @return
     */
    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String[] getNamePinyin() {
        return namePinyin;
    }

    public void setNamePinyin(String[] namePinyin) {
        this.namePinyin = namePinyin;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
