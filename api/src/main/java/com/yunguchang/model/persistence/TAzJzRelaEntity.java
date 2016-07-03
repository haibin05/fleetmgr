package com.yunguchang.model.persistence;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_az_jz_rela")
public class TAzJzRelaEntity implements Serializable {
    @Id
    @Column(name = "UUID")
    private String uuid;
    private String xszcx;
    private String jzyq;



    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getXszcx() {
        return xszcx;
    }

    public void setXszcx(String xszcx) {
        this.xszcx = xszcx;
    }


    public String getJzyq() {
        return jzyq;
    }

    public void setJzyq(String jzyq) {
        this.jzyq = jzyq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TAzJzRelaEntity that = (TAzJzRelaEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (xszcx != null ? !xszcx.equals(that.xszcx) : that.xszcx != null) return false;
        if (jzyq != null ? !jzyq.equals(that.jzyq) : that.jzyq != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (xszcx != null ? xszcx.hashCode() : 0);
        result = 31 * result + (jzyq != null ? jzyq.hashCode() : 0);
        return result;
    }
}
