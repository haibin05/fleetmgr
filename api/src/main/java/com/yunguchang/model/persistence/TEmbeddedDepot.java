package com.yunguchang.model.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by 禕 on 2015/10/17.
 */
@Embeddable
public class TEmbeddedDepot implements Serializable {


    /**
     * 驻车点名称
     */
    @Column(name = "DEPOTNAME")
    private String name;

    /**
     * 驻车点 经度
     */
    @Column(name = "DEPOTLONGITUDE")
    private Double longitude;

    /**
     * 驻车点 纬度
     */
    @Column(name = "DEPOTLATITUDE")
    private Double latitude;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TEmbeddedDepot that = (TEmbeddedDepot) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) return false;
        return !(latitude != null ? !latitude.equals(that.latitude) : that.latitude != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        return result;
    }
}
