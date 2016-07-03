package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by 禕 on 2015/11/5.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class KeyValue {
    private String key;
    private String value;

    public KeyValue(String key) {
        this.key = key;
    }

    public KeyValue() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KeyValue{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
