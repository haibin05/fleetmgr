package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 调度出车单
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class ScheduleCar {
    private Car car;
    private Driver driver;
    private Schedule schedule;
    private String id;

    private ScheduleStatus status;

    private double km;

    private double startMiles;
    private double endMiles;

    public double getKm() {
        return km;
    }

    public double getStartMiles() {
        return startMiles;
    }

    public void setStartMiles(double startMiles) {
        this.startMiles = startMiles;
    }

    public double getEndMiles() {
        return endMiles;
    }

    public void setEndMiles(double endMiles) {
        this.endMiles = endMiles;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
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

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }


    @Override
    public String toString() {
        return "ScheduleCar{" +
                "car=" + car +
                ", driver=" + driver +
                ", schedule=" + schedule +
                ", id=" +id +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    @JsonIgnore
    public long getStart(){
        if (schedule!=null && schedule.getStart()!=null){
            return schedule.getStart().getMillis();
        }else{
            return 0;
        }
    }

    @JsonIgnore
    public long getDuration(){
        if (schedule!=null && schedule.getStart()!=null && schedule.getEnd()!=null){
            return schedule.getEnd().getMillis()-schedule.getStart().getMillis();
        }else{
            return 0;
        }
    }
}
