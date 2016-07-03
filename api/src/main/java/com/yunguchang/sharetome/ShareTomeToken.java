package com.yunguchang.sharetome;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

/**
 * Created by gongy on 2015/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareTomeToken {

    private DateTime createTime = new DateTime();

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String  tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private DateTime expireIn;

    @JsonProperty("scope")
    private String scope;


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public DateTime getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(DateTime expireIn) {
        this.expireIn = expireIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public String getFullBearerToken(){
        return "Bearer "+accessToken;
    }
}
