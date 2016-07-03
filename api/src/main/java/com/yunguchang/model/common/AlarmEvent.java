package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.common.Car;
import com.yunguchang.model.common.GpsPoint;
import org.joda.time.DateTime;

/**
 * Created by gongy on 9/21/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class AlarmEvent {
    private int id;
    protected Car car;
    protected Alarm alarm;
    protected DateTime start;
    protected DateTime end;
    protected GpsPoint gpsPoint;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
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

    public GpsPoint getGpsPoint() {
        return gpsPoint;
    }

    public void setGpsPoint(GpsPoint gpsPoint) {
        this.gpsPoint = gpsPoint;
    }

    @Override
    public String toString() {
        return "AlarmEvent{" +
                "car=" + car +
                ", alarm=" + alarm +
                ", start=" + start +
                ", end=" + end +
                ", gpsPoint=" + gpsPoint +
                '}';
    }
}
