package com.yunguchang.model.persistence;

import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.Index;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@javax.persistence.Table(name = "t_bus_applyinfo",
        indexes = {
                @Index(name = "UK_rbnn1ofgym0t986qq5asi2oke", columnList = "APPLYNO", unique = true),
                @Index(name = "FK_ql7lq9ubmi8oi7tukhfit542i", columnList = "USERID", unique = false),
                @Index(name = "FK_9k41435r1qx5b5lwm73w5dky", columnList = "ORGID", unique = false),
                @Index(name = "FK_pcmkpky282fbbqwcx54tij7mp", columnList = "SSCD", unique = false),
                @Index(name = "FK_b6tadkkamm4eusgo3ajl8yuw5", columnList = "RELANO", unique = false),
                @Index(name = "FK_j0bnf0qowwmopes3bpdlge28p", columnList = "PERSONID", unique = false),
                @Index(name = "FK_28iquniia4f1400j7la7atume", columnList = "SENDUSER", unique = false),
                @Index(name = "index6", columnList = "BEGINTIME", unique = false),
                @Index(name = "index7", columnList = "ENDTIME", unique = false),
        }
)
@FilterDefs(
        {
                @FilterDef(name = "filter_applications_fleet", parameters = {@ParamDef(name = "userId", type = "string")},
                        defaultCondition = "(SSCD like " +
                        " (select CONCAT(u.deptid, '%') from " +
                        "        t_sys_user u where u.userid=:userId" +
                        " )"+
                        "or USERID = (select u.userno from t_sys_user u where u.userid=:userId )" +
                        ")"


                ),
                @FilterDef(name = "filter_applications_all", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "1 = 1"),
                @FilterDef(name = "filter_applications_driver", parameters = {@ParamDef(name = "userId", type = "string")},
                        defaultCondition = "RELANO in " +
                        "(SELECT r.uuid FROM t_bus_schedule_rela r, t_bus_schedule_car sc " +
                        "where sc.relano=r.uuid " +
                        "and sc.driverid=:userId)"
                ),
                @FilterDef(name = "filter_applications_approver", parameters = {@ParamDef(name = "userId", type = "string")},
                        defaultCondition =
                        "(ORGID like " +
                        " (select CONCAT(u.deptid, '%') from " +
                        "        t_sys_user u where u.userid=:userId" +
                        " )"+
                        "or USERID = (select u.userno from t_sys_user u where u.userid=:userId )" +
                        ")"
                ),
                @FilterDef(name = "filter_applications_coordinator", parameters = {@ParamDef(name = "userId", type = "string")},
                        defaultCondition = "USERID = " +
                        "(SELECT user.userno FROM t_sys_user user where user.userid=:userId)"
                ),
                @FilterDef(name = "filter_applications_self", parameters = {@ParamDef(name = "userId", type = "string")},
                        defaultCondition = "USERID = " +
                        "(SELECT user.userno FROM t_sys_user user where user.userid=:userId)"
                ),
                @FilterDef(name = "filter_applications_none", parameters = {@ParamDef(name = "userId", type = "string")},
                        defaultCondition = "1 =0 "
                )
        }
)
@Filters({
        @Filter(name = "filter_applications_fleet"),
        @Filter(name = "filter_applications_all"),
        @Filter(name = "filter_applications_driver"),
        @Filter(name = "filter_applications_approver"),
        @Filter(name = "filter_applications_coordinator"),
        @Filter(name = "filter_applications_self"),
        @Filter(name = "filter_applications_none")
})
public class TBusApplyinfoEntity implements Serializable {
    @Id
    @javax.persistence.Column(name = "UUID", length = 50)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    private String uuid = UUID.randomUUID().toString().replace("-", "");

    private String applyno;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "userid", referencedColumnName = "userid")// 审核人
    private TSysUserEntity coordinator;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "orgid")
    private TSysOrgEntity department;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "personid")
    private TBusMainUserInfoEntity passenger;

    private Integer peoplenum;
    private String startpoint;
    private String ways;
    private String cargodes;
    private String reason;
    private String remark;
    private String status;
    private String updateuser;
    private DateTime begintime;
    private DateTime endtime;
    private DateTime applydate;
    private DateTime updatedate;
    private String isgoods;
    private String outid;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "senduser", referencedColumnName = "userid")// 调度人
    private TSysUserEntity senduser;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sscd")
    private TSysOrgEntity fleet;

    private String issend = "0"; // 转总调度标识(默认为0，1：转总调)
    private String schreason;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "relano")
    private TBusScheduleRelaEntity schedule;

    private String cancelseason;
    private String canceluser;
    private DateTime canceltime;
    private Integer savestatus;
    private String canceltype;
    // 是否规范： 0：不规范   1：规范
    private String sfgf;
    // 不规范信息，数据格式如下：  申请单填写不规范;用车人时间不规范;用车人地点不规范;
    private String bgfxx;
    private String isout;
    private String reasonms;
    private String isback;
    private String usetype;
    private Boolean updateBySync = false;
    private String postId4Coordinator;
    private String postId4Auditor;
    private String postId4Dispatcher;
    private String postId4Manager;

    @Formula("(select dic.datacode from t_sys_dic dic where dic.classcode='ReasonType' and dic.datacode = reason)")
    private String reasonCode;
    @Formula("(select dic.datavalue from t_sys_dic dic where dic.classcode='ReasonType' and dic.datacode = reason)")
    private String reasonText;

    @Formula("(select dic.datacode from t_sys_dic dic where dic.classcode='ScheduleStatus' and dic.datacode = status)")
    private String statusCode;
    @Formula("(select dic.datavalue from t_sys_dic dic where dic.classcode='ScheduleStatus' and dic.datacode = status)")
    private String statusText;

    @PrePersist
    void preInsert() {
        if (updatedate == null) {
            updatedate = DateTime.now();
        }
        if (issend == null) {
            issend = "0";
        }
        if (savestatus == null) {
            savestatus = 0;
        }

        if (sfgf == null) {
            sfgf = "1";
        }

        if (isout == null) {
            isout = "0";
        }

        if (isback == null) {
            isback = "1";
        }

        if (usetype == null) {
            usetype = "1";
        }

        if (status == null) {
            status = "1";
        }

    }

    public TBusScheduleRelaEntity getSchedule() {
        return schedule;
    }

    public void setSchedule(TBusScheduleRelaEntity schedule) {
        this.schedule = schedule;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getApplyno() {
        return applyno;
    }

    public void setApplyno(String applyno) {
        this.applyno = applyno;
    }

    public TSysUserEntity getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(TSysUserEntity coordinator) {
        this.coordinator = coordinator;
    }

    public TSysOrgEntity getDepartment() {
        return department;
    }

    public void setDepartment(TSysOrgEntity department) {
        this.department = department;
    }

    public TBusMainUserInfoEntity getPassenger() {
        return passenger;
    }

    public void setPassenger(TBusMainUserInfoEntity passenger) {
        this.passenger = passenger;
    }

    public Integer getPeoplenum() {
        return peoplenum;
    }

    public void setPeoplenum(Integer peoplenum) {
        this.peoplenum = peoplenum;
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


    public String getCargodes() {
        return cargodes;
    }

    public void setCargodes(String cargodes) {
        this.cargodes = cargodes;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }


    public DateTime getBegintime() {
        return begintime;
    }

    public void setBegintime(DateTime begintime) {
        this.begintime = begintime;
    }


    public DateTime getEndtime() {
        return endtime;
    }

    public void setEndtime(DateTime endtime) {
        this.endtime = endtime;
    }


    public DateTime getApplydate() {
        return applydate;
    }

    public void setApplydate(DateTime applydate) {
        this.applydate = applydate;
    }


    public DateTime getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(DateTime updatedate) {
        this.updatedate = updatedate;
    }


    public String getIsgoods() {
        return isgoods;
    }

    public void setIsgoods(String isgoods) {
        this.isgoods = isgoods;
    }


    public String getOutid() {
        return outid;
    }

    public void setOutid(String outid) {
        this.outid = outid;
    }


    public TSysUserEntity getSenduser() {
        return senduser;
    }

    public void setSenduser(TSysUserEntity senduser) {
        this.senduser = senduser;
    }


    public TSysOrgEntity getFleet() {
        return fleet;
    }

    public void setFleet(TSysOrgEntity fleet) {
        this.fleet = fleet;
    }

    public String getIssend() {
        return issend;
    }

    public void setIssend(String issend) {
        this.issend = issend;
    }


    public String getSchreason() {
        return schreason;
    }

    public void setSchreason(String schreason) {
        this.schreason = schreason;
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


    public Integer getSavestatus() {
        return savestatus;
    }

    public void setSavestatus(Integer savestatus) {
        this.savestatus = savestatus;
    }


    public String getCanceltype() {
        return canceltype;
    }

    public void setCanceltype(String canceltype) {
        this.canceltype = canceltype;
    }


    public String getSfgf() {
        return sfgf;
    }

    public void setSfgf(String sfgf) {
        this.sfgf = sfgf;
    }


    public String getBgfxx() {
        return bgfxx;
    }

    public void setBgfxx(String bgfxx) {
        this.bgfxx = bgfxx;
    }


    public String getIsout() {
        return isout;
    }

    public void setIsout(String isout) {
        this.isout = isout;
    }


    public String getReasonms() {
        return reasonms;
    }

    public void setReasonms(String reasonms) {
        this.reasonms = reasonms;
    }


    public String getIsback() {
        return isback;
    }

    public void setIsback(String isback) {
        this.isback = isback;
    }


    public String getUsetype() {
        return usetype;
    }

    public void setUsetype(String usetype) {
        this.usetype = usetype;
    }

    public String getPostId4Coordinator() {
        return postId4Coordinator;
    }

    public void setPostId4Coordinator(String postId4Coordinator) {
        this.postId4Coordinator = postId4Coordinator;
    }

    public String getPostId4Auditor() {
        return postId4Auditor;
    }

    public void setPostId4Auditor(String postId4Auditor) {
        this.postId4Auditor = postId4Auditor;
    }

    public String getPostId4Dispatcher() {
        return postId4Dispatcher;
    }

    public void setPostId4Dispatcher(String postId4Dispatcher) {
        this.postId4Dispatcher = postId4Dispatcher;
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

        TBusApplyinfoEntity that = (TBusApplyinfoEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (applyno != null ? !applyno.equals(that.applyno) : that.applyno != null) return false;
        if (coordinator != null ? !coordinator.equals(that.coordinator) : that.coordinator != null) return false;
        if (department != null ? !department.equals(that.department) : that.department != null) return false;
        if (passenger != null ? !passenger.equals(that.passenger) : that.passenger != null) return false;
        if (peoplenum != null ? !peoplenum.equals(that.peoplenum) : that.peoplenum != null) return false;
        if (startpoint != null ? !startpoint.equals(that.startpoint) : that.startpoint != null) return false;
        if (ways != null ? !ways.equals(that.ways) : that.ways != null) return false;
        if (cargodes != null ? !cargodes.equals(that.cargodes) : that.cargodes != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (updateuser != null ? !updateuser.equals(that.updateuser) : that.updateuser != null) return false;
        if (begintime != null ? !begintime.equals(that.begintime) : that.begintime != null) return false;
        if (endtime != null ? !endtime.equals(that.endtime) : that.endtime != null) return false;
        if (applydate != null ? !applydate.equals(that.applydate) : that.applydate != null) return false;
        if (updatedate != null ? !updatedate.equals(that.updatedate) : that.updatedate != null) return false;
        if (isgoods != null ? !isgoods.equals(that.isgoods) : that.isgoods != null) return false;
        if (outid != null ? !outid.equals(that.outid) : that.outid != null) return false;
        if (senduser != null ? !senduser.equals(that.senduser) : that.senduser != null) return false;
        if (fleet != null ? !fleet.equals(that.fleet) : that.fleet != null) return false;
        if (issend != null ? !issend.equals(that.issend) : that.issend != null) return false;
        if (schreason != null ? !schreason.equals(that.schreason) : that.schreason != null) return false;
        if (schedule != null ? !schedule.equals(that.schedule) : that.schedule != null) return false;
        if (cancelseason != null ? !cancelseason.equals(that.cancelseason) : that.cancelseason != null) return false;
        if (canceluser != null ? !canceluser.equals(that.canceluser) : that.canceluser != null) return false;
        if (canceltime != null ? !canceltime.equals(that.canceltime) : that.canceltime != null) return false;
        if (savestatus != null ? !savestatus.equals(that.savestatus) : that.savestatus != null) return false;
        if (canceltype != null ? !canceltype.equals(that.canceltype) : that.canceltype != null) return false;
        if (sfgf != null ? !sfgf.equals(that.sfgf) : that.sfgf != null) return false;
        if (bgfxx != null ? !bgfxx.equals(that.bgfxx) : that.bgfxx != null) return false;
        if (isout != null ? !isout.equals(that.isout) : that.isout != null) return false;
        if (reasonms != null ? !reasonms.equals(that.reasonms) : that.reasonms != null) return false;
        if (isback != null ? !isback.equals(that.isback) : that.isback != null) return false;
        if (usetype != null ? !usetype.equals(that.usetype) : that.usetype != null) return false;
        if (reasonCode != null ? !reasonCode.equals(that.reasonCode) : that.reasonCode != null) return false;
        if (reasonText != null ? !reasonText.equals(that.reasonText) : that.reasonText != null) return false;
        if (statusCode != null ? !statusCode.equals(that.statusCode) : that.statusCode != null) return false;
        return !(statusText != null ? !statusText.equals(that.statusText) : that.statusText != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (applyno != null ? applyno.hashCode() : 0);
        result = 31 * result + (coordinator != null ? coordinator.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (passenger != null ? passenger.hashCode() : 0);
        result = 31 * result + (peoplenum != null ? peoplenum.hashCode() : 0);
        result = 31 * result + (startpoint != null ? startpoint.hashCode() : 0);
        result = 31 * result + (ways != null ? ways.hashCode() : 0);
        result = 31 * result + (cargodes != null ? cargodes.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (updateuser != null ? updateuser.hashCode() : 0);
        result = 31 * result + (begintime != null ? begintime.hashCode() : 0);
        result = 31 * result + (endtime != null ? endtime.hashCode() : 0);
        result = 31 * result + (applydate != null ? applydate.hashCode() : 0);
        result = 31 * result + (updatedate != null ? updatedate.hashCode() : 0);
        result = 31 * result + (isgoods != null ? isgoods.hashCode() : 0);
        result = 31 * result + (outid != null ? outid.hashCode() : 0);
        result = 31 * result + (senduser != null ? senduser.hashCode() : 0);
        result = 31 * result + (fleet != null ? fleet.hashCode() : 0);
        result = 31 * result + (issend != null ? issend.hashCode() : 0);
        result = 31 * result + (schreason != null ? schreason.hashCode() : 0);
        result = 31 * result + (schedule != null ? schedule.hashCode() : 0);
        result = 31 * result + (cancelseason != null ? cancelseason.hashCode() : 0);
        result = 31 * result + (canceluser != null ? canceluser.hashCode() : 0);
        result = 31 * result + (canceltime != null ? canceltime.hashCode() : 0);
        result = 31 * result + (savestatus != null ? savestatus.hashCode() : 0);
        result = 31 * result + (canceltype != null ? canceltype.hashCode() : 0);
        result = 31 * result + (sfgf != null ? sfgf.hashCode() : 0);
        result = 31 * result + (bgfxx != null ? bgfxx.hashCode() : 0);
        result = 31 * result + (isout != null ? isout.hashCode() : 0);
        result = 31 * result + (reasonms != null ? reasonms.hashCode() : 0);
        result = 31 * result + (isback != null ? isback.hashCode() : 0);
        result = 31 * result + (usetype != null ? usetype.hashCode() : 0);
        result = 31 * result + (reasonCode != null ? reasonCode.hashCode() : 0);
        result = 31 * result + (reasonText != null ? reasonText.hashCode() : 0);
        result = 31 * result + (statusCode != null ? statusCode.hashCode() : 0);
        result = 31 * result + (statusText != null ? statusText.hashCode() : 0);
        return result;
    }
}
