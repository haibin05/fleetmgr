package com.yunguchang.model.sync;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import org.joda.time.DateTime;

/**
 * 车辆维修状态
 */
@JsonSerialize
public class SyncCarRepairing {
    private String cphm;
    private boolean cancle = false;
    private DateTime time;
    private RepairingState state;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @DocumentationExample("浙F13610(37039)")
    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }

    /**
     * 状态时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @DocumentationExample("2015-10-09 09:24:00")
    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    /**
     * 维修状态
     * @return
     */
    @DocumentationExample
    public RepairingState getState() {
        return state;
    }

    public void setState(RepairingState state) {
        this.state = state;
    }

    public boolean isCancle() {
        return cancle;
    }

    public void setCancle(boolean cancle) {
        this.cancle = cancle;
    }
}
