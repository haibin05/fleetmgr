package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

/**
 * 车辆行驶记录(司机填写)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class Record {
    private String id;
    private DateTime start;
    private DateTime end;
    private Double startMile;
    private Double endMile;
    private Double transport;
    private Double lodging;
    private Double parking;
    private Double  fuel;
    private String comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 出厂时间
     * @return
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    /**
     * 回厂时间
     * @return
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    /**
     * 开始里程
     * @return
     */
    public Double getStartMile() {
        return startMile;
    }

    public void setStartMile(Double startMile) {
        this.startMile = startMile;
    }

    /**
     * 结束历程
     * @return
     */
    public Double getEndMile() {
        return endMile;
    }

    public void setEndMile(Double endMile) {
        this.endMile = endMile;
    }

    /**
     * 处境费，过桥费，通行费...
     * @return
     */
    public Double getTransport() {
        return transport;
    }

    public void setTransport(Double transport) {
        this.transport = transport;
    }

    /**
     * 住宿费
     * @return
     */
    public Double getLodging() {
        return lodging;
    }

    public void setLodging(Double lodging) {
        this.lodging = lodging;
    }

    /**
     * 停车费
     * @return
     */
    public Double getParking() {
        return parking;
    }

    public void setParking(Double parking) {
        this.parking = parking;
    }

    /**
     * 加油费，加气费
     * @return
     */
    public Double getFuel() {
        return fuel;
    }

    public void setFuel(Double fuel) {
        this.fuel = fuel;
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
}
