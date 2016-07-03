package com.yunguchang.model.persistence;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_bus_schedule_car",
        indexes = {
                @Index(name = "FK_8khu6snky62urf8aft1jr6t4t", columnList = "DRIVERID", unique = false),
                @Index(name = "FK_t0d2bjvuqailyustecdaiaonb", columnList = "RELANO", unique = false),
                @Index(name = "index3", columnList = "CARID", unique = false),
                @Index(name = "index4", columnList = "SCHCARNO", unique = false),
                @Index(name = "index5", columnList = "RELANO", unique = false),
        }
)
@FilterDefs(
        {
                @FilterDef(name = "filter_schedule_car_fleet", parameters = {@ParamDef(name = "userId", type = "string")} ,defaultCondition = "driverid in " +
                        "(SELECT d.uuid FROM t_rs_driverinfo d " +
                        "where d.orgid like " +
                        " (select CONCAT(u.deptid, '%') from "+
                        "        t_sys_user u where u.userid=:userId" +
                        " )" +
                        ")"
                ),
                @FilterDef(name = "filter_schedule_car_self", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition =
                        "(SELECT count(app.uuid) FROM t_bus_applyinfo app " +
                        "where app.userid = :userId and app.RELANO = RELANO" +
                        ")"
                ),
                @FilterDef(name = "filter_schedule_car_all", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="1=1"),
                @FilterDef(name = "filter_schedule_car_driver", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="driverid=:userId "),
                @FilterDef(name = "filter_schedule_car_none", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="1=0"),

        }
)

@Filters({
        @Filter(name = "filter_schedule_car_fleet"),
        @Filter(name = "filter_schedule_car_self"),
        @Filter(name = "filter_schedule_car_all"),
        @Filter(name = "filter_schedule_car_driver"),
        @Filter(name = "filter_schedule_car_none"),


})
public class TBusScheduleCarEntity implements Serializable {
    @Id
    @Column(name = "UUID", length = 50)
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    private String uuid = UUID.randomUUID().toString().replace("-", "");

    @ManyToOne(fetch = FetchType.LAZY)
    //a workaround that handle inconsistent records
//    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "RELANO")
    private TBusScheduleRelaEntity schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARID")
    private TAzCarinfoEntity car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVERID")
    private TRsDriverinfoEntity driver;
    private String schcarno;
    private Integer status;
    private String cancelseason;
    private String canceluser;
    private Timestamp canceltime;
    private String canceltype;
    private String postId4Driver;
    private String postId4Manager;

    @OneToOne(mappedBy = "scheduleCar", fetch = FetchType.LAZY )
    private TBusRecordinfoEntity record;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public TBusScheduleRelaEntity getSchedule() {
        return schedule;
    }

    public void setSchedule(TBusScheduleRelaEntity schedule) {
        this.schedule = schedule;
    }


    public TAzCarinfoEntity getCar() {
        return car;
    }

    public void setCar(TAzCarinfoEntity car) {
        this.car = car;
    }


    public TRsDriverinfoEntity getDriver() {
        return driver;
    }
    public void setDriver(TRsDriverinfoEntity driver) {
        this.driver = driver;
    }


    public String getSchcarno() {
        return schcarno;
    }

    public void setSchcarno(String schcarno) {
        this.schcarno = schcarno;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getCancelseason() {
        return cancelseason;
    }

    public void setCancelseason(String cancelseason) {
        this.cancelseason = cancelseason;
    }


    public String getCanceluser() {
        return canceluser;
    }

    public void setCanceluser(String canceluser) {
        this.canceluser = canceluser;
    }


    public Timestamp getCanceltime() {
        return canceltime;
    }

    public void setCanceltime(Timestamp canceltime) {
        this.canceltime = canceltime;
    }


    public String getCanceltype() {
        return canceltype;
    }

    public void setCanceltype(String canceltype) {
        this.canceltype = canceltype;
    }

    public TBusRecordinfoEntity getRecord() {
        return record;
    }

    public void setRecord(TBusRecordinfoEntity record) {
        this.record = record;
    }

    public String getPostId4Driver() {
        return postId4Driver;
    }

    public void setPostId4Driver(String postId4Driver) {
        this.postId4Driver = postId4Driver;
    }

    public String getPostId4Manager() {
        return postId4Manager;
    }

    public void setPostId4Manager(String postId4Manager) {
        this.postId4Manager = postId4Manager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusScheduleCarEntity that = (TBusScheduleCarEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (schedule != null ? !schedule.equals(that.schedule) : that.schedule != null) return false;
        if (car != null ? !car.equals(that.car) : that.car != null) return false;
        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (schcarno != null ? !schcarno.equals(that.schcarno) : that.schcarno != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (cancelseason != null ? !cancelseason.equals(that.cancelseason) : that.cancelseason != null) return false;
        if (canceluser != null ? !canceluser.equals(that.canceluser) : that.canceluser != null) return false;
        if (canceltime != null ? !canceltime.equals(that.canceltime) : that.canceltime != null) return false;
        return !(canceltype != null ? !canceltype.equals(that.canceltype) : that.canceltype != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (schedule != null ? schedule.hashCode() : 0);
        result = 31 * result + (car != null ? car.hashCode() : 0);
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (schcarno != null ? schcarno.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (cancelseason != null ? cancelseason.hashCode() : 0);
        result = 31 * result + (canceluser != null ? canceluser.hashCode() : 0);
        result = 31 * result + (canceltime != null ? canceltime.hashCode() : 0);
        result = 31 * result + (canceltype != null ? canceltype.hashCode() : 0);
        return result;
    }
}
