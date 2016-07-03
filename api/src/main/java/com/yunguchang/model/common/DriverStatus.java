package com.yunguchang.model.common;

/**
 * Created by WHB on 2015-09-17.
 */
public enum DriverStatus {

    /**
     * 所有
     */
    ALL(-1),

    /**
     * 出车
     */
    WORK(1),

    /**
     * 待命
     */
    IDLE(2),

    /**
     * 请假
     */
    LEAVE(5),

    /**
     * 休息
     */
    REST(6);

    private  int status;

    DriverStatus(int status) { this.status = status; }


    @Override
    public String toString() {
        return this.name();
    }


    public static DriverStatus valueOf(Integer value){
        switch (value) {
            case -1 : return ALL;
            case 1 : return WORK;
            case 2 : return IDLE;
            case 5 : return LEAVE;
            case 6: return REST;
            default:return LEAVE;
        }
    }
}
