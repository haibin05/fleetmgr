package com.yunguchang.model.common;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongy on 2015/10/20.
 */
public class ErrorResponse {
    //private int code;
    private String message;
    private List<String> messages=new ArrayList<>();

//    public int getCode() {
//        return code;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }

    public ErrorResponse addMessage(String message){
        messages.add(message);
        return this;
    }


    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public String getMessage() {
        if (message!=null) {
            return message;
        }else if (messages.size()>0){
            return messages.get(0);
        }else{
            return  null;
        }

    }

    public void setMessage(String message) {
        this.message = message;
        if ( StringUtils.isNotBlank(message)){
            messages.add(0,message);
        }
    }
}
