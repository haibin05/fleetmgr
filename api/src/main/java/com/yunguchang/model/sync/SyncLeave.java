package com.yunguchang.model.sync;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import org.joda.time.DateTime;

/**
 * 请假申请
 */
@JsonSerialize
public class SyncLeave {
    private String driverEid;
    private LeaveType leaveType;
    private DateTime date;


    /**
     * 驾驶员ID
     * @return
     */
    @DocumentationExample("1009269")
    public String getDriverEid() {
        return driverEid;
    }

    public void setDriverEid(String driverEid) {
        this.driverEid = driverEid;
    }

    /**
     * 请假类型
     * @return
     */
    @DocumentationExample("事假")
    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    /**
     * 请假日期
     * @return
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @DocumentationExample("2015-11-01")
    public DateTime getDate() {
        return date;
    }
    @JsonDeserialize(converter = SyncDateConverter.class)
    public void setDate(DateTime date) {
        this.date = date;
    }
}
