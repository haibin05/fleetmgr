package com.yunguchang.sharetome;

/**
 * Created by gongy on 2015/12/21.
 */
public class SharetomeProfile {

//    {"success":true,"errorCode":0,"message":null,"data":"gongyi"}

    private boolean success;
    private int errorCode;
    private String message;
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
