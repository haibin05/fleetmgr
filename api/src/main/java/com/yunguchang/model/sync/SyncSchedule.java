package com.yunguchang.model.sync;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 调度单信息
 */
@JsonSerialize
public class SyncSchedule {
    private String uuid = UUID.randomUUID().toString().replace("-", "");
    private List<ScheduleApplyInfoForSync> apply;   // 申请单
    private List<SchedulerelaForSync> rela;         // 调度单
    private List<ScheduleCarForSync> car;           // 调度用车列表

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<ScheduleApplyInfoForSync> getApply() {
        return apply;
    }

    public void setApply(List<ScheduleApplyInfoForSync> apply) {
        this.apply = apply;
    }

    public List<SchedulerelaForSync> getRela() {
        return rela;
    }

    public void setRela(List<SchedulerelaForSync> rela) {
        this.rela = rela;
    }

    public List<ScheduleCarForSync> getCar() {
        return car;
    }

    public void setCar(List<ScheduleCarForSync> car) {
        this.car = car;
    }

    public void addRela(SchedulerelaForSync rela) {
        if(this.rela == null) {
            this.rela = new ArrayList<>();
        }
        this.rela.add(rela);
    }
}



