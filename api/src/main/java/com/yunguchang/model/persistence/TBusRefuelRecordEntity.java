package com.yunguchang.model.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_bus_refuel_record")
public class TBusRefuelRecordEntity implements Serializable {
    @Id
    @Column(name = "UUID")
    private String uuid;
    private String carno;
    private String driverno;
    private Timestamp refueltime;
    private BigDecimal mileage;
    private BigDecimal unitprice;
    private BigDecimal money;
    private String recorder;
    private Timestamp recordtime;
    private BigDecimal refueltype;
    private String carorg;
    private String drivername;
    private String carid;
    private String cardid;
    private String oilnumber;
    private BigDecimal fuelrise;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getCarno() {
        return carno;
    }

    public void setCarno(String carno) {
        this.carno = carno;
    }


    public String getDriverno() {
        return driverno;
    }

    public void setDriverno(String driverno) {
        this.driverno = driverno;
    }


    public Timestamp getRefueltime() {
        return refueltime;
    }

    public void setRefueltime(Timestamp refueltime) {
        this.refueltime = refueltime;
    }


    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }


    public BigDecimal getUnitprice() {
        return unitprice;
    }

    public void setUnitprice(BigDecimal unitprice) {
        this.unitprice = unitprice;
    }


    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }


    public String getRecorder() {
        return recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }


    public Timestamp getRecordtime() {
        return recordtime;
    }

    public void setRecordtime(Timestamp recordtime) {
        this.recordtime = recordtime;
    }


    public BigDecimal getRefueltype() {
        return refueltype;
    }

    public void setRefueltype(BigDecimal refueltype) {
        this.refueltype = refueltype;
    }


    public String getCarorg() {
        return carorg;
    }

    public void setCarorg(String carorg) {
        this.carorg = carorg;
    }


    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }


    public String getCarid() {
        return carid;
    }

    public void setCarid(String carid) {
        this.carid = carid;
    }


    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }


    public String getOilnumber() {
        return oilnumber;
    }

    public void setOilnumber(String oilnumber) {
        this.oilnumber = oilnumber;
    }


    public BigDecimal getFuelrise() {
        return fuelrise;
    }

    public void setFuelrise(BigDecimal fuelrise) {
        this.fuelrise = fuelrise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusRefuelRecordEntity that = (TBusRefuelRecordEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (carno != null ? !carno.equals(that.carno) : that.carno != null) return false;
        if (driverno != null ? !driverno.equals(that.driverno) : that.driverno != null) return false;
        if (refueltime != null ? !refueltime.equals(that.refueltime) : that.refueltime != null) return false;
        if (mileage != null ? !mileage.equals(that.mileage) : that.mileage != null) return false;
        if (unitprice != null ? !unitprice.equals(that.unitprice) : that.unitprice != null) return false;
        if (money != null ? !money.equals(that.money) : that.money != null) return false;
        if (recorder != null ? !recorder.equals(that.recorder) : that.recorder != null) return false;
        if (recordtime != null ? !recordtime.equals(that.recordtime) : that.recordtime != null) return false;
        if (refueltype != null ? !refueltype.equals(that.refueltype) : that.refueltype != null) return false;
        if (carorg != null ? !carorg.equals(that.carorg) : that.carorg != null) return false;
        if (drivername != null ? !drivername.equals(that.drivername) : that.drivername != null) return false;
        if (carid != null ? !carid.equals(that.carid) : that.carid != null) return false;
        if (cardid != null ? !cardid.equals(that.cardid) : that.cardid != null) return false;
        if (oilnumber != null ? !oilnumber.equals(that.oilnumber) : that.oilnumber != null) return false;
        if (fuelrise != null ? !fuelrise.equals(that.fuelrise) : that.fuelrise != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (carno != null ? carno.hashCode() : 0);
        result = 31 * result + (driverno != null ? driverno.hashCode() : 0);
        result = 31 * result + (refueltime != null ? refueltime.hashCode() : 0);
        result = 31 * result + (mileage != null ? mileage.hashCode() : 0);
        result = 31 * result + (unitprice != null ? unitprice.hashCode() : 0);
        result = 31 * result + (money != null ? money.hashCode() : 0);
        result = 31 * result + (recorder != null ? recorder.hashCode() : 0);
        result = 31 * result + (recordtime != null ? recordtime.hashCode() : 0);
        result = 31 * result + (refueltype != null ? refueltype.hashCode() : 0);
        result = 31 * result + (carorg != null ? carorg.hashCode() : 0);
        result = 31 * result + (drivername != null ? drivername.hashCode() : 0);
        result = 31 * result + (carid != null ? carid.hashCode() : 0);
        result = 31 * result + (cardid != null ? cardid.hashCode() : 0);
        result = 31 * result + (oilnumber != null ? oilnumber.hashCode() : 0);
        result = 31 * result + (fuelrise != null ? fuelrise.hashCode() : 0);
        return result;
    }
}
