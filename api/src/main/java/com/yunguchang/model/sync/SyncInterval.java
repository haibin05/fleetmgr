package com.yunguchang.model.sync;

import org.joda.time.DateTime;

/**
 * 用于表示时间 包含开始时间和结束时间
 * Created by haibin on 2015/12/30.
 */
public class SyncInterval {
    private DateTime startTime;
    private DateTime endTime;

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = new DateTime(Long.valueOf(startTime));
    }
    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(String startTime) {
        this.endTime = new DateTime(Long.valueOf(startTime));
    }
    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }
}
