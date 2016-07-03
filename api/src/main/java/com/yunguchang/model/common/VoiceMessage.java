package com.yunguchang.model.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by 禕 on 2016/1/2.
 */
@JsonSerialize
public class VoiceMessage {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
