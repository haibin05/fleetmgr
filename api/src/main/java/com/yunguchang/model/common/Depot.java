package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * Created by WHB on 2015-09-22.
 * 停车场
 *
 */
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)

public class Depot {

    // 停车场名称
    @DocumentationExample("嘉兴市唐山路")
    private String name;

    // 经度
    @DocumentationExample("120.71429153644")
    private double lng;

    // 纬度
    @DocumentationExample("30.768255455628")
    private double lat;





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "Depot{" +
                "name='" + name + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                '}';
    }
}
