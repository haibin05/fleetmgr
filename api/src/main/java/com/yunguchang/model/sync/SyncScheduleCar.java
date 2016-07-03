package com.yunguchang.model.sync;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * Created by gongy on 2015/10/20.
 */
@JsonSerialize
public class SyncScheduleCar {
    private String uuid;
    private String cphm;
    private String carId;
    private String driverId;

    /**
     * 调度用车id
     * @return
     */
    @DocumentationExample("0044c12345674da4ab043c174cddff92")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    /**
     * 车牌号码
     * @return
     */
    @DocumentationExample("浙F13693")
    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }

    /**
     * 车辆id
     * @return
     */
    @DocumentationExample("5678c12345674da4ab043c174cddff92")
    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    /**
     * 驾驶员id
     * @return
     */
    @DocumentationExample("9876c12345674da4ab043c174cddff92")
    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    //
//    “uuid”: “调度用车id”,
//            “carId”, “car guid”,
//            “driverid”:”107001”

}
