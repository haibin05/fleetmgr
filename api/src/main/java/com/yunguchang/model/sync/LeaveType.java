package com.yunguchang.model.sync;

/**
 * 考勤种类
 */
public enum LeaveType {
    出勤("0"),
    病假("1"),
    事假("2"),
    年假("3"),
    加班("4"),
    拖班("5"),
    抢修("6"),
    公休("7"),
    调修("8"),
    出差("9"),
    通宵班("a");

    String value;

    LeaveType(String value) {
        this.value = value;
    }

    public String typeValue() {
        return value;
    }
}
