package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * 司机
 */
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)

public class Driver {
    private String id;
    private String name;
    private String mobile;
    private String phone;
    private DriverStatus status;
    private String licenseClass;
    private String useCarType;
    private String[] namePinyin;
    private String internalLincenseClassCode;

    private Fleet fleet;

    public Fleet getFleet() {
        return fleet;
    }

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }

    /**
     * 司机Id
     *
     * @return 司机Id
     */
    @DocumentationExample("1009269")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 司机姓名
     *
     * @return 司机姓名
     */
    @DocumentationExample("周明明")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * 司机手机号码
     *
     * @return 司机手机号码
     */
    @DocumentationExample("13916770000")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 短号码
     * @return
     */
    @DocumentationExample("627449")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *  状态
     *
     * @return
     */
    @DocumentationExample("1")
    public DriverStatus getDriverStatus() {
        return status;
    }

    public void setDriverStatus(DriverStatus driverStatus) {
        this.status = driverStatus;
    }

    /**
     * 驾照类型
     *
     * @return
     */
    @DocumentationExample("A1")
    public String getLicenseClass() {
        return licenseClass;
    }

    public void setLicenseClass(String status) {
        this.licenseClass = status;
    }

    @DocumentationExample("01")
    public String getInternalLincenseClassCode() {
        return internalLincenseClassCode;
    }

    public void setInternalLicenseClassCode(String internalLincenseClassCode) {
        this.internalLincenseClassCode = internalLincenseClassCode;
    }

    /**
     * 当前使用车类型
     *
     * @return
     */
    public String getUseCarType() {
        return useCarType;
    }

    public void setUseCarType(String useCarType) {
        this.useCarType = useCarType;
    }

    public void setNamePinyin(String[] namePinyin) {
        this.namePinyin = namePinyin;
    }

    public String[] getNamePinyin() {
        return namePinyin;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", licenseClass='" + licenseClass + '\'' +
                ", useCarType='" + useCarType + '\'' +
                ", internalLincenseClassCode='" + internalLincenseClassCode + '\'' +
                '}';
    }
}
