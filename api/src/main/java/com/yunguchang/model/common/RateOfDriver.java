package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 用车评价
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class RateOfDriver {
    private int driverRate;
    private String driverConcern;
    private int carRate;
    private String carConcern;
    private int fleetRate;
    private String fleetConcern;

    public int getDriverRate() {
        return driverRate;
    }

    public void setDriverRate(int driverRate) {
        this.driverRate = driverRate;
    }

    public int getCarRate() {
        return carRate;
    }

    public void setCarRate(int carRate) {
        this.carRate = carRate;
    }

    public int getFleetRate() {
        return fleetRate;
    }

    public void setFleetRate(int fleetRate) {
        this.fleetRate = fleetRate;
    }

    public String getDriverConcern() {
        return driverConcern;
    }

    public void setDriverConcern(String driverConcern) {
        this.driverConcern = driverConcern;
    }

    public String getCarConcern() {
        return carConcern;
    }

    public void setCarConcern(String carConcern) {
        this.carConcern = carConcern;
    }

    public String getFleetConcern() {
        return fleetConcern;
    }

    public void setFleetConcern(String fleetConcern) {
        this.fleetConcern = fleetConcern;
    }
}
