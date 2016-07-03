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
@Table(name = "t_bus_no_returning_depot",
        indexes = {
                @Index(name = "FK_kacmk6bkva1g0klfxmi9dbes9", columnList = "carId", unique = false),
        }
)

@FilterDefs(
        {
                @FilterDef(name = "filter_bus_no_return_fleet", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "carid in " +
                        "(SELECT car.id FROM t_az_carinfo car " +
                        "where car.SSCD like " +
                        " (select CONCAT(u.deptid, '%') from " +
                        "        t_sys_user u where u.userid=:userId" +
                        " )" +
                        ")"
                ),
                @FilterDef(name = "filter_bus_no_return_all", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "1=1"),
                @FilterDef(name = "filter_bus_no_return_driver", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "carid in " +
                        "(select car.id from t_az_carinfo car where car.JSY = :userId)"),
                @FilterDef(name = "filter_bus_no_return_none", parameters = {@ParamDef(name = "userId", type = "string")}, defaultCondition = "1=0"),

        }
)

@Filters({
        @Filter(name = "filter_bus_no_return_fleet"),
        @Filter(name = "filter_bus_no_return_all"),
        @Filter(name = "filter_bus_no_return_driver"),
        @Filter(name = "filter_bus_no_return_none")
})
public class TBusNoReturningDepotEntity implements Serializable {
    @Id
    @Column(name = "UUID")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",
            strategy = "uuid")
    private String uuid;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="carId")
    private TAzCarinfoEntity car;

    private DateTime dateTime;

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

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusNoReturningDepotEntity that = (TBusNoReturningDepotEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (car != null ? !car.equals(that.car) : that.car != null) return false;
        return !(dateTime != null ? !dateTime.equals(that.dateTime) : that.dateTime != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (car != null ? car.hashCode() : 0);
        result = 31 * result + (dateTime != null ? dateTime.hashCode() : 0);
        return result;
    }
}
