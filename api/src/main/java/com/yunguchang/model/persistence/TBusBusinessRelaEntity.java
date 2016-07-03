package com.yunguchang.model.persistence;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_bus_business_rela",
        indexes = {
                @Index(name = "fk_s7wie3vpojea1bj481cuy8ly4", columnList = "busorgid", unique = false),
                @Index(name = "fk_f9w1tiv11olexs8m0fagt0onx", columnList = "orgid", unique = false),
                @Index(name = "fk_e105fxf87jne68t88l15xlqql", columnList = "userid", unique = false),
        }
)
public class TBusBusinessRelaEntity implements Serializable {
    @Id
    @Column(name = "UUID")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    private String uuid = UUID.randomUUID().toString().replace("-", "");

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="orgid")
    private TSysOrgEntity fleet;
    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="busorgid")
    private TSysOrgEntity busOrg;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="userid")
    private TSysUserEntity supervisor;
    // 1：统一调度；2：自行管理； 默认为 1
    private String magmodel = "1";


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

    public TSysOrgEntity getBusOrg() {
        return busOrg;
    }

    public void setBusOrg(TSysOrgEntity busOrg) {
        this.busOrg = busOrg;
    }

    public TSysUserEntity getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(TSysUserEntity supervisor) {
        this.supervisor = supervisor;
    }

    public String getMagmodel() {
        return magmodel;
    }

    public void setMagmodel(String magmodel) {
        this.magmodel = magmodel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusBusinessRelaEntity that = (TBusBusinessRelaEntity) o;

        if (busOrg != null ? !busOrg.equals(that.busOrg) : that.busOrg != null) return false;
        if (fleet != null ? !fleet.equals(that.fleet) : that.fleet != null) return false;
        if (magmodel != null ? !magmodel.equals(that.magmodel) : that.magmodel != null) return false;
        if (supervisor != null ? !supervisor.equals(that.supervisor) : that.supervisor != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (fleet != null ? fleet.hashCode() : 0);
        result = 31 * result + (busOrg != null ? busOrg.hashCode() : 0);
        result = 31 * result + (supervisor != null ? supervisor.hashCode() : 0);
        result = 31 * result + (magmodel != null ? magmodel.hashCode() : 0);
        return result;
    }
}
