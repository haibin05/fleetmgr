package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by gongy on 2015/10/26.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class RateOfPassenger {
    private int applicationRate;
    private String applicationConcern;
    private int passengerRate;
    private String passengerConcern;

    public int getApplicationRate() {
        return applicationRate;
    }

    public void setApplicationRate(int applicationRate) {
        this.applicationRate = applicationRate;
    }

    public String getApplicationConcern() {
        return applicationConcern;
    }

    public void setApplicationConcern(String applicationConcern) {
        this.applicationConcern = applicationConcern;
    }

    public int getPassengerRate() {
        return passengerRate;
    }

    public void setPassengerRate(int passengerRate) {
        this.passengerRate = passengerRate;
    }

    public String getPassengerConcern() {
        return passengerConcern;
    }

    public void setPassengerConcern(String passengerConcern) {
        this.passengerConcern = passengerConcern;
    }
}
