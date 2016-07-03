package com.yunguchang.model.sync;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * 违章信息
 */
@JsonSerialize
public class SyncViolation {
    private String cphm;
    private int times;

    /**
     * 车牌号码
     * @return
     */
    @DocumentationExample("浙F55586")
    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }

    /**
     * 违章次数
     * @return
     */
    @DocumentationExample("4")
    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
