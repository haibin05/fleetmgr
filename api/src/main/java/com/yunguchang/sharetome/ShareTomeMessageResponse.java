package com.yunguchang.sharetome;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Created by gongy on 2015/11/17.
 */


//{
//        "success": true,
//        "errorCode": 50801,
//        "message": "",
//        "data": {
//        "postId": 1408963903408128,
//        "title": "ddd",
//        "userAvatarUrl": "/user_statics/1106177278658560/avatar",
//        "userNickName": "王海斌",
//        "lastUpdateDate": 1447750073846,
//        "postStatus": "SENT",
//        "numOfUnread": 0,
//        "postType": "Share",
//        "hasAttachment": false,
//        "setTop": false,
//        "favorite": false,
//        "read": true,
//        "fromGroup": false,
//        "like": false
//        }
//        }
@JsonIgnoreProperties(ignoreUnknown=true)
public class ShareTomeMessageResponse {
    private boolean success;
    private int errorCode;
    private String message;
    private Map<String, Object> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
