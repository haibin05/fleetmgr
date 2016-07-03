package com.yunguchang.model.sync;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * 车辆信息
 */
@JsonSerialize
public class SyncCar {
    private String cphm;    //车牌号
    private String sscd;    //所属车队
    private String driver;  //驾驶员编号
    private String gps;     // 1/0  是否安装GPS 1-安装
    private String carType; //  “05”
    private boolean inner; //  false

    /**
     * 车牌号码
     *
     * @return
     */
    @DocumentationExample("浙F123456")
    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }

    /**
     * 所属车队
     *
     * @return
     */
    @DocumentationExample("0010701")
    public String getSscd() {
        return sscd;
    }

    public void setSscd(String sscd) {
        this.sscd = sscd;
    }

    /**
     * 驾驶员编号
     *
     * @return
     */
    @DocumentationExample("107123")
    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * 是否安装GPS
     * 0 未安装
     * 1 已安装
     *
     * @return
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    @DocumentationExample("1")
    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    /**
     * 车辆类型
     *
     * @return
     */
    @DocumentationExample("05")
    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    @DocumentationExample("内部移交")
    public boolean isInner() {
        return inner;
    }

    public void setInner(boolean inner) {
        this.inner = inner;
    }
}
