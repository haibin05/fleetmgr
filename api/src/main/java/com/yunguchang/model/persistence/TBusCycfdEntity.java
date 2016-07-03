package com.yunguchang.model.persistence;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_bus_cycfd")
public class TBusCycfdEntity implements Serializable {
    @Id
    @Column(name = "UUID")
    private String uuid;
    private String name;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBusCycfdEntity that = (TBusCycfdEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
