package com.yunguchang.model.persistence;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@javax.persistence.Table(name = "t_bus_evaluateinfo",
        indexes = {
                @Index(name = "FK_rct57audm88vg3wh7vdc80fh3", columnList = "DRIVERID", unique = false),
                @Index(name = "FK_gkav2nyd79hba7nrsf66jo3j5", columnList = "CARID", unique = false),
                @Index(name = "fk_p4xc0sb87xvcvqpg6cysek912", columnList = "USERID", unique = false),
        }
)
public class TBusEvaluateinfoEntity implements Serializable {
    @Id
    @javax.persistence.Column(name = "UUID", length = 50)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    private String uuid = UUID.randomUUID().toString().replace("-", "");

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "userid")
    private TSysUserEntity user;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "applyno", referencedColumnName = "applyno")
    private TBusApplyinfoEntity application;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "carid")
    private TAzCarinfoEntity car;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "driverid")
    private TRsDriverinfoEntity driver;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "relano")
    private TBusScheduleRelaEntity schedule;


    private Integer carscore;
    private Integer driverscore;
    private String evalstatus = "0"; // 评价状态(0:未评价；1：已评价)
    private DateTime evaldate;
    private String remark;
    private Integer teamscore;
    private String teamreason;
    private String driverreason;
    private String carreason;
    private String type;
    private String responseinfo;
    private Integer applyscore;
    private Integer personscore;
    private String applyreason;
    private String personreason;
    private String gdzt;
    private Boolean updateBySync = false;

    public TBusApplyinfoEntity getApplication() {
        return application;
    }

    public void setApplication(TBusApplyinfoEntity application) {
        this.application = application;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public TSysUserEntity getUser() {
        return user;
    }

    public void setUser(TSysUserEntity user) {
        this.user = user;
    }


    public Integer getCarscore() {
        return carscore;
    }

    public void setCarscore(Integer carscore) {
        this.carscore = carscore;
    }


    public Integer getDriverscore() {
        return driverscore;
    }

    public void setDriverscore(Integer driverscore) {
        this.driverscore = driverscore;
    }


    public String getEvalstatus() {
        return evalstatus;
    }

    public void setEvalstatus(String evalstatus) {
        this.evalstatus = evalstatus;
    }


    public DateTime getEvaldate() {
        return evaldate;
    }

    public void setEvaldate(DateTime evaldate) {
        this.evaldate = evaldate;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public Integer getTeamscore() {
        return teamscore;
    }

    public void setTeamscore(Integer teamscore) {
        this.teamscore = teamscore;
    }


    public String getTeamreason() {
        return teamreason;
    }

    public void setTeamreason(String teamreason) {
        this.teamreason = teamreason;
    }


    public String getDriverreason() {
        return driverreason;
    }

    public void setDriverreason(String driverreason) {
        this.driverreason = driverreason;
    }


    public String getCarreason() {
        return carreason;
    }

    public void setCarreason(String carreason) {
        this.carreason = carreason;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getResponseinfo() {
        return responseinfo;
    }

    public void setResponseinfo(String responseinfo) {
        this.responseinfo = responseinfo;
    }


    public Integer getApplyscore() {
        return applyscore;
    }

    public void setApplyscore(Integer applyscore) {
        this.applyscore = applyscore;
    }


    public Integer getPersonscore() {
        return personscore;
    }

    public void setPersonscore(Integer personscore) {
        this.personscore = personscore;
    }


    public String getApplyreason() {
        return applyreason;
    }

    public void setApplyreason(String applyreason) {
        this.applyreason = applyreason;
    }


    public String getPersonreason() {
        return personreason;
    }

    public void setPersonreason(String personreason) {
        this.personreason = personreason;
    }


    public String getGdzt() {
        return gdzt;
    }

    public void setGdzt(String gdzt) {
        this.gdzt = gdzt;
    }

//    public TBusApplyinfoEntity getApplication() {
//        return application;
//    }
//
//    public void setApplication(TBusApplyinfoEntity application) {
//        this.application = application;
//    }

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

    public TBusScheduleRelaEntity getSchedule() {
        return schedule;
    }

    public void setSchedule(TBusScheduleRelaEntity schedule) {
        this.schedule = schedule;
    }

    public Boolean getUpdateBySync() {
        return updateBySync;
    }

    public void setUpdateBySync(Boolean updateBySync) {
        this.updateBySync = updateBySync;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusEvaluateinfoEntity that = (TBusEvaluateinfoEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
//        if (application != null ? !application.equals(that.application) : that.application != null) return false;
        if (car != null ? !car.equals(that.car) : that.car != null) return false;
        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (schedule != null ? !schedule.equals(that.schedule) : that.schedule != null) return false;
        if (carscore != null ? !carscore.equals(that.carscore) : that.carscore != null) return false;
        if (driverscore != null ? !driverscore.equals(that.driverscore) : that.driverscore != null) return false;
        if (evalstatus != null ? !evalstatus.equals(that.evalstatus) : that.evalstatus != null) return false;
        if (evaldate != null ? !evaldate.equals(that.evaldate) : that.evaldate != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (teamscore != null ? !teamscore.equals(that.teamscore) : that.teamscore != null) return false;
        if (teamreason != null ? !teamreason.equals(that.teamreason) : that.teamreason != null) return false;
        if (driverreason != null ? !driverreason.equals(that.driverreason) : that.driverreason != null) return false;
        if (carreason != null ? !carreason.equals(that.carreason) : that.carreason != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (responseinfo != null ? !responseinfo.equals(that.responseinfo) : that.responseinfo != null) return false;
        if (applyscore != null ? !applyscore.equals(that.applyscore) : that.applyscore != null) return false;
        if (personscore != null ? !personscore.equals(that.personscore) : that.personscore != null) return false;
        if (applyreason != null ? !applyreason.equals(that.applyreason) : that.applyreason != null) return false;
        if (personreason != null ? !personreason.equals(that.personreason) : that.personreason != null) return false;
        return !(gdzt != null ? !gdzt.equals(that.gdzt) : that.gdzt != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
//        result = 31 * result + (application != null ? application.hashCode() : 0);
        result = 31 * result + (car != null ? car.hashCode() : 0);
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (schedule != null ? schedule.hashCode() : 0);
        result = 31 * result + (carscore != null ? carscore.hashCode() : 0);
        result = 31 * result + (driverscore != null ? driverscore.hashCode() : 0);
        result = 31 * result + (evalstatus != null ? evalstatus.hashCode() : 0);
        result = 31 * result + (evaldate != null ? evaldate.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (teamscore != null ? teamscore.hashCode() : 0);
        result = 31 * result + (teamreason != null ? teamreason.hashCode() : 0);
        result = 31 * result + (driverreason != null ? driverreason.hashCode() : 0);
        result = 31 * result + (carreason != null ? carreason.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (responseinfo != null ? responseinfo.hashCode() : 0);
        result = 31 * result + (applyscore != null ? applyscore.hashCode() : 0);
        result = 31 * result + (personscore != null ? personscore.hashCode() : 0);
        result = 31 * result + (applyreason != null ? applyreason.hashCode() : 0);
        result = 31 * result + (personreason != null ? personreason.hashCode() : 0);
        result = 31 * result + (gdzt != null ? gdzt.hashCode() : 0);
        return result;
    }
}
