package com.yunguchang.model.persistence;

import org.hibernate.annotations.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;


/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_bus_schedule_rela",
        indexes = {
                @Index(name = "FK_tcir15xn14o4tev7w3rynvwuv", columnList = "SSCD", unique = false),
                @Index(name = "FK_mrphbnhjae45orio5hhwq1lht", columnList = "RECIVEUSER", unique = false),
                @Index(name = "FK_c1wtwfpc19e2slpfjbmdlte34", columnList = "SENDUSER", unique = false),
                @Index(name = "index2", columnList = "STARTTIME", unique = false),
                @Index(name = "index3", columnList = "ENDTIME", unique = false),
        }
)

@FilterDefs(
        {
                @FilterDef(name = "filter_schedule_fleet", parameters = {@ParamDef(name = "userId", type = "string")} ,
                        defaultCondition = "SSCD like " +
                        " (select CONCAT(u.deptid, '%') from " +
                        "        t_sys_user u where u.userid=:userId" +
                        ")"
                ),
                @FilterDef(name = "filter_schedule_all", parameters = {@ParamDef(name = "userId", type = "string")},
                        defaultCondition="1=1"),
                @FilterDef(name = "filter_schedule_driver", parameters = {@ParamDef(name = "userId", type = "string")},
                        defaultCondition="UUID in" +
                                "(select sc.RELANO from t_bus_schedule_car sc where sc.DRIVERID=:userId)"),
                @FilterDef(name = "filter_schedule_none", parameters = {@ParamDef(name = "userId", type = "string")},
                        defaultCondition="1=0"),

        }
)

@Filters({
        @Filter(name = "filter_schedule_fleet"),
        @Filter(name = "filter_schedule_all"),
        @Filter(name = "filter_schedule_driver"),
        @Filter(name = "filter_schedule_none"),


})
public class TBusScheduleRelaEntity implements Serializable {

    @Id
    @Column(name = "UUID")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    private String uuid = (DateTimeFormat.forPattern("yyyyMMddHHmmssSSS")).print(DateTime.now());

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sscd")
    private TSysOrgEntity fleet;
    private DateTime starttime;
    private DateTime endtime;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="senduser")
    private TSysUserEntity senduser;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="reciveuser")
    private TSysUserEntity reciveuser;
    private int status;
    private String cancelseason;
    private String canceluser;
    private DateTime canceltime;
    private String insertuser;
    private DateTime insertdate;
    private String updateuser;
    private DateTime updatedate;
    private String startpoint;
    private String ways;
    private int peoplenum;
    private String canceltype;
    private int printflag;
    private String postId4Dispatcher;
    private String postId4Driver;
    private String postId4Manager;
    private Boolean updateBySync = false;

    @OneToMany(mappedBy = "schedule", fetch=FetchType.LAZY)
    private List<TBusScheduleCarEntity> scheduleCars;

    @OneToMany(mappedBy = "schedule", fetch=FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<TBusApplyinfoEntity> applications;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public TSysOrgEntity getFleet() {
        return fleet;
    }

    public void setFleet(TSysOrgEntity fleet) {
        this.fleet = fleet;
    }

    public DateTime getStarttime() {
        return starttime;
    }

    public void setStarttime(DateTime starttime) {
        this.starttime = starttime;
    }


    public DateTime getEndtime() {
        return endtime;
    }

    public void setEndtime(DateTime endtime) {
        this.endtime = endtime;
    }


    public TSysUserEntity getSenduser() {
        return senduser;
    }

    public void setSenduser(TSysUserEntity senduser) {
        this.senduser = senduser;
    }


    public TSysUserEntity getReciveuser() {
        return reciveuser;
    }

    public void setReciveuser(TSysUserEntity reciveuser) {
        this.reciveuser = reciveuser;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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


    public DateTime getCanceltime() {
        return canceltime;
    }

    public void setCanceltime(DateTime canceltime) {
        this.canceltime = canceltime;
    }


    public String getInsertuser() {
        return insertuser;
    }

    public void setInsertuser(String insertuser) {
        this.insertuser = insertuser;
    }


    public DateTime getInsertdate() {
        return insertdate;
    }

    public void setInsertdate(DateTime insertdate) {
        this.insertdate = insertdate;
    }


    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }


    public DateTime getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(DateTime updatedate) {
        this.updatedate = updatedate;
    }


    public String getStartpoint() {
        return startpoint;
    }

    public void setStartpoint(String startpoint) {
        this.startpoint = startpoint;
    }


    public String getWays() {
        return ways;
    }

    public void setWays(String ways) {
        this.ways = ways;
    }


    public int getPeoplenum() {
        return peoplenum;
    }

    public void setPeoplenum(int peoplenum) {
        this.peoplenum = peoplenum;
    }


    public String getCanceltype() {
        return canceltype;
    }

    public void setCanceltype(String canceltype) {
        this.canceltype = canceltype;
    }


    public int getPrintflag() {
        return printflag;
    }

    public void setPrintflag(int printflag) {
        this.printflag = printflag;
    }

    public List<TBusScheduleCarEntity> getScheduleCars() {
        return scheduleCars;
    }

    public void setScheduleCars(List<TBusScheduleCarEntity> scheduleCars) {
        this.scheduleCars = scheduleCars;
    }

    public List<TBusApplyinfoEntity> getApplications() {
        return applications;
    }

    public void setApplications(List<TBusApplyinfoEntity> applications) {
        this.applications = applications;
    }

    public String getPostId4Dispatcher() {
        return postId4Dispatcher;
    }

    public void setPostId4Dispatcher(String postId4Dispatcher) {
        this.postId4Dispatcher = postId4Dispatcher;
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

        TBusScheduleRelaEntity that = (TBusScheduleRelaEntity) o;

        if (status != that.status) return false;
        if (peoplenum != that.peoplenum) return false;
        if (printflag != that.printflag) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (fleet != null ? !fleet.equals(that.fleet) : that.fleet != null) return false;
        if (starttime != null ? !starttime.equals(that.starttime) : that.starttime != null) return false;
        if (endtime != null ? !endtime.equals(that.endtime) : that.endtime != null) return false;
        if (senduser != null ? !senduser.equals(that.senduser) : that.senduser != null) return false;
        if (reciveuser != null ? !reciveuser.equals(that.reciveuser) : that.reciveuser != null) return false;
        if (cancelseason != null ? !cancelseason.equals(that.cancelseason) : that.cancelseason != null) return false;
        if (canceluser != null ? !canceluser.equals(that.canceluser) : that.canceluser != null) return false;
        if (canceltime != null ? !canceltime.equals(that.canceltime) : that.canceltime != null) return false;
        if (insertuser != null ? !insertuser.equals(that.insertuser) : that.insertuser != null) return false;
        if (insertdate != null ? !insertdate.equals(that.insertdate) : that.insertdate != null) return false;
        if (updateuser != null ? !updateuser.equals(that.updateuser) : that.updateuser != null) return false;
        if (updatedate != null ? !updatedate.equals(that.updatedate) : that.updatedate != null) return false;
        if (startpoint != null ? !startpoint.equals(that.startpoint) : that.startpoint != null) return false;
        if (ways != null ? !ways.equals(that.ways) : that.ways != null) return false;
        if (canceltype != null ? !canceltype.equals(that.canceltype) : that.canceltype != null) return false;
        return !(scheduleCars != null ? !scheduleCars.equals(that.scheduleCars) : that.scheduleCars != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (fleet != null ? fleet.hashCode() : 0);
        result = 31 * result + (starttime != null ? starttime.hashCode() : 0);
        result = 31 * result + (endtime != null ? endtime.hashCode() : 0);
        result = 31 * result + (senduser != null ? senduser.hashCode() : 0);
        result = 31 * result + (reciveuser != null ? reciveuser.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + (cancelseason != null ? cancelseason.hashCode() : 0);
        result = 31 * result + (canceluser != null ? canceluser.hashCode() : 0);
        result = 31 * result + (canceltime != null ? canceltime.hashCode() : 0);
        result = 31 * result + (insertuser != null ? insertuser.hashCode() : 0);
        result = 31 * result + (insertdate != null ? insertdate.hashCode() : 0);
        result = 31 * result + (updateuser != null ? updateuser.hashCode() : 0);
        result = 31 * result + (updatedate != null ? updatedate.hashCode() : 0);
        result = 31 * result + (startpoint != null ? startpoint.hashCode() : 0);
        result = 31 * result + (ways != null ? ways.hashCode() : 0);
        result = 31 * result + peoplenum;
        result = 31 * result + (canceltype != null ? canceltype.hashCode() : 0);
        result = 31 * result + printflag;
        result = 31 * result + (scheduleCars != null ? scheduleCars.hashCode() : 0);
        return result;
    }
}
