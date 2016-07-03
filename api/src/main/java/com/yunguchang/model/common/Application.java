package com.yunguchang.model.common;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;


/**
 * Created by gongy on 8/20/2015.
 */
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application {


    private String id;

    private String applyNo;

    private String orgId;

    @NotNull(message = "申请人不能为空")
    private User coordinator;

    private User dispatcher;

    @NotNull(message = "乘车人不能为空")
    private User passenger;

    private Schedule schedule;

    @NotNull(message = "起点不能为空")
    private String origin;

    @NotNull(message = "终点不能为空")
    private String destination;

    @NotNull(message = "乘客数量不能为空")
    private Integer passengers;

    @NotNull(message = "申请理由不能为空")
    private KeyValue reason;

    private String reasonMs;

    private KeyValue status;

    private String cargoes;

    private String comment;

    @NotNull(message = "开始时间不能为空")
    private DateTime start;
    @NotNull(message = "结束时间不能为空")
    private DateTime end;

    private Boolean regular;

    private String irregularReason;


    private DateTime creationTime;

    private String relaNo;

    private String isSend;

    private String userType;

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public Boolean getRegular() {
        return regular;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    /**
     * 申请人
     *
     * @return
     */
    public User getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(User coordinator) {
        this.coordinator = coordinator;
    }

    public User getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(User dispatcher) {
        this.dispatcher = dispatcher;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * 是否规范申请
     * @return
     */
    public Boolean isRegular() {
        return regular;
    }

    public void setRegular(Boolean regular) {
        this.regular = regular;
    }



    /**
     * 不规范理由
     * @return
     */
    public String getIrregularReason() {
        return irregularReason;
    }

    public void setIrregularReason(String irregularReason) {
        this.irregularReason = irregularReason;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * 起点
     * @return
     */
    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * 终点
     * @return
     */
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * 乘客数量
     * @return
     */
    public Integer getPassengers() {
        return passengers;
    }

    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
    }

    /**
     * 用车事由.从api/enums/ReasonType 接口获取
     * @return
     */
    public KeyValue getReason() {
        return reason;
    }

    public void setReason(KeyValue reason) {
        this.reason = reason;
    }

    /**
     * 承载货物
     * @return
     */
    public String getCargoes() {
        return cargoes;
    }

    public void setCargoes(String cargoes) {
        this.cargoes = cargoes;
    }

    /**
     * 注释
     * @return
     */
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 用车人（乘客）
     * @return
     */
    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    /**
     * 申请记录id
     * @return
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 申请状态.从 api/enums/ApplyStatus 接口获取
     * @return
     */
    public KeyValue getStatus() {
        return status;
    }

    public void setStatus(KeyValue status) {
        this.status = status;
    }


    public DateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(DateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getRelaNo() {
        return relaNo;
    }

    public void setRelaNo(String relaNo) {
        this.relaNo = relaNo;
    }

    public String getReasonMs() {
        return reasonMs;
    }

    public void setReasonMs(String reasonMs) {
        this.reasonMs = reasonMs;
    }

    public String getIsSend() {
        return isSend;
    }

    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
