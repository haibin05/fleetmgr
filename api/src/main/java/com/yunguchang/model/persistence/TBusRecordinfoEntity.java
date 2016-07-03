package com.yunguchang.model.persistence;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@javax.persistence.Table(name = "t_bus_recordinfo",
        indexes = {
                @Index(name = "FK_717mo8xql6k2llqsekunpxtfb", columnList = "OPERATEUSER", unique = false),
                @Index(name = "FK_e1rxg4ffcobg8hg5oiwyfkvfe", columnList = "SCHEDULEID", unique = false),
                @Index(name = "FK_npvq3gejye7uwfv2um0fjjhd4", columnList = "CARNO", unique = false),
                @Index(name = "FK_smobcog2brno6cjpw7seluagt", columnList = "DRIVERNO", unique = false),
                @Index(name = "FK_npvq3gejye7uwfv2um0fjjhd4", columnList = "CARNO", unique = false),
        }
)
public class TBusRecordinfoEntity implements Serializable {
    @Id
    @javax.persistence.Column(name = "UUID")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "assigned")
    private String uuid = UUID.randomUUID().toString().replace("-", "");

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="scheduleid")
    private  TBusScheduleCarEntity scheduleCar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="carno")
    private TAzCarinfoEntity car;

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="driverno")
    private TRsDriverinfoEntity driver;
    private DateTime starttime;
    private DateTime endtime;
    private Double startmile;
    private Double endmile;
    private Double mileage;
    private Double trancost;
    private Double staycost;
    private Double stopcost;
    private Double refuelcost;
    private Double othercost;
    private Double tbprice;
    private Double costsum;
    private Double moretime;
    private Double moretimeprice;
    private Double moretimecostsum;
    private Double moremile;
    private Double moremileprice;
    private Double moremilecostsum;
    private Double total;
    private String remark;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="operateuser")
    private TSysUserEntity operateuser;

    private DateTime operatedate;
    private DateTime updatedate;
    private String status;
    private Double yj;
    private Double milecostsum;
    private String yjlx;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="relano")
    private TBusScheduleRelaEntity schedule;
    private String oilnumber;
    private Double fuelrise;


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


    public Double getStartmile() {
        return startmile;
    }

    public void setStartmile(Double startmile) {
        this.startmile = startmile;
    }


    public Double getEndmile() {
        return endmile;
    }

    public void setEndmile(Double endmile) {
        this.endmile = endmile;
    }


    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }


    public Double getTrancost() {
        return trancost;
    }

    public void setTrancost(Double trancost) {
        this.trancost = trancost;
    }


    public Double getStaycost() {
        return staycost;
    }

    public void setStaycost(Double staycost) {
        this.staycost = staycost;
    }


    public Double getStopcost() {
        return stopcost;
    }

    public void setStopcost(Double stopcost) {
        this.stopcost = stopcost;
    }


    public Double getRefuelcost() {
        return refuelcost;
    }

    public void setRefuelcost(Double refuelcost) {
        this.refuelcost = refuelcost;
    }


    public Double getOthercost() {
        return othercost;
    }

    public void setOthercost(Double othercost) {
        this.othercost = othercost;
    }


    public Double getTbprice() {
        return tbprice;
    }

    public void setTbprice(Double tbprice) {
        this.tbprice = tbprice;
    }


    public Double getCostsum() {
        return costsum;
    }

    public void setCostsum(Double costsum) {
        this.costsum = costsum;
    }


    public Double getMoretime() {
        return moretime;
    }

    public void setMoretime(Double moretime) {
        this.moretime = moretime;
    }


    public Double getMoretimeprice() {
        return moretimeprice;
    }

    public void setMoretimeprice(Double moretimeprice) {
        this.moretimeprice = moretimeprice;
    }


    public Double getMoretimecostsum() {
        return moretimecostsum;
    }

    public void setMoretimecostsum(Double moretimecostsum) {
        this.moretimecostsum = moretimecostsum;
    }


    public Double getMoremile() {
        return moremile;
    }

    public void setMoremile(Double moremile) {
        this.moremile = moremile;
    }


    public Double getMoremileprice() {
        return moremileprice;
    }

    public void setMoremileprice(Double moremileprice) {
        this.moremileprice = moremileprice;
    }


    public Double getMoremilecostsum() {
        return moremilecostsum;
    }

    public void setMoremilecostsum(Double moremilecostsum) {
        this.moremilecostsum = moremilecostsum;
    }


    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public TSysUserEntity getOperateuser() {
        return operateuser;
    }

    public void setOperateuser(TSysUserEntity operateuser) {
        this.operateuser = operateuser;
    }


    public DateTime getOperatedate() {
        return operatedate;
    }

    public void setOperatedate(DateTime operatedate) {
        this.operatedate = operatedate;
    }


    public DateTime getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(DateTime updatedate) {
        this.updatedate = updatedate;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Double getYj() {
        return yj;
    }

    public void setYj(Double yj) {
        this.yj = yj;
    }


    public Double getMilecostsum() {
        return milecostsum;
    }

    public void setMilecostsum(Double milecostsum) {
        this.milecostsum = milecostsum;
    }


    public String getYjlx() {
        return yjlx;
    }

    public void setYjlx(String yjlx) {
        this.yjlx = yjlx;
    }


    public String getOilnumber() {
        return oilnumber;
    }

    public void setOilnumber(String oilnumber) {
        this.oilnumber = oilnumber;
    }


    public Double getFuelrise() {
        return fuelrise;
    }

    public void setFuelrise(Double fuelrise) {
        this.fuelrise = fuelrise;
    }

    public TBusScheduleCarEntity getScheduleCar() {
        return scheduleCar;
    }

    public void setScheduleCar(TBusScheduleCarEntity scheduleCar) {
        this.scheduleCar = scheduleCar;
    }

    public TRsDriverinfoEntity getDriver() {
        return driver;
    }

    public void setDriver(TRsDriverinfoEntity driver) {
        this.driver = driver;
    }

    public TBusScheduleRelaEntity getSchedule() {
        return schedule;
    }

    public void setSchedule(TBusScheduleRelaEntity schedule) {
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusRecordinfoEntity that = (TBusRecordinfoEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (scheduleCar != null ? !scheduleCar.equals(that.scheduleCar) : that.scheduleCar != null) return false;
        if (starttime != null ? !starttime.equals(that.starttime) : that.starttime != null) return false;
        if (endtime != null ? !endtime.equals(that.endtime) : that.endtime != null) return false;
        if (startmile != null ? !startmile.equals(that.startmile) : that.startmile != null) return false;
        if (endmile != null ? !endmile.equals(that.endmile) : that.endmile != null) return false;
        if (mileage != null ? !mileage.equals(that.mileage) : that.mileage != null) return false;
        if (trancost != null ? !trancost.equals(that.trancost) : that.trancost != null) return false;
        if (staycost != null ? !staycost.equals(that.staycost) : that.staycost != null) return false;
        if (stopcost != null ? !stopcost.equals(that.stopcost) : that.stopcost != null) return false;
        if (refuelcost != null ? !refuelcost.equals(that.refuelcost) : that.refuelcost != null) return false;
        if (othercost != null ? !othercost.equals(that.othercost) : that.othercost != null) return false;
        if (tbprice != null ? !tbprice.equals(that.tbprice) : that.tbprice != null) return false;
        if (costsum != null ? !costsum.equals(that.costsum) : that.costsum != null) return false;
        if (moretime != null ? !moretime.equals(that.moretime) : that.moretime != null) return false;
        if (moretimeprice != null ? !moretimeprice.equals(that.moretimeprice) : that.moretimeprice != null)
            return false;
        if (moretimecostsum != null ? !moretimecostsum.equals(that.moretimecostsum) : that.moretimecostsum != null)
            return false;
        if (moremile != null ? !moremile.equals(that.moremile) : that.moremile != null) return false;
        if (moremileprice != null ? !moremileprice.equals(that.moremileprice) : that.moremileprice != null)
            return false;
        if (moremilecostsum != null ? !moremilecostsum.equals(that.moremilecostsum) : that.moremilecostsum != null)
            return false;
        if (total != null ? !total.equals(that.total) : that.total != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (operateuser != null ? !operateuser.equals(that.operateuser) : that.operateuser != null) return false;
        if (operatedate != null ? !operatedate.equals(that.operatedate) : that.operatedate != null) return false;
        if (updatedate != null ? !updatedate.equals(that.updatedate) : that.updatedate != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (yj != null ? !yj.equals(that.yj) : that.yj != null) return false;
        if (milecostsum != null ? !milecostsum.equals(that.milecostsum) : that.milecostsum != null) return false;
        if (yjlx != null ? !yjlx.equals(that.yjlx) : that.yjlx != null) return false;
        if (oilnumber != null ? !oilnumber.equals(that.oilnumber) : that.oilnumber != null) return false;
        return !(fuelrise != null ? !fuelrise.equals(that.fuelrise) : that.fuelrise != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (scheduleCar != null ? scheduleCar.hashCode() : 0);
        result = 31 * result + (starttime != null ? starttime.hashCode() : 0);
        result = 31 * result + (endtime != null ? endtime.hashCode() : 0);
        result = 31 * result + (startmile != null ? startmile.hashCode() : 0);
        result = 31 * result + (endmile != null ? endmile.hashCode() : 0);
        result = 31 * result + (mileage != null ? mileage.hashCode() : 0);
        result = 31 * result + (trancost != null ? trancost.hashCode() : 0);
        result = 31 * result + (staycost != null ? staycost.hashCode() : 0);
        result = 31 * result + (stopcost != null ? stopcost.hashCode() : 0);
        result = 31 * result + (refuelcost != null ? refuelcost.hashCode() : 0);
        result = 31 * result + (othercost != null ? othercost.hashCode() : 0);
        result = 31 * result + (tbprice != null ? tbprice.hashCode() : 0);
        result = 31 * result + (costsum != null ? costsum.hashCode() : 0);
        result = 31 * result + (moretime != null ? moretime.hashCode() : 0);
        result = 31 * result + (moretimeprice != null ? moretimeprice.hashCode() : 0);
        result = 31 * result + (moretimecostsum != null ? moretimecostsum.hashCode() : 0);
        result = 31 * result + (moremile != null ? moremile.hashCode() : 0);
        result = 31 * result + (moremileprice != null ? moremileprice.hashCode() : 0);
        result = 31 * result + (moremilecostsum != null ? moremilecostsum.hashCode() : 0);
        result = 31 * result + (total != null ? total.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (operateuser != null ? operateuser.hashCode() : 0);
        result = 31 * result + (operatedate != null ? operatedate.hashCode() : 0);
        result = 31 * result + (updatedate != null ? updatedate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (yj != null ? yj.hashCode() : 0);
        result = 31 * result + (milecostsum != null ? milecostsum.hashCode() : 0);
        result = 31 * result + (yjlx != null ? yjlx.hashCode() : 0);
        result = 31 * result + (oilnumber != null ? oilnumber.hashCode() : 0);
        result = 31 * result + (fuelrise != null ? fuelrise.hashCode() : 0);
        return result;
    }
}
