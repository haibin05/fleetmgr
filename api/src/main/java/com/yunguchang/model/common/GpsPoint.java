package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.Serializable;

/**
 * GPS监测点信息
 */
@JsonSerialize
public class GpsPoint {
    // 经度
    @DocumentationExample("120")
    private double lng;
    // 纬度
    @DocumentationExample("30")
    private double lat;
    // 速度 (公里/小时)
    @DocumentationExample("65")
    private double speed;
    // 当前车辆
    @JsonIgnore
    private Car car;
    // 当前驾驶员
    private Driver driver;
    // GPS采样时间
    @DocumentationExample("2015-01-01 10:12:13")
    private DateTime sampleTime;
    // GPS保存到数据库存的时间
    @DocumentationExample("2015-01-01 10:12:13")
    private DateTime persistTime;

    @JsonIgnore
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GpsPoint() {
    }

    public GpsPoint(double lng, double lat, double speed, Car car) {
        this.lng = lng;
        this.lat = lat;
        this.speed = speed;
        this.car = car;
    }

    public GpsPoint(double lng, double lat, double speed, Car car, DateTime sampleTime) {
        this(lng, lat, speed, car);
        this.sampleTime = sampleTime;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public DateTime getSampleTime() {
        return sampleTime;
    }

    public void setSampleTime(DateTime sampleTime) {
        this.sampleTime = sampleTime;
    }

    public DateTime getPersistTime() {
        return persistTime;
    }

    public void setPersistTime(DateTime persistTime) {
        this.persistTime = persistTime;
    }

    @Override
    public String toString() {
        String carString = null;
        if (car != null) {
            carString = car.getBadge();
        }
        if (car != null && carString == null) {
            carString = car.getId();
        }

        return "GpsPoint{" +
                "lng=" + lng +
                ", lat=" + lat +
                ", speed=" + speed +
                ", car=" + carString +
                ", sampleTime=" + sampleTime +
                ", persistTime=" + persistTime +
                '}';
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static GpsPoint fromJson(String json){
        try {
            return new ObjectMapper().readValue(json, GpsPoint.class);
        } catch (IOException e) {
            return null;
        }

    }


}
