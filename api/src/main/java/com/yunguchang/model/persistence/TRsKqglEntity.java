package com.yunguchang.model.persistence;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by WHB on 2015-09-20.
 * 考勤管理信息表
 */
@Entity
@Table(name = "t_rs_kqgl",
        indexes = {
                @Index(name = "FK_4b99jxxbi516gqjv9kq7y9vhh", columnList = "ORGID", unique = false),
                @Index(name = "index3", columnList = "DRIVERID", unique = false),
                @Index(name = "index4", columnList = "KQDATE", unique = false),
        }
)
@FilterDefs(
        {
                @FilterDef(name = "filter_attendance_fleet", parameters = {@ParamDef(name = "userId", type = "string")} ,defaultCondition = "ORGID like " +
                        "   (" +
                        "   select CONCAT(u.deptid, '%') from t_sys_user u where u.userid=:userId " +
                        "   )"
                ),
                @FilterDef(name = "filter_attendance_all", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="1=1"),
                @FilterDef(name = "filter_attendance_driver", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="driverid = :userId"),
                @FilterDef(name = "filter_attendance_none", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="1 = 0")

        }
)

@Filters({
        @Filter(name = "filter_attendance_fleet"),
        @Filter(name = "filter_attendance_all"),
        @Filter(name = "filter_attendance_driver"),
        @Filter(name = "filter_attendance_none")
})

public class TRsKqglEntity implements Serializable {

    // pk
    @Id
    @Column(name = "UUID")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String UUID;

    // 车队id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGID")
    private TSysOrgEntity department;

    // 驾驶员id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVERID")
    private TRsDriverinfoEntity driver;

    // 驾驶员姓名
    String driverName;
    // 考勤日期：格式-年月
    String kqDate;
    // 1号
    String KQ01;
    // 2号
    String KQ02;
    // 3号
    String KQ03;
    // 4号
    String KQ04;
    // 5号
    String KQ05;
    // 6号
    String KQ06;
    // 7号
    String KQ07;
    // 8号
    String KQ08;
    // 9号
    String KQ09;
    // 10号
    String KQ10;
    // 11号
    String KQ11;
    // 12号
    String KQ12;
    // 13号
    String KQ13;
    // 14号
    String KQ14;
    // 15号
    String KQ15;
    // 16号
    String KQ16;
    // 17号
    String KQ17;
    // 18号
    String KQ18;
    // 19号
    String KQ19;
    // 20号
    String KQ20;
    // 21号
    String KQ21;
    // 22号
    String KQ22;
    // 23号
    String KQ23;
    // 24号
    String KQ24;
    // 25号
    String KQ25;
    // 26号
    String KQ26;
    // 27号
    String KQ27;
    // 28号
    String KQ28;
    // 29号
    String KQ29;
    // 30号
    String KQ30;
    // 31号
    String KQ31;
    // 提交状态 1：提交；0：未提交
    String status;
    // 更新日期
    Date updateDate;
    // 更新人
    String updateUser;
    // 病假
    Integer bjCount;
    // 事假
    Integer sjCount;
    // 加班
    Integer jbCount;
    // 拖班
    Integer tbCount;
    // 抢修
    Integer qxCount;
    // 年假
    Integer njCount;
    // 出勤
    Integer cqCount;
    // 公休
    Integer gxCount;
    // 调休
    Integer txCount;
    // 出差
    Integer ccCount;
    // 通宵班
    Integer ybCount;


    // getter and setter

    public Integer getYbCount() {
        return ybCount;
    }

    public void setYbCount(Integer ybCount) {
        this.ybCount = ybCount;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public TSysOrgEntity getDepartment() {
        return department;
    }

    public void setDepartment(TSysOrgEntity orgId) {
        this.department = orgId;
    }

    public TRsDriverinfoEntity getDriver() {
        return driver;
    }

    public void setDriver(TRsDriverinfoEntity driver) {
        this.driver = driver;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getKqDate() {
        return kqDate;
    }

    public void setKqDate(String kqDate) {
        this.kqDate = kqDate;
    }

    public String getKQ01() {
        return KQ01;
    }

    public void setKQ01(String KQ01) {
        this.KQ01 = KQ01;
    }

    public String getKQ02() {
        return KQ02;
    }

    public void setKQ02(String KQ02) {
        this.KQ02 = KQ02;
    }

    public String getKQ03() {
        return KQ03;
    }

    public void setKQ03(String KQ03) {
        this.KQ03 = KQ03;
    }

    public String getKQ04() {
        return KQ04;
    }

    public void setKQ04(String KQ04) {
        this.KQ04 = KQ04;
    }

    public String getKQ05() {
        return KQ05;
    }

    public void setKQ05(String KQ05) {
        this.KQ05 = KQ05;
    }

    public String getKQ06() {
        return KQ06;
    }

    public void setKQ06(String KQ06) {
        this.KQ06 = KQ06;
    }

    public String getKQ07() {
        return KQ07;
    }

    public void setKQ07(String KQ07) {
        this.KQ07 = KQ07;
    }

    public String getKQ08() {
        return KQ08;
    }

    public void setKQ08(String KQ08) {
        this.KQ08 = KQ08;
    }

    public String getKQ09() {
        return KQ09;
    }

    public void setKQ09(String KQ09) {
        this.KQ09 = KQ09;
    }

    public String getKQ10() {
        return KQ10;
    }

    public void setKQ10(String KQ10) {
        this.KQ10 = KQ10;
    }

    public String getKQ11() {
        return KQ11;
    }

    public void setKQ11(String KQ11) {
        this.KQ11 = KQ11;
    }

    public String getKQ12() {
        return KQ12;
    }

    public void setKQ12(String KQ12) {
        this.KQ12 = KQ12;
    }

    public String getKQ13() {
        return KQ13;
    }

    public void setKQ13(String KQ13) {
        this.KQ13 = KQ13;
    }

    public String getKQ14() {
        return KQ14;
    }

    public void setKQ14(String KQ14) {
        this.KQ14 = KQ14;
    }

    public String getKQ15() {
        return KQ15;
    }

    public void setKQ15(String KQ15) {
        this.KQ15 = KQ15;
    }

    public String getKQ16() {
        return KQ16;
    }

    public void setKQ16(String KQ16) {
        this.KQ16 = KQ16;
    }

    public String getKQ17() {
        return KQ17;
    }

    public void setKQ17(String KQ17) {
        this.KQ17 = KQ17;
    }

    public String getKQ18() {
        return KQ18;
    }

    public void setKQ18(String KQ18) {
        this.KQ18 = KQ18;
    }

    public String getKQ19() {
        return KQ19;
    }

    public void setKQ19(String KQ19) {
        this.KQ19 = KQ19;
    }

    public String getKQ20() {
        return KQ20;
    }

    public void setKQ20(String KQ20) {
        this.KQ20 = KQ20;
    }

    public String getKQ21() {
        return KQ21;
    }

    public void setKQ21(String KQ21) {
        this.KQ21 = KQ21;
    }

    public String getKQ22() {
        return KQ22;
    }

    public void setKQ22(String KQ22) {
        this.KQ22 = KQ22;
    }

    public String getKQ23() {
        return KQ23;
    }

    public void setKQ23(String KQ23) {
        this.KQ23 = KQ23;
    }

    public String getKQ24() {
        return KQ24;
    }

    public void setKQ24(String KQ24) {
        this.KQ24 = KQ24;
    }

    public String getKQ25() {
        return KQ25;
    }

    public void setKQ25(String KQ25) {
        this.KQ25 = KQ25;
    }

    public String getKQ26() {
        return KQ26;
    }

    public void setKQ26(String KQ26) {
        this.KQ26 = KQ26;
    }

    public String getKQ27() {
        return KQ27;
    }

    public void setKQ27(String KQ27) {
        this.KQ27 = KQ27;
    }

    public String getKQ28() {
        return KQ28;
    }

    public void setKQ28(String KQ28) {
        this.KQ28 = KQ28;
    }

    public String getKQ29() {
        return KQ29;
    }

    public void setKQ29(String KQ29) {
        this.KQ29 = KQ29;
    }

    public String getKQ30() {
        return KQ30;
    }

    public void setKQ30(String KQ30) {
        this.KQ30 = KQ30;
    }

    public String getKQ31() {
        return KQ31;
    }

    public void setKQ31(String KQ31) {
        this.KQ31 = KQ31;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Integer getBjCount() {
        return bjCount;
    }

    public void setBjCount(Integer bjCount) {
        this.bjCount = bjCount;
    }

    public Integer getSjCount() {
        return sjCount;
    }

    public void setSjCount(Integer sjCount) {
        this.sjCount = sjCount;
    }

    public Integer getJbCount() {
        return jbCount;
    }

    public void setJbCount(Integer jbCount) {
        this.jbCount = jbCount;
    }

    public Integer getTbCount() {
        return tbCount;
    }

    public void setTbCount(Integer tbCount) {
        this.tbCount = tbCount;
    }

    public Integer getQxCount() {
        return qxCount;
    }

    public void setQxCount(Integer qxCount) {
        this.qxCount = qxCount;
    }

    public Integer getNjCount() {
        return njCount;
    }

    public void setNjCount(Integer njCount) {
        this.njCount = njCount;
    }

    public Integer getCqCount() {
        return cqCount;
    }

    public void setCqCount(Integer cqCount) {
        this.cqCount = cqCount;
    }

    public Integer getGxCount() {
        return gxCount;
    }

    public void setGxCount(Integer gxCount) {
        this.gxCount = gxCount;
    }

    public Integer getTxCount() {
        return txCount;
    }

    public void setTxCount(Integer txCount) {
        this.txCount = txCount;
    }

    public Integer getCcCount() {
        return ccCount;
    }

    public void setCcCount(Integer ccCount) {
        this.ccCount = ccCount;
    }
}
