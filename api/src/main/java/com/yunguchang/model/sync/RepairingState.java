package com.yunguchang.model.sync;

/**
 * 维修状态
 */
public enum RepairingState {
    /**
     * 报修
     */
    报修(1),
    /**
     * 进厂
     */
    进厂(2),
    /**
     * 出厂
     */
    出厂(0);

    private int id;

    RepairingState(int id){
        this.id=id;
    }

    public int id(){
        return id;
    }

}
