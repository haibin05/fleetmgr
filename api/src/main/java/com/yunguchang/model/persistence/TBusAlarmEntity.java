package com.yunguchang.model.persistence;

import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.Index;

/**
 * Created by 禕 on 2015/9/23.
 */
@Entity
@javax.persistence.Table(name = "t_bus_alarm",
        indexes = {
                @Index(name = "index2", columnList = "alarm", unique = false),
                @Index(name = "index3", columnList = "end", unique = false),
                @Index(name = "index4", columnList = "start", unique = false),
                @Index(name = "index5", columnList = "carid", unique = false),
        }
)
@FilterDefs(
        {
                @FilterDef(name = "filter_alarms_fleet", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "carid in " +
                        "(SELECT car.id FROM t_az_carinfo car " +
                        "where car.SSCD like " +
                        " (select CONCAT(u.deptid, '%') from " +
                        "        t_sys_user u where u.userid=:userId" +
                        " )" +
                        ")"
                ),
                @FilterDef(name = "filter_alarms_all", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "1=1"),
                @FilterDef(name = "filter_alarms_driver", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "carid in " +
                        "(select car.id from t_az_carinfo car where car.JSY = :userId)"),
                @FilterDef(name = "filter_alarms_none", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "1=0"),

        }
)

@Filters({
        @Filter(name = "filter_alarms_fleet"),
        @Filter(name = "filter_alarms_all"),
        @Filter(name = "filter_alarms_driver"),
        @Filter(name = "filter_alarms_none")
})
public class TBusAlarmEntity {
    /**
     * 主键Mysql自增加字段
     *
     * @return
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer alarm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARID")
    private TAzCarinfoEntity car;

    private DateTime start;

    private DateTime end;

    private Double gpsLat;

    private Double gpsLng;

    private DateTime gpsSampleTime;

    private Double gpsSpeed;

    private DateTime persistTime;

    public DateTime getPersistTime() {
        return persistTime;
    }

    public void setPersistTime(DateTime persistTime) {
        this.persistTime = persistTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAlarm() {
        return alarm;
    }

    public void setAlarm(Integer alarm) {
        this.alarm = alarm;
    }

    public TAzCarinfoEntity getCar() {
        return car;
    }

    public void setCar(TAzCarinfoEntity car) {
        this.car = car;
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

    public Double getGpsLat() {
        return gpsLat;
    }

    public void setGpsLat(Double gpsLat) {
        this.gpsLat = gpsLat;
    }

    public Double getGpsLng() {
        return gpsLng;
    }

    public void setGpsLng(Double gpsLng) {
        this.gpsLng = gpsLng;
    }

    public DateTime getGpsSampleTime() {
        return gpsSampleTime;
    }

    public void setGpsSampleTime(DateTime gpsSampleTime) {
        this.gpsSampleTime = gpsSampleTime;
    }

    public Double getGpsSpeed() {
        return gpsSpeed;
    }

    public void setGpsSpeed(Double gpsSpeed) {
        this.gpsSpeed = gpsSpeed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusAlarmEntity that = (TBusAlarmEntity) o;

        if (id != that.id) return false;
        if (alarm != null ? !alarm.equals(that.alarm) : that.alarm != null) return false;
        if (car != null ? !car.equals(that.car) : that.car != null) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (gpsLat != null ? !gpsLat.equals(that.gpsLat) : that.gpsLat != null) return false;
        if (gpsLng != null ? !gpsLng.equals(that.gpsLng) : that.gpsLng != null) return false;
        if (gpsSampleTime != null ? !gpsSampleTime.equals(that.gpsSampleTime) : that.gpsSampleTime != null)
            return false;
        return !(gpsSpeed != null ? !gpsSpeed.equals(that.gpsSpeed) : that.gpsSpeed != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (alarm != null ? alarm.hashCode() : 0);
        result = 31 * result + (car != null ? car.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (gpsLat != null ? gpsLat.hashCode() : 0);
        result = 31 * result + (gpsLng != null ? gpsLng.hashCode() : 0);
        result = 31 * result + (gpsSampleTime != null ? gpsSampleTime.hashCode() : 0);
        result = 31 * result + (gpsSpeed != null ? gpsSpeed.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TBusAlarmEntity{" +
                "alarm=" + alarm +
                ", car=" + car +
                ", start=" + start +
                ", end=" + end +
                ", gpsLat=" + gpsLat +
                ", gpsLng=" + gpsLng +
                ", gpsSampleTime=" + gpsSampleTime +
                ", gpsSpeed=" + gpsSpeed +
                ", persistTime=" + persistTime +
                ", id=" + id +
                '}';
    }
}
