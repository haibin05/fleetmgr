package com.yunguchang.model.persistence;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_bus_approve_sug",
        indexes = {
                @Index(name = "FK_3901p3wed1lhuhm7u2xgedt4f", columnList = "APPLYNO", unique = true),
                @Index(name = "FK_tfj18qgpukkrnijqnjrlr9nn4", columnList = "USERID", unique = false),
        }
)
public class TBusApproveSugEntity implements Serializable {
    @Id
    @Column(name = "UUID")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    private String uuid = UUID.randomUUID().toString().replace("-", "");

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "applyno", referencedColumnName = "applyno")
    private TBusApplyinfoEntity application;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "userid", referencedColumnName = "userid")// 审核人
    private TSysUserEntity user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "orgid")
    private TSysOrgEntity department;

    private String suggest;
    private String remark;
    private Timestamp operatedate;
    private Boolean updateBySync = false;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public TBusApplyinfoEntity getApplication() {
        return application;
    }

    public void setApplication(TBusApplyinfoEntity application) {
        this.application = application;
    }

    public TSysUserEntity getUser() {
        return user;
    }

    public void setUser(TSysUserEntity user) {
        this.user = user;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public Timestamp getOperatedate() {
        return operatedate;
    }

    public void setOperatedate(Timestamp operatedate) {
        this.operatedate = operatedate;
    }

    public Boolean getUpdateBySync() {
        return updateBySync;
    }

    public void setUpdateBySync(Boolean updateBySync) {
        this.updateBySync = updateBySync;
    }

    public TSysOrgEntity getDepartment() {
        return department;
    }

    public void setDepartment(TSysOrgEntity department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusApproveSugEntity that = (TBusApproveSugEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (application != null ? !application.equals(that.application) : that.application != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (suggest != null ? !suggest.equals(that.suggest) : that.suggest != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (operatedate != null ? !operatedate.equals(that.operatedate) : that.operatedate != null) return false;
        return updateBySync != null ? updateBySync.equals(that.updateBySync) : that.updateBySync == null;

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (application != null ? application.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (suggest != null ? suggest.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (operatedate != null ? operatedate.hashCode() : 0);
        result = 31 * result + (updateBySync != null ? updateBySync.hashCode() : 0);
        return result;
    }
}
