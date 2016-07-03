package com.yunguchang.model.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

/**
 * Created by gongy on 9/27/2015.
 */

@JsonSerialize
public class ReturningDepotEvent {
    private Car car;
    private DateTime eventTime;
    private ReturningEventType returningEventType;

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public DateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(DateTime eventTime) {
        this.eventTime = eventTime;
    }

    public ReturningEventType getReturningEventType() {
        return returningEventType;
    }

    public void setReturningEventType(ReturningEventType returningEventType) {
        this.returningEventType = returningEventType;
    }

    @Override
    public String toString() {
        return "ReturningDepotEvent{" +
                "car=" + car +
                ", eventTime=" + eventTime +
                '}';
    }
}
