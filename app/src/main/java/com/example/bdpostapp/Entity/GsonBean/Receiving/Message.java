package com.example.bdpostapp.Entity.GsonBean.Receiving;

public class Message {

    public ChatSendStatus chatSendStatus;  //
    public String chatTimeStr;  //
    public ChatType chatType;  //
    public String content;  //
    public String errorMsg;  //
    public String from;  //
    public String id;  //
    public String fromAccount;  //
    public ImageInfo imageInfo;  //
    public LocationInfo loc;  //
    public String to;  //
    public VoiceInfo voiceInfo;  //
    public AlarmInfo alarmInfo;  //
    public ChatSendType chatSendType;  //

    public class ChatSendStatus {
        public String name;
        public String value;

    }

    public class ChatType {
        public String name;
        public String value;

    }

    public class ImageInfo {
        public double beforeCompSize;
        public int curTotal;
        public double decompSize;
        public String errorMsg;
        public String fileId;
        public boolean finished;
        public double size;
        public int total;
    }

    public class LocationInfo {
        public double alt;  //
        public double dir;  //
        public double lat;  //
        public double lng;  //
        public String locStatus;  //
        public String locType;  //
        public String remark;  //
        public double speed;  //
        public String time;  //
        public double wgs84Lat;  //
        public double wgs84Lng;  //
    }

    public class VoiceInfo {
        public String enhanceFileId;
        public EnhanceStatus enhanceStatus;
        public String fileId;
        public int sec;
    }

    public class EnhanceStatus {
        public String name;
        public String value;
    }

    public class AlarmInfo {
        public String content;
    }

    public class ChatSendType {
        public String name;
        public String value;
    }
}
