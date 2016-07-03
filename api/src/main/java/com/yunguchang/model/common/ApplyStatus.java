package com.yunguchang.model.common;

/**
 * Created by WHB on 2015-11-20.
 */
public enum ApplyStatus {

    NULL(null),
    FORMATION(1),   // 编制
    DEP_APPROVE(2), // 部门审批
    APPLY(3),  //申请调度
    DISPATCH_SUCCESS(4), // 调度成功
    APPLY_REJECT(5), // 审批退回
    DISPATCH_REJECT(6),// 调度退回
    DISPATCH_CANCEL(7),  // 调度作废
    APPLY_CANCEL(8), // 取消申请
    SYSTEM_CANCEL(9); // 系统作废
    Integer value;

    ApplyStatus(Integer value) {
        this.value = value;
    }

    public static ApplyStatus valueOf(Integer i) {
        switch (i) {
            case 1:
                return FORMATION;
            case 2:
                return DEP_APPROVE;
            case 3:
                return APPLY;
            case 4:
                return DISPATCH_SUCCESS;
            case 5:
                return APPLY_REJECT;
            case 6:
                return DISPATCH_REJECT;
            case 7:
                return DISPATCH_CANCEL;
            case 8:
                return APPLY_CANCEL;
            case 9:
                return SYSTEM_CANCEL;

            default:
                return NULL;
        }
    }

    @Override
    public String toString() {
        switch (value) {
            case 1:
                return "编制";
            case 2:
                return "部门审批";
            case 3:
                return "申请调度";
            case 4:
                return "调度成功";
            case 5:
                return "审批退回";
            case 6:
                return "调度退回";
            case 7:
                return "调度作废";
            case 8:
                return "取消申请";
            case 9:
                return "系统作废";
            default:
                return "未指定";
        }
    }

    public String toStringValue(){
        return String.valueOf(value);
    }
    public Integer id(){
        return value;
    }
}
