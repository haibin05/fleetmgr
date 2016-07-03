package com.yunguchang.model.sync;

import java.util.Date;

public class ScheduleApplyInfoForSync {
    private String uuId;                // 申请单UUID
    private String applyNo;            // 申请单NO
    private String userId;             // 申请者
    private String orgId;               // 部门
    private String startPoint;          // 起始点
    private String ways;                // 终止点
    private String cargodes;           // 货物描述
    private String reason;              // 原因-事宜
    private String remark;              // 备注
    private Date beginTime;             // 计划开始日期
    private Date endTime;               // 计划结束日期
    private Date applyDate;             // 申请日期
    private String isGoods;             // 是否有货
    private String sendUser;            // 车队长/片队
    private String sscd;                // 车队
    private String relaNo;              // 分配编号
    private Integer peopleNum;          // 人数
    private String personId;            // 用车人
    private String personName;         // 用车人姓名
    private String personMobile;       // 用车人手机
    private String phone;               //用车人短号
    private String orgName;             //部门名字
    private String status;              // 申请单状态
    private String isSend = "0";         // 车队
    private String usetype;                // 车队

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
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

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonMobile() {
        return personMobile;
    }

    public void setPersonMobile(String personMobile) {
        this.personMobile = personMobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(Integer peopleNum) {
        this.peopleNum = peopleNum;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getWays() {
        return ways;
    }

    public void setWays(String ways) {
        this.ways = ways;
    }

    public String getCargodes() {
        return cargodes;
    }

    public void setCargodes(String cargodes) {
        this.cargodes = cargodes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getIsGoods() {
        return isGoods;
    }

    public void setIsGoods(String isGoods) {
        this.isGoods = isGoods;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public String getRelaNo() {
        return relaNo;
    }

    public void setRelaNo(String relaNo) {
        this.relaNo = relaNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSscd() {
        return sscd;
    }

    public void setSscd(String sscd) {
        this.sscd = sscd;
    }

    public String getIsSend() {
        return isSend;
    }

    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }

    public String getUsetype() {
        return usetype;
    }

    public void setUsetype(String usetype) {
        this.usetype = usetype;
    }
}
