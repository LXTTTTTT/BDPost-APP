package com.example.bdpostapp.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.bdpostapp.Base.BaseActivity;
import com.example.bdpostapp.Base.MainApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 权限管理工具
public class PermissionUtil {
    public static String TAG = "PermissionUtil";
    public static final Map<Integer, String> permissionsMap = new HashMap<>();  // 权限列表
    public static final int PERMISSION_REQUEST_ALL = 999;  // 请求所有权限

// 要新增权限的位置：这里、下面的 static、最后的 isPermissionGranted ---------------------------------------------
    public static final int PERMISSION_REQUEST_CODE_INTERNET = 0x001;
    public static final int PERMISSION_REQUEST_ACCESS_NETWORK_STATE = 0x002;
    public static final int PERMISSION_REQUEST_ACCESS_WIFI_STATE = 0x003;
    public static final int PERMISSION_REQUEST_READ_PHONE_STATE = 0x004;
    public static final int PERMISSION_REQUEST_CHANGE_WIFI_STATE = 0x005;
    public static final int PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE = 0x009;
    public static final int PERMISSION_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0x006;  // 定位
    public static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION = 0x007;  // 定位
    public static final int PERMISSION_REQUEST_CODE_ACCESS_BACKGROUND_LOCATION = 0x008;  // 后台定位
    public static final int PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE = 0x011;  // 读取文件
    public static final int PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 0x012;  // 写入文件
    public static final int PERMISSION_REQUEST_CODE_FOREGROUND_SERVICE = 0x020;  // 前台服务：保持后台活跃
    public static final int PERMISSION_REQUEST_CODE_VIBRATE = 0x021;  // 响铃振动

    static {
        permissionsMap.put(PERMISSION_REQUEST_CODE_INTERNET, Manifest.permission.INTERNET);
        permissionsMap.put(PERMISSION_REQUEST_ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_NETWORK_STATE);
        permissionsMap.put(PERMISSION_REQUEST_ACCESS_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE);
        permissionsMap.put(PERMISSION_REQUEST_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
        permissionsMap.put(PERMISSION_REQUEST_CHANGE_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE);
        permissionsMap.put(PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        permissionsMap.put(PERMISSION_REQUEST_CODE_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);  // 定位
        permissionsMap.put(PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);  // 定位
        permissionsMap.put(PERMISSION_REQUEST_CODE_ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION);  // 定位
        permissionsMap.put(PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);  // 读取文件
        permissionsMap.put(PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);  // 写入文件
        permissionsMap.put(PERMISSION_REQUEST_CODE_FOREGROUND_SERVICE, Manifest.permission.FOREGROUND_SERVICE);
        permissionsMap.put(PERMISSION_REQUEST_CODE_VIBRATE, Manifest.permission.VIBRATE);
    }


    // 使用requestCode码查看权限获取状态
    public static boolean isPermissionGranted(Activity activity, int requestCode) {
        // 高于 23 版本
        if (Build.VERSION.SDK_INT >= 23) {
            boolean ans = false;
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE_INTERNET:
                    ans = (activity.checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_ACCESS_NETWORK_STATE:
                    ans = (activity.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_ACCESS_WIFI_STATE:
                    ans = (activity.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_READ_PHONE_STATE:
                    ans = (activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_CHANGE_WIFI_STATE:
                    ans = (activity.checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE:
                    ans = (activity.checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_CODE_ACCESS_COARSE_LOCATION:
                    ans = (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION:
                    ans = (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_CODE_ACCESS_BACKGROUND_LOCATION:
                    ans = (activity.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE:
                    ans = (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                    ans = (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_CODE_FOREGROUND_SERVICE:
                    ans = (activity.checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                case PERMISSION_REQUEST_CODE_VIBRATE:
                    ans = (activity.checkSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED);
                    return ans;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    // =======================================================================
    // ************************ 批量权限授予 ***********************************
    // =======================================================================

    // 批量申请权限
    public static void requestPermissionsByRequestCodes(BaseActivity activity, int[] requestCodes, int requestCode) {
        if (null == requestCodes || requestCodes.length < 1) {
            return;
        }
        List<String> requestPermissions = new ArrayList<>();

        for (int i = 0; i < requestCodes.length; i++) {
            if (permissionsMap.containsKey(requestCodes[i]) && !isPermissionGranted(activity, requestCodes[i])) {
                requestPermissions.add(permissionsMap.get(requestCodes[i]));
            }
        }

        if (requestPermissions.size() < 1) {
            return;
        }

        String[] permissions = new String[requestPermissions.size()];
        for (int i = 0; i < requestPermissions.size(); i++) {
            permissions[i] = requestPermissions.get(i);
        }
        // 申请多个权限
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * 查看某权限组是否被拒绝过
     *
     * @param activity
     * @param requestCode
     * @return
     */
    public static boolean isPermissionRefused(BaseActivity activity, int requestCode) {
        if (permissionsMap.containsKey(requestCode)) {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionsMap.get(requestCode));
        }
        return false;
    }



// 获取手机状态权限
    public static void requestPhoneStatePermission(BaseActivity activity) {
        if (!isPermissionGranted(activity, PERMISSION_REQUEST_READ_PHONE_STATE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
                // 这个权限被用户拒绝过
                Toast.makeText(activity,"请给予设备信息权限APP的部分功能才可以正常使用",Toast.LENGTH_SHORT);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_READ_PHONE_STATE);
            }
        }
    }

    // 检查应用是否具有发送通知的权限，传入对应的id
    public static boolean hasNotificationPermission(String channel_id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) MainApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = notificationManager.getNotificationChannel(channel_id); // 替换成你的通知渠道ID
            return channel!=null;
        }
        return true; // 对于低于 Android 8.0 的版本，假设有通知权限
    }




}
