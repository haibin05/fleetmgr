package com.yunguchang.model.persistence;

import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by gongy on 9/27/2015.
 */

@Entity
@Table(name = "t_bus_returning_depot",
        indexes = {
                @Index(name = "FK_29jgwmurnijo57bsc7qn0arfq", columnList = "carId", unique = false),
        }
)

@FilterDefs(
        {
                @FilterDef(name = "filter_bus_return_fleet", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "carid in " +
                        "(SELECT car.id FROM t_az_carinfo car " +
                        "where car.SSCD like " +
                        " (select CONCAT(u.deptid, '%') from " +
                        "        t_sys_user u where u.userid=:userId" +
                        " )" +
                        ")"
                ),
                @FilterDef(name = "filter_bus_return_all", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "1=1"),
                @FilterDef(name = "filter_bus_return_driver", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "carid in " +
                        "(select car.id from t_az_carinfo car where car.JSY = :userId)"),
                @FilterDef(name = "filter_bus_return_none", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "1=0"),

        }
)

@Filters({
        @Filter(name = "filter_bus_return_fleet"),
        @Filter(name = "filter_bus_return_all"),
        @Filter(name = "filter_bus_return_driver"),
        @Filter(name = "filter_bus_return_none")
})
public class TBusReturningDepotEntity implements Serializable {
    @Id
    @Column(name = "UUID")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",
            strategy = "uuid")
    private String uuid;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="carId")
    private TAzCarinfoEntity car;

    private DateTime returnTime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public TAzCarinfoEntity getCar() {
        return car;
    }

    public void setCar(TAzCarinfoEntity car) {
        this.car = car;
    }

    public DateTime getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(DateTime returnTime) {
        this.returnTime = returnTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusReturningDepotEntity that = (TBusReturningDepotEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (car != null ? !car.equals(that.car) : that.car != null) return false;
        return !(returnTime != null ? !returnTime.equals(that.returnTime) : that.returnTime != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (car != null ? car.hashCode() : 0);
        result = 31 * result + (returnTime != null ? returnTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TBusReturningDepotEntity{" +
                "uuid='" + uuid + '\'' +
                ", car=" + car +
                ", returnTime=" + returnTime +
                '}';
    }
}
