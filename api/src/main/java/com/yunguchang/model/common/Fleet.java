package com.yunguchang.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * 车队对象
 */
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)

public class Fleet {
    // 车队id
    @DocumentationExample("0010701")
    private String id;
    // 车队名称
    @DocumentationExample("二车队")
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
