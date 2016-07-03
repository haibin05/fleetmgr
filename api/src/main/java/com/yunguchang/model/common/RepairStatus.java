package com.yunguchang.model.common;

/**
 * Created by WHB on 2015-12-04.
 * 车辆维修状态
 */
public enum RepairStatus {
    REPAIRING("在修"),          // '等待车队长确认','报修','待确认','已确认','重新确认','车队长已确认','返工'
    SETTLEMENT("完工结算"),     // '完工结算'
    CHECKING("完工检测"),       // '完工检测'
    COMMIT("报修");             //'报修'
    private String value;

    RepairStatus(String value) {
        this.value = value;
    }
}
