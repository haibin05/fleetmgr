package com.yunguchang.model.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_az_refuel_card")
public class TAzRefuelCardEntity implements Serializable {
    @Id
    @Column(name = "UUID")
    private String uuid;
    private String cardno;
    private String cardtype;
    private String carid;
    private Timestamp insertdate;
    private String cphm;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }


    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }


    public String getCarid() {
        return carid;
    }

    public void setCarid(String carid) {
        this.carid = carid;
    }


    public Timestamp getInsertdate() {
        return insertdate;
    }

    public void setInsertdate(Timestamp insertdate) {
        this.insertdate = insertdate;
    }


    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TAzRefuelCardEntity that = (TAzRefuelCardEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (cardno != null ? !cardno.equals(that.cardno) : that.cardno != null) return false;
        if (cardtype != null ? !cardtype.equals(that.cardtype) : that.cardtype != null) return false;
        if (carid != null ? !carid.equals(that.carid) : that.carid != null) return false;
        if (insertdate != null ? !insertdate.equals(that.insertdate) : that.insertdate != null) return false;
        if (cphm != null ? !cphm.equals(that.cphm) : that.cphm != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (cardno != null ? cardno.hashCode() : 0);
        result = 31 * result + (cardtype != null ? cardtype.hashCode() : 0);
        result = 31 * result + (carid != null ? carid.hashCode() : 0);
        result = 31 * result + (insertdate != null ? insertdate.hashCode() : 0);
        result = 31 * result + (cphm != null ? cphm.hashCode() : 0);
        return result;
    }
}
