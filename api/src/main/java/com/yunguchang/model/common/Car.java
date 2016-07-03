package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * 车辆对象
 */
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)

public class Car {
    private String id;
    private Fleet fleet;
    private String model;
    private Integer capacity;
    private CarState carState;
    private String badge;
    private Driver driver;


    private int volition;

    private String licenseCarType;

    private Depot depot;

    private GpsPoint lastGps;

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public Car(String id) {
        this.id = id;
    }

    public Car(){}
    /**
     * 车辆Id
     *
     * @return 车辆Id
     */
    @DocumentationExample("2a59c66d605d42858258c6aae7571a26")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 所属车队
     *
     * @return 所属车队
     */
    public Fleet getFleet() {
        return fleet;
    }

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }

    /**
     * 车辆型号
     *
     * @return 车辆型号
     */
    @DocumentationExample("大客车")
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 额定载员数量
     *
     * @return 额定载员数量
     */
    @DocumentationExample("50")
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * 车辆状况
     *
     * @return 车辆状况
     */
    public CarState getCarState() {
        return carState;
    }

    public void setCarState(CarState carState) {
        this.carState = carState;
    }


    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public GpsPoint getLastGps() {
        return lastGps;
    }

    public void setLastGps(GpsPoint lastGps) {
        this.lastGps = lastGps;
    }

    /**
     * 车牌号
     *
     * @return 车牌号
     */
    @DocumentationExample("浙F13693")
    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public int getVolition() {
        return volition;
    }

    public void setVolition(int volition) {
        this.volition = volition;
    }

    public String getLicenseCarType() {
        return licenseCarType;
    }

    public void setLicenseCarType(String licenseCarType) {
        this.licenseCarType = licenseCarType;
    }

    public boolean hasValidDepot(){
        return depot!=null; //&& fleet.getDepot()!=null;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id='" + id + '\'' +
                ", fleet=" + fleet +
                ", model='" + model + '\'' +
                ", capacity=" + capacity +
                ", carState=" + carState +
                ", badge='" + badge + '\'' +
                ", driver=" + driver +
                ", volition=" + volition +
                ", licenseCarType='" + licenseCarType + '\'' +
                ", depot=" + depot +
                '}';
    }
}
