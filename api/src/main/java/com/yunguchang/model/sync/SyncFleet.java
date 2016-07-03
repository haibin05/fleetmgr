package com.yunguchang.model.sync;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * 车队信息
 */
@JsonSerialize
public class SyncFleet {
    private String id;
    private String orgType;        // 车队部门的类型
    private String gsid;   // 所属大区id
    private String name;        // 车队名字
    private String parentid;    // 上级机构编号
    private String enable;    // 启用状态   启用状态(1:启用;0:停用)
    private String ddtj;    // DDTJ     部门类型：0：嘉兴       1：大区     2：总车队    3：业务单位     4：安质     5：人事      6：总调室        7：营销
    private String sortNo;    // 排序No
    private String remark;    // 备注

    /**
     * 车队ID
     * @return
     */
    @DocumentationExample("0010701")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 车队名称
     */
    @DocumentationExample("二车队")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 上级车队Guid
     */
    @DocumentationExample("89d6353323d74311be52825180a3305e")
    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentGuid) {
        this.parentid = parentGuid;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getGsid() {
        return gsid;
    }

    public void setGsid(String gsid) {
        this.gsid = gsid;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getDdtj() {
        return ddtj;
    }

    public void setDdtj(String ddtj) {
        this.ddtj = ddtj;
    }

    public String getSortNo() {
        return sortNo;
    }

    public void setSortNo(String sortNo) {
        this.sortNo = sortNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
