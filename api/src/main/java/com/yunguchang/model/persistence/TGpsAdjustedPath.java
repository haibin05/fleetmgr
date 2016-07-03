package com.yunguchang.model.persistence;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 根据百度地图修正的行车路径
 */
@Entity
@javax.persistence.Table(name = "t_gps_adjusted_path",
        indexes = {
                @Index(name = "FK_cqry4t0kfhfy25yd1gtmu7dxp", columnList = "CARID"),
                @Index(name = "idx_start", columnList = "start"),
                @Index(name = "idx_end", columnList = "end"),
        }
)
public class TGpsAdjustedPath implements Serializable {
    /**
     * 主键Mysql自增加字段
     *
     * @return
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * 关联的车辆记录
     *
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARID")
    private TAzCarinfoEntity car;
    /**
     * 该路径起始时间
     * @return
     */
    private DateTime start;
    /**
     * 该路径结束时间
     * @return
     */
    private DateTime end;
    /**
     * 路线坐标，格式为 纬度,经度;纬度,经度;..
     * @return
     */
    @Lob
    private String path;

    /**
     * 起点城市
     */

    private String originCity;

    /**
     * 终点城市化
     */

    private String destinationCity;

    /**
     * 车辆速度，已起点速度为准
     */

    private Double speed;

    /**
     * 起点纬度
     */

    private Double gpsOriginLat;

    /**
     * 起点经度
     */

    private Double gpsOriginLng;

    /**
     * 终点纬度
     */

    private Double gpsDestinationLat;

    /**
     * 终点经度
     */

    private Double gpsDestinationLng;

    private DateTime gpsOriginSampleTime;

    private DateTime gpsDestinationSampleTime;

    /**
     * 线路状态
     * 0|Null，有效路径，会作为修正路径返回前台
     * 1， 无效路径， 将作进一步修正, 因为没有被修正路径覆盖，也会返回前台
     * 2， 已废弃，这条路径已经被另一条路径覆盖，此路径已废弃
     */

    private Integer status=0;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public TAzCarinfoEntity getCar() {
        return car;
    }

    public void setCar(TAzCarinfoEntity car) {
        this.car = car;
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


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getOriginCity() {
        return originCity;
    }

    /**
     * 路线终点城市
     * @param originCity
     */
    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getGpsOriginLat() {
        return gpsOriginLat;
    }

    public void setGpsOriginLat(Double gpsOriginLat) {
        this.gpsOriginLat = gpsOriginLat;
    }

    public Double getGpsOriginLng() {
        return gpsOriginLng;
    }

    public void setGpsOriginLng(Double gpsOriginLng) {
        this.gpsOriginLng = gpsOriginLng;
    }

    public Double getGpsDestinationLat() {
        return gpsDestinationLat;
    }

    public void setGpsDestinationLat(Double gpsDestinationLat) {
        this.gpsDestinationLat = gpsDestinationLat;
    }

    public Double getGpsDestinationLng() {
        return gpsDestinationLng;
    }

    public void setGpsDestinationLng(Double gpsDestinationLng) {
        this.gpsDestinationLng = gpsDestinationLng;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public DateTime getGpsOriginSampleTime() {
        return gpsOriginSampleTime;
    }

    public void setGpsOriginSampleTime(DateTime gpsOriginSampleTime) {
        this.gpsOriginSampleTime = gpsOriginSampleTime;
    }

    public DateTime getGpsDestinationSampleTime() {
        return gpsDestinationSampleTime;
    }

    public void setGpsDestinationSampleTime(DateTime gpsDestinationTime) {
        this.gpsDestinationSampleTime = gpsDestinationTime;
    }
}
