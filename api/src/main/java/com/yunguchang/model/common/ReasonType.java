package com.yunguchang.model.common;

/**
 * Created by WHB on 2015-11-24.
 */
public enum ReasonType {

    NULL(null),         // 未指定
    BUSINESS("01"),     // 出差
    TRAINING("02"),     // 培训
    REPAIRING("03"),    // 抢修
    CONSTRUCT("04"),    // 施工
    ZHENGCHU("05"),     // 政处
    XIAOQUE("06");      // 消缺

    private String type;

    ReasonType(String value) {
        this.type = value;
    }
    public static ReasonType typeOf(String value) {
        if(value == null) {
            return NULL;
        }
        switch (value) {
            case "01":
                return BUSINESS;
            case "02":
                return TRAINING;
            case "03":
                return REPAIRING;
            case "04":
                return CONSTRUCT;
            case "05":
                return ZHENGCHU;
            case "06":
                return XIAOQUE;
            default:
                return NULL;
        }
    }

    public String value(){
        return this.type;
    }

    @Override
    public String toString() {
        switch (type) {
            case "01":
                return "出差";
            case "02":
                return "培训";
            case "03":
                return "抢修";
            case "04":
                return "施工";
            case "05":
                return "政处";
            case "06":
                return "消缺";
            default:
                return "未指定";
        }
    }
}
