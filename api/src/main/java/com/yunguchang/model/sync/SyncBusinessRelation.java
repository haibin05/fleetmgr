package com.yunguchang.model.sync;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

import java.util.List;

/**
 * 用车联系人
 */
@JsonSerialize
public class SyncBusinessRelation {
    private String uuid;
    private String userId;
    private String orgId;
    private List<String> businessOrgIds;
    private String model;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public List<String> getBusinessOrgIds() {
        return businessOrgIds;
    }

    public void setBusinessOrgIds(List<String> businessOrgIds) {
        this.businessOrgIds = businessOrgIds;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
