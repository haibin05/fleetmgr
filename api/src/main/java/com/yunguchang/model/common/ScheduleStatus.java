package com.yunguchang.model.common;

/**
 * Created by gongy on 2015/11/2.
 */
public enum ScheduleStatus {
    NOT_IN_EFFECT(0),
    AWAITING(1),
    FINISHED(2),
    CANCELED(3);
    private final int id;

    ScheduleStatus(int id){
        this.id=id;
    }

    public int id(){
        return this.id;
    }

    public static ScheduleStatus valueOf(Integer i){
        switch (i) {
            case 0 : return NOT_IN_EFFECT;
            case 1 : return AWAITING;
            case 2 : return FINISHED;
            case 3 : return CANCELED;
            default:return null;
        }

    }

}
