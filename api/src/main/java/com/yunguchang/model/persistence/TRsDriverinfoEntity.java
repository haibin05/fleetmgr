package com.yunguchang.model.persistence;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@javax.persistence.Table(name = "t_rs_driverinfo",
        indexes = {
                @Index(name = "index3", columnList = "STATUS", unique = false),
                @Index(name = "index4", columnList = "ENABLED", unique = false),
                @Index(name = "FK_hxo7uvpc3xqpk83ym43f7efxe", columnList = "ORGID", unique = false),
        }
)
@FilterDefs(
        {
                @FilterDef(name = "filter_drivers_fleet", parameters = {@ParamDef(name = "userId", type = "string")} ,defaultCondition = "ORGID like " +
                        "   (" +
                        "   select CONCAT(u.deptid, '%') from t_sys_user u where u.userid=:userId " +
                        "   )"
                ),
                @FilterDef(name = "filter_drivers_all", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="1=1"),
                @FilterDef(name = "filter_drivers_driver", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="uuid = :userId"),
                @FilterDef(name = "filter_drivers_none", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="1 = 0")

        }
)

@Filters({
        @Filter(name = "filter_drivers_fleet"),
        @Filter(name = "filter_drivers_all"),
        @Filter(name = "filter_drivers_driver"),
        @Filter(name = "filter_drivers_none")
})
public class TRsDriverinfoEntity implements Serializable {
    @Id
    @javax.persistence.Column(name = "UUID")
    private String uuid;


    private String driverno;


    private String drivername;

    // 默认为 男
    private String sex = "1";

    private String idcard;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="orgid")
    private TSysOrgEntity department;

    private String drivertype;

    private String driverstatus;

    private String mobile;

    private String phone;

    private String firstdate;

    private BigDecimal age = new BigDecimal(16);

    private Timestamp startdate;

    private Timestamp enddate;

    private String contractno;

    private String drivecartype;

    // 准驾类型
    @Formula("(select dic.datavalue from t_sys_dic dic where dic.classcode='DriverCarType' and dic.datacode = DRIVECARTYPE)")
    private String licenseClass;

    @Formula("( " +
            "select  " +
            "case day(now())  " +
            "WHEN 1 THEN k.kq01 " +
            "WHEN 2 THEN k.kq02 " +
            "WHEN 3 THEN k.kq03 " +
            "WHEN 4 THEN k.kq04 " +
            "WHEN 5 THEN k.kq05 " +
            "WHEN 6 THEN k.kq06 " +
            "WHEN 7 THEN k.kq07 " +
            "WHEN 8 THEN k.kq08 " +
            "WHEN 9 THEN k.kq09 " +
            "WHEN 10 THEN k.kq10 " +
            "WHEN 11 THEN k.kq11 " +
            "WHEN 12 THEN k.kq12 " +
            "WHEN 13 THEN k.kq13 " +
            "WHEN 14 THEN k.kq14 " +
            "WHEN 15 THEN k.kq15 " +
            "WHEN 16 THEN k.kq16 " +
            "WHEN 17 THEN k.kq17 " +
            "WHEN 18 THEN k.kq18 " +
            "WHEN 19 THEN k.kq19 " +
            "WHEN 20 THEN k.kq20 " +
            "WHEN 21 THEN k.kq21 " +
            "WHEN 22 THEN k.kq22 " +
            "WHEN 23 THEN k.kq23 " +
            "WHEN 24 THEN k.kq24 " +
            "WHEN 25 THEN k.kq25 " +
            "WHEN 26 THEN k.kq26 " +
            "WHEN 27 THEN k.kq27 " +
            "WHEN 28 THEN k.kq28 " +
            "WHEN 29 THEN k.kq29 " +
            "WHEN 30 THEN k.kq30 " +
            "WHEN 31 THEN k.kq31 " +
            "else 0 " +
            "END  " +
            "from t_rs_kqgl k  " +
            "where k.kqdate=DATE_FORMAT(now(),'%Y%m') " +
            "and k.driverid=uuid " +
            ")")
    private String leaveStatus;

    @Formula("(select count(*)<>0 from t_bus_schedule_car sc, t_bus_schedule_rela s " +
            "where sc.DRIVERID =uuid and sc.status=1 and s.status=1 and sc.RELANO=s.UUID and s.starttime<=now() and now()<=s.endtime)")
    private boolean onWorking;

    private String enabled = "1";

    private String remark;

    private String insertorg;

    private Timestamp insertdate;

    private String updateuser;

    private String updateorg;

    private Timestamp updatedate;

    private String status = "1";

    private String insertuser;

    private Timestamp birthday;

    private String zw;

    private String scdname;

    private String grade;
    @OneToMany(mappedBy = "driver",fetch = FetchType.LAZY)
    private List<TBusScheduleCarEntity> scheduleCarList;

    public String getDrivecartype() {
        return drivecartype;
    }

    public void setDrivecartype(String drivecartype) {
        this.drivecartype = drivecartype;
    }

// getter and setter

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getDriverno() {
        return driverno;
    }

    public void setDriverno(String driverno) {
        this.driverno = driverno;
    }
    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public TSysOrgEntity getDepartment() {
        return department;
    }

    public void setDepartment(TSysOrgEntity department) {
        this.department = department;
    }

    public String getDrivertype() {
        return drivertype;
    }

    public void setDrivertype(String drivertype) {
        this.drivertype = drivertype;
    }

    public String getDriverstatus() {
        return driverstatus;
    }

    public void setDriverstatus(String driverstatus) {
        this.driverstatus = driverstatus;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstdate() {
        return firstdate;
    }

    public void setFirstdate(String firstdate) {
        this.firstdate = firstdate;
    }

    public BigDecimal getAge() {
        return age;
    }

    public void setAge(BigDecimal age) {
        this.age = age;
    }
    public Timestamp getStartdate() {
        return startdate;
    }

    public void setStartdate(Timestamp startdate) {
        this.startdate = startdate;
    }


    public Timestamp getEnddate() {
        return enddate;
    }

    public void setEnddate(Timestamp enddate) {
        this.enddate = enddate;
    }


    public String getContractno() {
        return contractno;
    }

    public void setContractno(String contractno) {
        this.contractno = contractno;
    }

    public String getLicenseClass() {
        return licenseClass;
    }

    public void setLicenseClass(String driverLicence) {
        this.licenseClass = driverLicence;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getInsertorg() {
        return insertorg;
    }

    public void setInsertorg(String insertorg) {
        this.insertorg = insertorg;
    }


    public Timestamp getInsertdate() {
        return insertdate;
    }

    public void setInsertdate(Timestamp insertdate) {
        this.insertdate = insertdate;
    }


    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }


    public String getUpdateorg() {
        return updateorg;
    }

    public void setUpdateorg(String updateorg) {
        this.updateorg = updateorg;
    }


    public Timestamp getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Timestamp updatedate) {
        this.updatedate = updatedate;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getInsertuser() {
        return insertuser;
    }

    public void setInsertuser(String insertuser) {
        this.insertuser = insertuser;
    }


    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }


    public String getZw() {
        return zw;
    }

    public void setZw(String zw) {
        this.zw = zw;
    }


    public String getScdname() {
        return scdname;
    }

    public void setScdname(String scdname) {
        this.scdname = scdname;
    }


    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public List<TBusScheduleCarEntity> getScheduleCarList() {
        return scheduleCarList;
    }

    public void setScheduleCarList(List<TBusScheduleCarEntity> scheduleCarList) {
        this.scheduleCarList = scheduleCarList;
    }

    public String getLeaveStatus() {
        return leaveStatus;
    }

    public void setLeaveStatus(String leaveStatus) {
        this.leaveStatus = leaveStatus;
    }

    public boolean isOnWorking() {
        return onWorking;
    }

    public void setOnWorking(boolean onWorking) {
        this.onWorking = onWorking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TRsDriverinfoEntity that = (TRsDriverinfoEntity) o;

        if (onWorking != that.onWorking) return false;
        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (birthday != null ? !birthday.equals(that.birthday) : that.birthday != null) return false;
        if (contractno != null ? !contractno.equals(that.contractno) : that.contractno != null) return false;
        if (drivername != null ? !drivername.equals(that.drivername) : that.drivername != null) return false;
        if (driverno != null ? !driverno.equals(that.driverno) : that.driverno != null) return false;
        if (driverstatus != null ? !driverstatus.equals(that.driverstatus) : that.driverstatus != null) return false;
        if (drivertype != null ? !drivertype.equals(that.drivertype) : that.drivertype != null) return false;
        if (enabled != null ? !enabled.equals(that.enabled) : that.enabled != null) return false;
        if (enddate != null ? !enddate.equals(that.enddate) : that.enddate != null) return false;
        if (firstdate != null ? !firstdate.equals(that.firstdate) : that.firstdate != null) return false;
        if (department != null ? !department.equals(that.department) : that.department != null) return false;
        if (grade != null ? !grade.equals(that.grade) : that.grade != null) return false;
        if (idcard != null ? !idcard.equals(that.idcard) : that.idcard != null) return false;
        if (insertdate != null ? !insertdate.equals(that.insertdate) : that.insertdate != null) return false;
        if (insertorg != null ? !insertorg.equals(that.insertorg) : that.insertorg != null) return false;
        if (insertuser != null ? !insertuser.equals(that.insertuser) : that.insertuser != null) return false;
        if (leaveStatus != null ? !leaveStatus.equals(that.leaveStatus) : that.leaveStatus != null) return false;
        if (licenseClass != null ? !licenseClass.equals(that.licenseClass) : that.licenseClass != null) return false;
        if (mobile != null ? !mobile.equals(that.mobile) : that.mobile != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (scdname != null ? !scdname.equals(that.scdname) : that.scdname != null) return false;
        if (scheduleCarList != null ? !scheduleCarList.equals(that.scheduleCarList) : that.scheduleCarList != null)
            return false;
        if (sex != null ? !sex.equals(that.sex) : that.sex != null) return false;
        if (startdate != null ? !startdate.equals(that.startdate) : that.startdate != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (updatedate != null ? !updatedate.equals(that.updatedate) : that.updatedate != null) return false;
        if (updateorg != null ? !updateorg.equals(that.updateorg) : that.updateorg != null) return false;
        if (updateuser != null ? !updateuser.equals(that.updateuser) : that.updateuser != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (zw != null ? !zw.equals(that.zw) : that.zw != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (driverno != null ? driverno.hashCode() : 0);
        result = 31 * result + (drivername != null ? drivername.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (idcard != null ? idcard.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (drivertype != null ? drivertype.hashCode() : 0);
        result = 31 * result + (driverstatus != null ? driverstatus.hashCode() : 0);
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (firstdate != null ? firstdate.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (startdate != null ? startdate.hashCode() : 0);
        result = 31 * result + (enddate != null ? enddate.hashCode() : 0);
        result = 31 * result + (contractno != null ? contractno.hashCode() : 0);
        result = 31 * result + (licenseClass != null ? licenseClass.hashCode() : 0);
        result = 31 * result + (leaveStatus != null ? leaveStatus.hashCode() : 0);
        result = 31 * result + (onWorking ? 1 : 0);
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (insertorg != null ? insertorg.hashCode() : 0);
        result = 31 * result + (insertdate != null ? insertdate.hashCode() : 0);
        result = 31 * result + (updateuser != null ? updateuser.hashCode() : 0);
        result = 31 * result + (updateorg != null ? updateorg.hashCode() : 0);
        result = 31 * result + (updatedate != null ? updatedate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (insertuser != null ? insertuser.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (zw != null ? zw.hashCode() : 0);
        result = 31 * result + (scdname != null ? scdname.hashCode() : 0);
        result = 31 * result + (grade != null ? grade.hashCode() : 0);
        result = 31 * result + (scheduleCarList != null ? scheduleCarList.hashCode() : 0);
        return result;
    }
}
