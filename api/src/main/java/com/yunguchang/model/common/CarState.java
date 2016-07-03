package com.yunguchang.model.common;

/**
 * 车辆状况
 */
public enum CarState {

    /**
     * 待命
     */
    IDLE(1),

    /**
     * 出车
     */
    INUSE(2),

    /**
     *  待修
     */
    AWAITING_REPAIRE(4),

    /**
     * 维修
     */
    REPAIRING(8);

    private int value;

    CarState(int value) {
        this.value = value;
    }

    public static CarState toEnum(Integer value) {
        if(value == null) {
            return IDLE;
        }
        switch (value) {
            case 1:
                return IDLE;
            case 2:
                return INUSE;
            case 4 :
                return AWAITING_REPAIRE;
            case 8 :
                return REPAIRING;
        }
        return IDLE;
    }

}