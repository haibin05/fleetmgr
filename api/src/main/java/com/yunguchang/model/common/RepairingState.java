package com.yunguchang.model.common;

/**
 * Created by gongy on 2015/10/22.
 */
public enum RepairingState {
    REQUESTED(1),
    ONGOING(2),
    NONE(0);
    private int id;
    RepairingState(int id){
        this.id=id;
    }

    public int id(){
        return this.id;
    }

    public static RepairingState valueOf(Integer i){
        switch (i) {
            case 1 : return REQUESTED;
            case 2 : return ONGOING;
            default:return NONE;
        }
    }
}
