package com.yunguchang.model.sync;

/**
 * 调度作废类型
 * Created by haibin on 2015/12/30.
 */
public enum ScheduleCancelType {
    NULL(null),
    用户原因("0"),
    车队原因("1");
    String value;

    ScheduleCancelType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ScheduleCancelType toEnumType(String value) {
        switch (value) {
            case "0":
                return 用户原因;
            case "1":
                return 车队原因;
            default:
                return NULL;
        }
    }
}
