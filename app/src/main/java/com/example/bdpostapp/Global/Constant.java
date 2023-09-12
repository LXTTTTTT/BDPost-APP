package com.example.bdpostapp.Global;


import android.media.AudioFormat;

// 项目使用的全局常量
public class Constant {

    public static final String NONE_SN = "-";  // 未设置 SN 标识
    public static final String DEFAULT_ACCOUNT = "admin";  // 默认账号
    public static final String DEFAULT_PASSWORD = "test12346";  // 默认密码

    public static final String NONE_PASSWORD = "000";  // 未设置密码标识
    public static final String SERVICE_OPERATION = "service_operation";  // 服务指令标识
    public static final String INIT_COMMAND = "init_command";  // 下发初始化指令
    public static final String SEND_COMMAND = "send_command";  // 下发消息指令
    public static final String DISCONNECT_COMMAND = "disconnect_command";  // 断开蓝牙标识
    public static final String SEND_DATA = "send_data";  // 下发的消息包裹标识
    public static final String CONNECT_BLUETOOTH = "connect_bluetooth";  // 连接蓝牙标识
    public static final String DISCONNECT_BLUETOOTH = "disconnect_bluetooth";  // 断开蓝牙标识
    public static final String BLUETOOTH_DEVICE = "bluetooth_device";  // 下发的消息包裹标识

// NotificationCenter 常量 -----------------------------------
    public static final int UPDATA_NOTE = 6660;  // 更新笔记数据库
    public static final int CONNECT_SERIAL_PORT = 6661;  // 连接串口
    public static final int DISCONNECT_SERIAL_PORT = 6662;  // 断开连接串口
    public static final int CONNECT_USB_ACCESSORY = 6663;  // 连接USB附件
    public static final int DISCONNECT_USB_ACCESSORY = 6664;  // 连接USB附件
    public static final int CONNECT_USB = 6665;  // 连接USB
    public static final int DISCONNECT_USB = 6666;  // 连接USB
    public static final int RECEIVE_MESSAGE = 6667;  // websocket 收到新消息
    public static final int CHANGE_MARKER = 6668;  // 改变地图 marker
    public static final int RECEIVE_OK_SOS = 6669;  // 收到报平安/报警
    public static final int NEW_IMAGE_MSG = 6670;  // 收到新的图片分包

// SharedPreferences 名称 ----------------------------------
    public static final String LAST_SN = "last_sn";  // 最后设置的SN码
    public static final String ACCOUNT_S = "default_account";  // 最后设置的SN码
    public static final String PASSWORD_S = "default_password";  // 最后设置的SN码

// 权限请求码 -----------------------------------
    public static final int REQUEST_RECORD_AUDIO = 700;  // 请求录音权限
    public static final int REQUEST_CAMERA = 701;  // 请求相机权限

// 页面响应码 -----------------------------------
    public static final int REQUEST_SCAN_SN = 153;  // 扫描页面返回码_SN码
    public static final int REQUEST_SCAN_NUMBER = 154;  // 扫描页面返回码_卡号

// 录音工具参数 ----------------------------------
    public static final int sampleRateInHz = 8000;
    // 声道   CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static final int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    public static final int channelCount = channelConfig == AudioFormat.CHANNEL_IN_STEREO ? 2:1;
    // 比特率
    public static final int BIT_RATE = 64;
    // 编码格式
    public static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 读取数据的最大字节数
    public static final int KEY_MAX_INPUT_SIZE =20 * 1024;

// 全局常量 -----------------------------------------
    public static final String REMARK="remark";
    public static final String ADDR="addr";

    public static final int TEXT_MESSAGE = 0;
    public static final int VOICE_MESSAGE = 1;
    public static final int PICTURE_MESSAGE = 2;
    public static final int SOS_MESSAGE = 3;
    public static final int OK_MESSAGE = 4;

    public static final String TOKEN="token";
    public static final String LAST_LOGIN_ACCOUNT="last_login_account";
    public static final String LAST_LOGIN_PASSWORD="last_login_password";
    public static final String WAREHOUSING_NAME="warehousing_name";
    public static final String WAREHOUSING_REMARK="warehousing_remark";
    public static final String DEVICE_TYPE_INDEX="device_type_index";
    public static final String USAGE_TYPE_INDEX="usage_type_index";
    public static final String DEVICE_TYPE_STR="device_type_str";
    public static final String USAGE_TYPE_STR="usage_type_str";
    // 请求用到的 URL
    public static final String BASE_URL = "http://192.168.0.74:9004";
//    public static final String BASE_URL = "http://192.168.0.135:9004";
    public static final String WEBSOCKET_URL = "ws://192.168.0.74:9004/ws";
//    public static final String WEBSOCKET_URL = "ws://192.168.0.135:9004/ws";


    public static final String ACCOUNT_PWD_LOGIN="/api/monitor/web-user/login";  // /api/admin/login  /api/monitor/app-users/login/pwd
    public static final String CHAT_LIST="/api/monitor/platform-chats/chat-list";
    public static final String MESSAGE_LIST="/api/monitor/platform-chats/";
    public static final String TERMINAL_LIST="/api/monitor/groups";
    public static final String FILE="/api/monitor/files/";
    public static final String UNREAD_MESSAGE(String addr){
        return "/api/monitor/platform-chats/clear/"+addr+"/unread";
    }
    public static final String GET_MARKER="/api/monitor/aggregation";
    public static final String GET_TERMINAL_DETAIL="/api/monitor/terminals/batch/details";
    // 枚举类型
    public static final String DEVICE_TYPES="/api/monitor/enums/terminal-types";
    public static final String LOCATION_TYPES="/api/monitor/enums/location-types";
    public static final String LOCATION_STATUS="/api/monitor/enums/location-status";
}
