package com.yunguchang.model.common;

/**
 * Created by 禕 on 2015/9/24.
 */
public class LicenseVehicleMapping {
    private String  licenseClass;
    private String vehicleType;

    public String getLicenseClass() {
        return licenseClass;
    }

    public void setLicenseClass(String licenseClass) {
        this.licenseClass = licenseClass;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public LicenseVehicleMapping(String licenseClass, String vehicleType) {
        this.licenseClass = licenseClass;
        this.vehicleType = vehicleType;
    }

    public LicenseVehicleMapping() {
    }

    @Override
    public String toString() {
        return "LicenseVehicleMapping{" +
                "licenseClass='" + licenseClass + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                '}';
    }
}
