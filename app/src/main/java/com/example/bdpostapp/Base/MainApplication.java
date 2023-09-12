package com.example.bdpostapp.Base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.bdpostapp.Global.Constant;
import com.example.bdpostapp.Global.Variable;
import com.example.bdpostapp.MainActivity;
import com.example.bdpostapp.R;
import com.example.bdpostapp.Request.AccountInterface;
import com.example.bdpostapp.Utils.RequestUtil;
import com.example.bdpostapp.Utils.WebSocket.WebSocketService;
import com.example.bdpostapp.View.CustomDialog;
import com.example.bdpostapp.View.LoadingDialog;
import com.example.bdpostapp.View.WarnDialog;
import com.example.bdpostapp.ViewModel.DataViewModel;
import com.example.bdpostapp.Utils.RetrofitUtil;
import com.example.bdpostapp.Utils.SharedPreferencesUtil;
import com.google.android.material.snackbar.Snackbar;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;

public class MainApplication extends Application {

// Variable ------------------------------
    public String TAG = "MainApplication";
    public BaseActivity now_activity;  // 程序当前所在的 activity
    private Toast my_toast;  // Toast
    private Snackbar my_snackbar;  //

// Control --------------------------------
    private CustomDialog customDialog;  // 自定义 dialog
    private AlertDialog alertDialog;  // 自定义图像的系统警告框 AlertDialog
    private LoadingDialog loadingDialog;  // 加载中
    private WarnDialog warnDialog;  // 警告 dialog

// ViewModel ------------------------------
    public DataViewModel dataViewModel;

// Other ----------------------------------
    public AccountInterface accountInterface;  // 请求工具
    public WebSocketService mWebSocketService;  // WebSocket
    public Handler mHandler = new Handler();
    public AMapLocationClient aMapLocationClient;  // 定位
    public AMapLocationClientOption aMapLocationClientOption;  // 定位参数
    public Vibrator vibrator;  // 振动
    public AudioManager audioManager;  // 音频
    public NotificationManager notificationManager;  // 通知

// Instance -------------------------
    private static MainApplication mainApplication;
    public static MainApplication getInstance() {
        return mainApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;
        // 初始化 SharedPreferences 工具
        SharedPreferencesUtil.getInstance().initContext(this);
        // 初始化 Retrofit 网络请求工具
        RetrofitUtil.init();
        // 设置这个程序中的每个 activity 的生命周期事件（暂不操作）
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                Window window = activity.getWindow();
                window.setStatusBarColor(ContextCompat.getColor(activity, R.color.gray_3));
            }
            @Override public void onActivityStarted(@NonNull Activity activity) {}
            @Override public void onActivityResumed(@NonNull Activity activity) {}
            @Override public void onActivityPaused(@NonNull Activity activity) {}
            @Override public void onActivityStopped(@NonNull Activity activity) {}
            @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {}
            @Override public void onActivityDestroyed(@NonNull Activity activity) {}
        });
        // 初始化全局使用 ViewModel
        init_ViewModel();
        // 初始化请求工具
        accountInterface = RetrofitUtil.getRetrofit().create(AccountInterface.class);
        RequestUtil.getInstance().init(accountInterface,dataViewModel);
        // 初始化下拉刷新列表的头部
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.gray_2);  // 设置背景颜色
                return new ClassicsHeader(context);
            }
        });

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    // 初始化 ViewModel
    public void init_ViewModel(){
        dataViewModel = new ViewModelProvider.AndroidViewModelFactory(this).create(DataViewModel.class);
    }

    public void init_location(){
        
        // 初始化定位
        aMapLocationClient = new AMapLocationClient(this);
        // 设置定位回调监听
        aMapLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                Variable.longitude = aMapLocation.getLongitude();
                Variable.latitude = aMapLocation.getLatitude();
                Variable.altitude = aMapLocation.getAltitude();
            }
        });
        aMapLocationClientOption = new AMapLocationClientOption();
        // 高精度模式(Hight_Accuracy)、低功耗模式(Battery_Saving)、仅设备模式(Device_Sensors)
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        // 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
        aMapLocationClientOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        // 获取最近3s内精度最高的一次定位结果：设置setOnceLocationLatest(boolean b)接口为true，
        // 启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        aMapLocationClientOption.setOnceLocationLatest(true);
        // 设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        aMapLocationClientOption.setInterval(20000);
        // 设置是否返回地址信息（默认返回地址信息）
        aMapLocationClientOption.setNeedAddress(false);
        // 设置是否允许模拟位置,默认为true，允许模拟位置
        aMapLocationClientOption.setMockEnable(true);
        // 单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        aMapLocationClientOption.setHttpTimeOut(30000);
        // 关闭缓存机制
        aMapLocationClientOption.setLocationCacheEnable(false);

        if (aMapLocationClientOption != null) {
            aMapLocationClient.setLocationOption(aMapLocationClientOption);
            // 设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            aMapLocationClient.stopLocation();
            aMapLocationClient.startLocation();
        } else {
            // 给定位客户端对象设置定位参数
            aMapLocationClient.setLocationOption(aMapLocationClientOption);
            // 启动定位
            aMapLocationClient.startLocation();
        }
        

    }

    // 全局唯一 Toast 方法：0 - 短 ， 1 - 长
    public void showToast(String msg,int length) {
        now_activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(my_toast!=null){
                    my_toast.cancel();
                    my_toast = null;
                }
                if(length == 0){
                    my_toast = Toast.makeText(now_activity,msg,Toast.LENGTH_SHORT);
                }else {
                    my_toast = Toast.makeText(now_activity,msg,Toast.LENGTH_LONG);
                }
                my_toast.show();
                Log.e(TAG, "showToast：" + msg);
            }
        });
    }


    public void showSnackBar(View view,String msg,int length){
        now_activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(my_snackbar!=null){
                    my_snackbar.dismiss();
                    my_snackbar = null;
                }
                if(length==0){
                    my_snackbar = Snackbar.make(view,msg,Snackbar.LENGTH_SHORT);
                }else {
                    my_snackbar = Snackbar.make(view,msg,Snackbar.LENGTH_LONG);
                }
                my_snackbar.show();
                Log.e(TAG, "showSnackBar：" + msg);
            }
        });
    }


    // 全局唯一 展示 AlertDialog 方法： target_activity 留空 - 显示在当前 activity
    public void showAlertDialog(Activity target_activity, View view) {
        if (alertDialog == null) {

            if(target_activity != null){
                alertDialog = new AlertDialog.Builder(target_activity)
                        .setView(view)
                        .setTitle(null)
                        .setMessage(null)
                        .setPositiveButton(null,null)
                        .setNegativeButton(null, null)
                        .create();
            }else {
                alertDialog = new AlertDialog.Builder(now_activity)
                        .setView(view)
                        .setTitle(null)
                        .setMessage(null)
                        .setPositiveButton(null,null)
                        .setNegativeButton(null, null)
                        .create();;
            }

        }

        if(alertDialog.isShowing()){
            return;
        }

        alertDialog.show();
    }

    // 隐藏 AlertDialog 方法
    public void hideAlertDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    // 全局唯一 展示 加载中 方法： target_activity 留空 - 显示在当前 activity
    public void showLoadingDialog(Activity target_activity) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog();
        }
        Log.e(TAG, "弹出 loadingDialog");
        if (loadingDialog.isAdded()) return;

        // 设置警告的 内容 和 点击确定事件
        if(target_activity != null){
            loadingDialog.show(((FragmentActivity)target_activity).getSupportFragmentManager(),"");
        }else {
            loadingDialog.show(((FragmentActivity)now_activity).getSupportFragmentManager(), "");
        }
    }

    // 隐藏 加载中 方法
    public void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    // 全局唯一 展示 自定义 dialog 方法： target_activity 留空 - 显示在当前 activity，列表用到的数据，保存列表拿到的数据
    public void showCustomDialog(Activity target_activity) {
        if (customDialog == null) {
            customDialog = new CustomDialog();
        }
        Log.e(TAG, "弹出 CustomDialog");
        if (customDialog.isAdded()) return;

        // 设置警告的 内容 和 点击确定事件
        customDialog.setData();
        if(target_activity != null){
            customDialog.show(target_activity.getFragmentManager(), "");
        }else {
            customDialog.show(now_activity.getFragmentManager(), "");
        }
    }
    // 隐藏 警告 dialog 方法
    public void hideCustomDialog() {
        if (customDialog != null) {
            customDialog.dismiss();
        }
    }

    // 全局唯一 展示 警告 dialog 方法： target_activity 留空 - 显示在当前 activity
    public void showWarnDialog(Activity target_activity, String str, View.OnClickListener onYesClickListener, View.OnClickListener onNoClickListener) {
        if (warnDialog == null) {
            warnDialog = new WarnDialog();
        }
        Log.e(TAG, "弹出 dialog");
        if (warnDialog.isAdded()) return;

        // 设置警告的 内容 和 点击确定事件
        if(target_activity != null){
            warnDialog.setData(target_activity, str, onYesClickListener, onNoClickListener);
            warnDialog.show(target_activity.getFragmentManager(), "");
        }else {
            warnDialog.setData(now_activity, str, onYesClickListener, onNoClickListener);
            warnDialog.show(now_activity.getFragmentManager(), "");
        }
    }

    // 隐藏 警告 dialog 方法
    public void hideWarnDialog() {
        if (warnDialog != null) {
            warnDialog.dismiss();
        }
    }

    // 播放消息提示音
    public void ringBell(String content){

        // 检查设备是否支持振动
        if (vibrator!=null && vibrator.hasVibrator()) {
            // 震动一段时间（以毫秒为单位）
            long[] pattern = {0, 1000, 500, 1000, 500}; // 静止，振动1秒，静止0.5秒，振动1秒，静止0.5秒
            vibrator.vibrate(pattern, -1); // 第二个参数是重复的索引，-1表示不重复
        }
        // 检查当前音频模式是否为正常模式
        if (audioManager!=null && (audioManager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL)) {
            // 播放铃声
            // 你可以在这里添加播放铃声的逻辑
            // 获取默认的消息提示音
            Uri defaultNotificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // 创建消息提示音的实例
            Ringtone defaultNotificationRingtone = RingtoneManager.getRingtone(getApplicationContext(), defaultNotificationUri);
            // 播放消息提示音
            if (defaultNotificationRingtone != null) {
                defaultNotificationRingtone.play();
            }
        }
        // 通知
        showNotification(content);
    }

    public void showNotification(String content){
        if(notificationManager==null){return;}
        // 创建通知渠道（仅适用于 Android 8.0 及更高版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "001";
            CharSequence channelName = "新消息通知";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        // 创建跳转到指定页面的 Intent
        Intent targetIntent = new Intent(this, MainActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, targetIntent, 0);
        }
        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "001")
                .setSmallIcon(R.drawable.chibi_maruko)
                .setContentTitle("收到新消息")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 设置点击通知后的操作
                .setAutoCancel(true); // 点击通知后自动取消通知

        // 显示通知
        notificationManager.notify((int) (System.currentTimeMillis() / 1000), builder.build());
    }

// ------------------------------------------------------------------------------------

    public void startWebSocketService() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "已有前台权限");
        } else {
            Log.e(TAG, "没有前台权限");
        }
        Intent bindIntent = new Intent(this, WebSocketService.class);
        bindIntent.putExtra(Constant.SERVICE_OPERATION, Constant.INIT_COMMAND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(bindIntent);
            Log.e(TAG, "连接 WebSocketService 1");
        } else {
            startService(bindIntent);
            Log.e(TAG, "连接 WebSocketService 2");
        }
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    public void stopWebSocketService(){
        Intent bindIntent = new Intent(this, WebSocketService.class);
        bindIntent.putExtra(Constant.SERVICE_OPERATION, Constant.DISCONNECT_COMMAND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(bindIntent);
            Log.e(TAG, "连接 WebSocketService 1");
        } else {
            startService(bindIntent);
            Log.e(TAG, "连接 WebSocketService 2");
        }
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mWebSocketService = ((WebSocketService.JWebSocketClientBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mWebSocketService = null;
        }
    };
    
    
// 生命周期 --------------------------------------------

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
