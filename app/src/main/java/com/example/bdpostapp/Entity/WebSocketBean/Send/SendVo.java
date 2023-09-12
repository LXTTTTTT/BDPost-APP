package com.example.bdpostapp.Entity.WebSocketBean.Send;

import java.io.Serializable;

public class SendVo implements Serializable {

    private Object content;
    private  String type;
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
