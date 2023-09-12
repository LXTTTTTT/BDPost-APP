package com.example.bdpostapp.Entity.GsonBean.Receiving;

import java.util.ArrayList;
import java.util.List;

public class TerminalDetail {
    public String groupId = "";
    public String groupName = "";
    public TerminalInfo terminalInfo = new TerminalInfo();


    public class TerminalInfo {
        public String addr = "";
        public String chatTextContent = "";
        public List<Field> fields = new ArrayList<>();
        public boolean follow = false;
        public List<Info> infos = new ArrayList<>();
        public long lastCommTime = 0;
        public Location loc = new Location();
        public String remark = "";
        public String scope = "";
        public int selectTextIndex = 0;
        public boolean selected = false;
        public String status = "";
        public String trackColor = "";
        public int trackSize = 0;
        public int belongTo = 0;
        public int followByShareNum = 0;
        public boolean showAoWeiBtn = false;
        public String type = "";

        // Other fields...
        public class Field {
            public String name = "";
            public String value = "";

            // Other fields...
        }

        public class Info {
            public String name = "";
            public String value = "";

            // Other fields...
        }
    }



    public class Location {
        public double alt = 0.0;
        public double dir = 0.0;
        public double lat = 0.0;
        public double lng = 0.0;
        public String locStatus = "";
        public String locType = "";
        public String remark = "";
        public double speed = 0.0;
        public String time = "";
        public double wgs84Lat = 0.0;
        public double wgs84Lng = 0.0;

        // Other fields...
    }



}
