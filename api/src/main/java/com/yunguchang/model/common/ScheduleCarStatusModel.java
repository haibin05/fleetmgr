package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by gongy on 2015/11/2.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class ScheduleCarStatusModel {
    private ScheduleStatus status;

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }
}
