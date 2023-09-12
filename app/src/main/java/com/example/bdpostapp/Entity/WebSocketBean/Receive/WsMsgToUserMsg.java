package com.example.bdpostapp.Entity.WebSocketBean.Receive;

import android.util.Log;

import com.example.bdpostapp.Base.MainApplication;
import com.example.bdpostapp.Global.Constant;
import com.example.bdpostapp.Utils.NotificationCenter;
import com.example.bdpostapp.Utils.RequestUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

// 解析 websocket 消息
public class WsMsgToUserMsg {
    public String TAG = "WsMsgToUserMsg";
    public static final int ERROR_MSG = 0;
    public static final int HEARTBEAT = 1;
    public static final int LOGIN_SUCCESS = 11;
    public static final int MSG = 20;
    public static final int MSG_IMG_UPDATE = 21;  // 图片新包
    public static final int MSG_LIVE_CHAT = 61;
    public static final int UNREAD_MSG = 30;  // 收到未读消息
    public static final int RECEIVE_SOS_OK = 99;  // 收到报平安消息/报警

    public static final String MSG_TEXT = "TEXT";
    public static final String MSG_VOICE = "VOICE";
    public static final String MSG_IMG = "IMAGE";
    public static final String MSG_SOS = "ALARM";
    public static final String MSG_OK = "OK";
    public static final String NUM = "NUM";
    public static final String GET_INTO = "GET_INTO";
    public static final String CHAT = "CHAT";
    public static final String LOCATION = "LOCATION";

    public void convert(String msg) {
        try {
            JSONObject obj = new JSONObject(msg);
            JSONObject data = obj.getJSONObject("content");
            switch (obj.getInt("type")) {
                case ERROR_MSG:
                    error(data);
                    break;
                case LOGIN_SUCCESS:
                    loginSuccess(data);
                    break;
                case MSG:
                    onMsg(data);
                    break;
                case MSG_IMG_UPDATE:
                    imgMsg(data);
                    break;
                case MSG_LIVE_CHAT:
                    onLiveMsg(data);
                    break;
                case UNREAD_MSG:
                    onUnreadMsg();
                    break;
                case RECEIVE_SOS_OK:
                    onOKOrSOS(data);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onLiveMsg(JSONObject data) {
        // 直播消息
    }

    private void onUnreadMsg() {
        RequestUtil.getInstance().getChatList();  // 刷新聊天列表
    }

    private void onOKOrSOS(JSONObject data) {
        try {
            JSONObject info = data.getJSONArray("infos").getJSONObject(0);
            if (info!=null) {
                String addr = info.getString("addr");
                String status = info.getString("status");
                if(status.equals("SOS")){
                    // 有这个才是SOS消息
                    if(!info.isNull("wsAlarms")){
                        NotificationCenter.standard().postNotification(Constant.RECEIVE_OK_SOS,addr);
                        MainApplication.getInstance().ringBell("收到SOS消息");  // 响铃
                    }
                }else if(status.equals("ON")){
                    // 有这个才是报平安消息
                    if(!info.isNull("okMsg")){
                        NotificationCenter.standard().postNotification(Constant.RECEIVE_OK_SOS,addr);
                        MainApplication.getInstance().ringBell("收到报平安消息");  // 响铃
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void error(JSONObject data) {
        String errMsg = null;
        try {
            errMsg = data.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "errMsg"+errMsg);
    }

    private void loginSuccess(JSONObject data) {
        //  Log.e("loginSuccess", "1111111");
        Log.e(TAG, "loginSuccess: WebSocket 登陆成功");
    }

    private void onMsg(JSONObject data) {
        NotificationCenter.standard().postNotification(Constant.RECEIVE_MESSAGE,data);
        try {
            JSONObject jsonObject = data.getJSONObject("chatInfo");
            JSONObject chatType = jsonObject.getJSONObject("chatType");
            String msg_content = "";
            switch (chatType.getString("name")) {
                // 收到文本消息
                case MSG_TEXT:
                    Log.e(TAG, "收到文本消息");
                    msg_content = jsonObject.getString("content");
//                    loadTextMsg(jsonObject);
                    break;
                // 收到语音消息
                case MSG_VOICE:
                    Log.e(TAG, "收到语音消息");
                    msg_content = "语音消息";
//                    loadVoiceMsg(jsonObject);
                    break;
                // 收到图片消息
                case MSG_IMG:
                    Log.e(TAG, "图片消息");
                    msg_content = "收到新的图片消息";
//                    loadImgMsg(jsonObject);
                    break;
                // 收到SOS消息
                case MSG_SOS:
                    Log.e(TAG, "MSG_SOS");
                    msg_content = "SOS消息";
//                    loadSOSMsg(jsonObject);
                    break;
                // 收到报平安消息
                case MSG_OK:
                    Log.e(TAG, "收到报平安消息");
                    msg_content = "报平安消息";
//                    loadOkMsg(jsonObject);
                    break;
            }

            MainApplication.getInstance().ringBell(msg_content);  // 响铃
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadVoiceMsg(JSONObject jsonObject) {
        if (!jsonObject.isNull("voiceInfo")) {
            try{
                JSONObject voiceInfo = jsonObject.getJSONObject("voiceInfo");
                String fileId = voiceInfo.getString("fileId");
                int sec = voiceInfo.getInt("sec");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadTextMsg(JSONObject jsonObject) {

        if (!jsonObject.isNull("loc")) {
            try{
                JSONObject loc = jsonObject.getJSONObject("loc");
                if (!loc.isNull("wgs84Lng") && !loc.isNull("wgs84Lat")) {
                    String loc_str = loc.getString("wgs84Lng");
                    String lat_str = loc.getString("wgs84Lat");
                } else {
                    String loc_str = loc.getString("lng");
                    String lat_str = loc.getString("lat");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void loadOkMsg(JSONObject jsonObject) {
        if (!jsonObject.isNull("loc")) {
            try {
                JSONObject loc = jsonObject.getJSONObject("loc");
                if (!loc.isNull("wgs84Lng") && !loc.isNull("wgs84Lat")) {
                    String loc_str = loc.getString("wgs84Lng");
                    String lat_str = loc.getString("wgs84Lat");
                } else {
                    String loc_str = loc.getString("lng");
                    String lat_str = loc.getString("lat");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void loadSOSMsg(JSONObject jsonObject) {

        if (!jsonObject.isNull("loc")) {
            try {
                Log.e("loc", "null");
                JSONObject loc = null;
                loc = jsonObject.getJSONObject("loc");
                if (!loc.isNull("wgs84Lng") && !loc.isNull("wgs84Lat")) {
                    String loc_str = loc.getString("wgs84Lng");
                    String lat_str = loc.getString("wgs84Lat");

                } else {
                    String loc_str = loc.getString("lng");
                    String lat_str = loc.getString("lat");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (!jsonObject.isNull("alarmInfo")) {
            try {
                JSONObject alarmInfo = jsonObject.getJSONObject("alarmInfo");
                JSONObject statusType = alarmInfo.getJSONObject("statusType");
                String statusTypeValue = statusType.getString("value");
                String num = alarmInfo.getString("num");
                JSONObject healthType = alarmInfo.getJSONObject("healthType");
                String healthTypeValue = healthType.getString("value");
                StringBuffer contentSb = new StringBuffer();
                contentSb.append("紧急情况:");
                contentSb.append(statusTypeValue);
                contentSb.append(", 身体情况:");
                contentSb.append(healthTypeValue);
                contentSb.append(", 待援人数:");
                contentSb.append(num);
                contentSb.append(", 备注:无");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void loadImgMsg(JSONObject jsonObject) {
        if (!jsonObject.isNull("imageInfo")) {
            try {
                JSONObject imageInfo = jsonObject.getJSONObject("imageInfo");
                String fileId = imageInfo.getString("fileId");
                int total = imageInfo.getInt("total");
                String curTotal = imageInfo.getString("curTotal");
                String size = imageInfo.getString("size");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void imgMsg(JSONObject data) {
        if (!data.isNull("imageInfo")) {
            try {
                String chatId = data.getString("chatId");
                NotificationCenter.standard().postNotification(Constant.NEW_IMAGE_MSG,chatId);  // 把收到的 id 发出去
                JSONObject imageInfo = data.getJSONObject("imageInfo");
                String fileId = imageInfo.getString("fileId");
                int total = imageInfo.getInt("total");
                String curTotal = imageInfo.getString("curTotal");
                String size = imageInfo.getString("size");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




}
