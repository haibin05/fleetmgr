package com.yunguchang.model.sync;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * 用车联系人
 */
@JsonSerialize
public class SyncMainUser {
    private String mainUserId; // 用车负责人ID
    private String name;        // 用车负责人姓名
    private String mobile;      // 用车负责人手机号
    private String phone;       // 用车负责人短号
    private String belongGroup;// 所属班组
    private String bmid;        // 部门ID

    /**
     * 用车人ID
     * @return
     */
    @DocumentationExample("1200518")
    public String getMainUserId() {
        return mainUserId;
    }

    public void setMainUserId(String mainUserId) {
        this.mainUserId = mainUserId;
    }

    /**
     * 用车人姓名
     */
    @DocumentationExample("张生生")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 用车人联系方式
     * @return
     */
    @DocumentationExample("13501001234")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 用车人部门Id
     * @return
     */
    @DocumentationExample("0010701")
    public String getBmid() {
        return bmid;
    }

    public void setBmid(String bmid) {
        this.bmid = bmid;
    }

    /**
     * 用车负责人短号
     * @return
     */
    @DocumentationExample("8812")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBelongGroup() {
        return belongGroup;
    }

    /**
     * 所属班组
     * @return
     */
    @DocumentationExample("变电项目二部")
    public void setBelongGroup(String belongGroup) {
        this.belongGroup = belongGroup;
    }
}
