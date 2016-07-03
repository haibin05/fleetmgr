package com.yunguchang.model.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 返回的jwt token
 */
@JsonSerialize
public class Token {
    private String token;

    /**
     * token 内容
     *
     * @return token 内容
     */
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
