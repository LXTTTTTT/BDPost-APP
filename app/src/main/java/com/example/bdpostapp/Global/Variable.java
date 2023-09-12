package com.example.bdpostapp.Global;


import com.example.bdpostapp.Utils.SharedPreferencesUtil;

// 项目使用的全局变量
public class Variable {

    public static boolean DebugMode = true;  // 调试模式
    public static int maximum_page = 1;  // 消息的最大页数

    public static double longitude = 0.0d;
    public static double latitude = 0.0d;
    public static double altitude = 0.0d;

    public static String getLastSN(){
        return SharedPreferencesUtil.getInstance().getString(Constant.LAST_SN, Constant.NONE_SN);
    };
    public static String getAccount(){
        return SharedPreferencesUtil.getInstance().getString(Constant.ACCOUNT_S, Constant.DEFAULT_ACCOUNT);
    };
    public static String getPassword(){
        return SharedPreferencesUtil.getInstance().getString(Constant.PASSWORD_S, Constant.DEFAULT_PASSWORD);
    };

    public static boolean isConnectUSB = false;  // 是否连接 USB
    public static String token = "";  // token



}
