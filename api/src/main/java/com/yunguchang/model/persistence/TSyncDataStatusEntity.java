package com.yunguchang.model.persistence;

import com.webcohesion.enunciate.metadata.DocumentationExample;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_sync_status")
public class TSyncDataStatusEntity implements Serializable {
    @Id
    private String uuid;

    // 原始数据
    @DocumentationExample("RYBG#0010701#仇智伟#1065720# #15068367920")
    @Column(length = 4000)
    private String dataValue;

    // 数据同步状态 0:失败, 1:成功
    @DocumentationExample("1")
    private String state = "1";

    // 数据类型
    private SyncDataType dataType = SyncDataType.INPUT;

    private int flowStep = 0;

    private boolean deleteFlg = false;

    private int tryTimes = 0;
    private int pingTimes = 0;

    @Column(length = 300)
    private String exceptionMessage;

    private Timestamp insertdate;
    private Timestamp updatedate;
    // 向外同步数据
    private boolean toOut = false;

    private boolean sendMessage = false;

    private Timestamp lastFetchTime = new Timestamp(DateTime.now().getMillis());

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getTryTimes() {
        return tryTimes;
    }

    public boolean isDeleteFlg() {
        return deleteFlg;
    }

    public void setDeleteFlg(boolean deleteFlg) {
        this.deleteFlg = deleteFlg;
    }

    public void addTryTimes() {
        this.tryTimes++;
    }

    public void addPingTimes() {
        this.lastFetchTime = new Timestamp(DateTime.now().getMillis());
        this.pingTimes++;
    }

    public void setTryTimes(int tryTimes) {
        this.tryTimes = tryTimes;
    }

    public int getPingTimes() {
        return pingTimes;
    }

    public void setPingTimes(int pingTimes) {
        this.pingTimes = pingTimes;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        if(exceptionMessage != null && "0".equals(this.state)) {
            this.exceptionMessage = exceptionMessage.substring(0, exceptionMessage.length() > 300 ? 300 : exceptionMessage.length());
        } else {
            this.exceptionMessage = null;
        }
    }

    public Timestamp getInsertdate() {
        return insertdate;
    }

    public void setInsertdate(Timestamp insertdate) {
        this.insertdate = insertdate;
    }

    public Timestamp getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Timestamp updatedate) {
        this.updatedate = updatedate;
    }

    public boolean isToOut() {
        return toOut;
    }

    public void setToOut(boolean toOut) {
        this.toOut = toOut;
    }

    public SyncDataType getDataType() {
        return dataType;
    }

    public void setDataType(SyncDataType dataType) {
        this.dataType = dataType;
    }

    public Timestamp getLastFetchTime() {
        return lastFetchTime;
    }

    public void setLastFetchTime(Timestamp lastFetchTime) {
        this.lastFetchTime = lastFetchTime;
    }

    public boolean isSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(boolean sendMessage) {
        this.sendMessage = sendMessage;
    }

    public int getFlowStep() {
        return flowStep;
    }

    public void setFlowStep(int flowStep) {
        this.flowStep = flowStep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TSyncDataStatusEntity that = (TSyncDataStatusEntity) o;

        if (deleteFlg != that.deleteFlg) return false;
        if (tryTimes != that.tryTimes) return false;
        if (pingTimes != that.pingTimes) return false;
        if (toOut != that.toOut) return false;
        if (sendMessage != that.sendMessage) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (dataValue != null ? !dataValue.equals(that.dataValue) : that.dataValue != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (dataType != that.dataType) return false;
        if (exceptionMessage != null ? !exceptionMessage.equals(that.exceptionMessage) : that.exceptionMessage != null) return false;
        if (insertdate != null ? !insertdate.equals(that.insertdate) : that.insertdate != null) return false;
        if (updatedate != null ? !updatedate.equals(that.updatedate) : that.updatedate != null) return false;
        return lastFetchTime != null ? lastFetchTime.equals(that.lastFetchTime) : that.lastFetchTime == null;

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (dataValue != null ? dataValue.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (deleteFlg ? 1 : 0);
        result = 31 * result + tryTimes;
        result = 31 * result + pingTimes;
        result = 31 * result + (exceptionMessage != null ? exceptionMessage.hashCode() : 0);
        result = 31 * result + (insertdate != null ? insertdate.hashCode() : 0);
        result = 31 * result + (updatedate != null ? updatedate.hashCode() : 0);
        result = 31 * result + (toOut ? 1 : 0);
        result = 31 * result + (sendMessage ? 1 : 0);
        result = 31 * result + (lastFetchTime != null ? lastFetchTime.hashCode() : 0);
        return result;
    }

    public enum  SyncDataType {
        APPLY,          // 申请
        APPLY_CANCELED,          // 申请
        APPROVE,        // 审批
        COORDINATE,     // 协调
        SCHEDULE,       // 调度
        EVALUATE,       // 评价
        EVALUATE_APPLY,       // 评价
        RECORD,         // 行车记录
        REJECTED,         // 被拒绝的申请单
        APPLY_DELETE,       // 取消的申请单
        SCHEDULE_ADD_APPLY,       // 取消的申请单
        DELETED,        // 删除的调度
        UPDATED,        // 更新的调度
        DELETE_CAR,     // 调度删除车辆
        INPUT,          // 数据从内网进入外网
        OUTPUT_ALL,         // 数据从外网进入内网
    }
}
