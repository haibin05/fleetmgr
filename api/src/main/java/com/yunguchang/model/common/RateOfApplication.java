package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by gongy on 2015/11/3.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class RateOfApplication {
    private Boolean regularApplication=true;
    private Boolean regularBookingTime=true;
    private Boolean regularRoute=true;

    /**
     * 用车申请是否规范
     * @return
     */
    public Boolean getRegularApplication() {
        return regularApplication;
    }

    public void setRegularApplication(Boolean regularApplication) {
        this.regularApplication = regularApplication;
    }

    /**
     * 用车时间是否规范
     * @return
     */
    public Boolean getRegularBookingTime() {
        return regularBookingTime;
    }

    public void setRegularBookingTime(Boolean regularBookingTime) {
        this.regularBookingTime = regularBookingTime;
    }

    /**
     * 用车路线是否规范
     * @return
     */
    public Boolean getRegularRoute() {
        return regularRoute;
    }

    public void setRegularRoute(Boolean regularRoute) {
        this.regularRoute = regularRoute;
    }
}
