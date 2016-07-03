package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;

import java.util.Arrays;

/**
 * 调度单对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class Schedule {
    private String id;
    private Application[] applications=new Application[]{};
    private Record record;
    private ScheduleCar[] scheduleCars=new ScheduleCar[]{};
    private String startPoint;
    private String wayPoint;
    private DateTime start;
    private DateTime end;
    private User sender;
    private User receiver;
    private Integer status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 本次调度相关联的申请单.可以为多单
     *
     * @return 本次调度相关联的申请单.可以为多单
     */
    public Application[] getApplications() {
        return applications;
    }

    public void setApplications(Application[] applications) {
        this.applications = applications;
    }

    /**
     * 本次调度相关联的行车记录
     *
     * @return 本次调度相关联的行车记录
     */
    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public ScheduleCar[] getScheduleCars() {
        return scheduleCars;
    }

    public void setScheduleCars(ScheduleCar[] scheduleCars) {
        this.scheduleCars = scheduleCars;
    }

    /**
     * 调度单的出发点
     *
     * @return 调度单的出发点
     */
    @DocumentationExample("路灯公司")
    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    @DocumentationExample("市郊")
    public String getWayPoint() {
        return wayPoint;
    }

    public void setWayPoint(String wayPoint) {
        this.wayPoint = wayPoint;
    }


    /**
     * 调度单开始时间
     * @return
     */
    //@JsonFormat(shape = JsonFormat.Shape.STRING)
    //@DocumentationExample("2015-08-22, 12:05")
    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    /**
     * 调度单结束时间
     * @return
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    //@DocumentationExample("2015-08-22, 14:05")
    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }



    /**
     * 发调人
     * @return
     */
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * 收调人
     * @return
     */
    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "applications=" + Arrays.toString(applications) +
                ", record=" + record +
                ", startPoint='" + startPoint + '\'' +
                ", wayPoint='" + wayPoint + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
