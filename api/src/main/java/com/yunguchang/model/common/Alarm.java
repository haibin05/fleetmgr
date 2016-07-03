package com.yunguchang.model.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 告警对象
 */
@JsonSerialize
public enum Alarm {
    /**
     * 超速
     */
    SPEEDING(1),
    /**
     * 疲劳驾驶
     */
    TIRED(2),
    /**
     * 无任务出车
     */
    NOSCHEDULE(4),
    /**
     * 超违章出车
     */
    VIOLATION(8),
    /**
     * 修理车辆出车
     */
    INREPAIRING(16),
    /**
     * 准驾车型不符
     */
    LICENSE_MISMATCH(32),
//    /**
//     * 未回车库
//     */
//    NO_RETURN(64);

    /**
     * 无新数据
     */
    NOGPSPOINTS(128),

    /**
     * 提前出厂
     */
    GO_OUT_EARLY(256),

    /**
     * 迟到回厂
     */
    COME_BACK_LATE(512);

    private int id;

    Alarm(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Alarm valueOf(Integer id) {

        if (id==null){ //possible null from database
            return null;
        }
        Alarm alarm = null; // Default
        for (Alarm item : Alarm.values()) {
            if (item.getId() == id) {
                alarm = item;
                break;
            }
        }
        return alarm;
    }

    public String getLocalString(){
        switch (id){
            case 1:
                return "超速";
            case 2:
                return "疲劳驾驶";
            case 4:
                return "无任务出车";
            case 8:
                return "超违章出车";
            case 16:
                return "修理车辆出车";
            case 32:
                return "准驾车型不符";
            case 64:
                return "";
            case 128:
                return "无新数据";
            case 256:
                return "提前出厂";
            case 512:
                return "延迟回厂";

        };
        return "";
    }

}