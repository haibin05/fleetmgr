package com.yunguchang.model.persistence;

import org.hibernate.annotations.*;
import org.jboss.marshalling.serial.Serial;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

/**
 * 用车负责人信息表
 * <p/>
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_bus_mainuserinfo",
        indexes = {
                @Index(name = "FK_bcbvhwvwe64ts7ffn5k0jpt40", columnList = "BMID", unique = false),
                @Index(name = "FK_b84x365rvvwahgybm1brblq3h", columnList = "USERID", unique = false),
        }
)
@FilterDefs(
        {
                @FilterDef(name = "filter_passengers_fleet", parameters = {@ParamDef(name = "userId", type = "string")}
                        ,defaultCondition = "exists (" +
                        " select r.* from t_bus_business_rela r " +
                        "   where r.orgid " +
                        "        like (" +
                        "                select CONCAT(u.deptid,'%' ) from t_sys_user u" +
                        "                where u.userid=:userId" +
                        "             )" +
                        "   and bmid like concat(r.busorgid,'%') " +
                        ")"
                ),
                @FilterDef(name = "filter_passengers_all", parameters = {@ParamDef(name = "userId", type = "string")}
                        ,defaultCondition="1=1"),
                @FilterDef(name = "filter_passengers_coordinator", parameters = {@ParamDef(name = "userId", type = "string")}
                        ,defaultCondition = "bmid like " +
                        "   (" +
                        "   select CONCAT(u.deptid, '%') from t_sys_user u INNER JOIN t_sys_user_role ur ON ur.userid=u.userid" +
                        " INNER JOIN t_sys_role r ON r.roleid = ur.roleid and r.rolename = '用车申请人' where u.userid=:userId" +
                        "   )"
                ),
                @FilterDef(name = "filter_passengers_none", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="1 = 0")

        }
)

@Filters({
        @Filter(name = "filter_passengers_fleet"),
        @Filter(name = "filter_passengers_all"),
        @Filter(name = "filter_passengers_coordinator"),
        @Filter(name = "filter_passengers_none")
})
public class TBusMainUserInfoEntity implements Serializable {
    // 主键ID
    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    private String uuid = UUID.randomUUID().toString().replace("-", "");

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId")
    // 调度人（）
    private TSysUserEntity dispatcher;
    // 用车负责人姓名
    @Column(length = 100)
    private String name;
    // 用车负责人手机号
    @Column(length = 11)
    private String mobile;
    // 用车负责人短号
    @Column(length = 8)
    private String phone;
    // 所属班组
    @Column(length = 32)
    private String belongGroup;
    // 人员编号
    @Column(length = 32)
    private String code;
    // 用车负责人手机号（备用）
    @Column(length = 11)
    private String mobile2;
    // 座机号
    @Column(length = 11)
    private String mobile3;

    // 部门ID
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="bmid")
    private TSysOrgEntity sysOrg;


    // 启用状态(1:启用;0:停用)
    private String enabled;


    // getter and setter
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBelongGroup() {
        return belongGroup;
    }

    public void setBelongGroup(String belongGroup) {
        this.belongGroup = belongGroup;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getMobile3() {
        return mobile3;
    }

    public void setMobile3(String mobile3) {
        this.mobile3 = mobile3;
    }

    public TSysOrgEntity getSysOrg() {
        return sysOrg;
    }

    public void setSysOrg(TSysOrgEntity sysOrg) {
        this.sysOrg = sysOrg;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public TSysUserEntity getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(TSysUserEntity dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusMainUserInfoEntity that = (TBusMainUserInfoEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (mobile != null ? !mobile.equals(that.mobile) : that.mobile != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (belongGroup != null ? !belongGroup.equals(that.belongGroup) : that.belongGroup != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (mobile2 != null ? !mobile2.equals(that.mobile2) : that.mobile2 != null) return false;
        if (mobile3 != null ? !mobile3.equals(that.mobile3) : that.mobile3 != null) return false;
        if (sysOrg != null ? !sysOrg.equals(that.sysOrg) : that.sysOrg != null) return false;
        return !(enabled != null ? !enabled.equals(that.enabled) : that.enabled != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (belongGroup != null ? belongGroup.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (mobile2 != null ? mobile2.hashCode() : 0);
        result = 31 * result + (mobile3 != null ? mobile3.hashCode() : 0);
        result = 31 * result + (sysOrg != null ? sysOrg.hashCode() : 0);
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        return result;
    }
}
