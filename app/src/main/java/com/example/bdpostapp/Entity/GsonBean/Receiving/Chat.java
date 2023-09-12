package com.example.bdpostapp.Entity.GsonBean.Receiving;

public class Chat {

    public String id;
    public String addr;
    public String status;
    public String scope;
    public String remark;
    public String chatTimeStr;
    public int unreadNum;
    public String type;
    public ChatType chatType;
    public class ChatType {
        public String name;
        public String value;
    }
    public String content;
    public boolean isPhone;
    public String avatar;

//    public String addr;
//    public String chatTimeStr;
//    public String id;
//    public String remark;
//    public String scope;
//    public String status;
//    public String type;
//    public int unreadNum;

}
