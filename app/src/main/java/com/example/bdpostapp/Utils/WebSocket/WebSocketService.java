package com.example.bdpostapp.Utils.WebSocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bdpostapp.Entity.WebSocketBean.Receive.WsMsgToUserMsg;
import com.example.bdpostapp.Entity.WebSocketBean.Send.SendVo;
import com.example.bdpostapp.Entity.WebSocketBean.Send.WsLogin;
import com.example.bdpostapp.Global.Constant;
import com.example.bdpostapp.Global.Variable;
import com.example.bdpostapp.MainActivity;
import com.example.bdpostapp.R;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import javax.crypto.spec.DESedeKeySpec;

public class WebSocketService extends Service {
    private final static String TAG = "WebSocketService";
  // private  final String ws_ip="ws://back.tdwtv2.pg8.ink/ws";
    private  final String ws_ip= Constant.WEBSOCKET_URL;
    public WebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();
    private final static int GRAY_SERVICE_ID = 1001;
    private static final long CLOSE_RECON_TIME = 15*1000;  // 连接断开或者连接错误立即重连
    private Notification notification;
    private WsMsgToUserMsg wsMsgToUserMsg;

    // 用于Activity和service通讯
    public class JWebSocketClientBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    // 灰色保活
    public static class GrayInnerService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.e(TAG, "onStartCommand: 2");
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            Log.e(TAG, "onBind: 2");
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: 1");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        wsMsgToUserMsg=new WsMsgToUserMsg();
        SendVo sendVO=new SendVo();
        sendVO.setType("1");
        heartbeatContent= new Gson().toJson(sendVO);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: 1");
        if(intent==null){return super.onStartCommand(intent, flags, startId);}
        String operation = intent.getStringExtra(Constant.SERVICE_OPERATION);
        if (operation == null) {return super.onStartCommand(intent, flags, startId);}

        if(operation.equals(Constant.INIT_COMMAND)){
            //初始化WebSocket
            initSocketClient();
            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测

            //设置service为前台服务，提高优先级
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                //Android4.3以下 ，隐藏Notification上的图标
                startForeground(GRAY_SERVICE_ID, new Notification());
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                //Android4.3 - Android8.0，隐藏Notification上的图标
                Intent innerIntent = new Intent(this, GrayInnerService.class);
                startService(innerIntent);
                startForeground(GRAY_SERVICE_ID, new Notification());
            } else {
                Intent nfIntent = new Intent(this, MainActivity.class);
                nfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getActivity(this, 0, nfIntent, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getActivity(this, 0, nfIntent, 0);
                }
                Notification.Builder builder = new Notification.Builder(this.getApplicationContext())
                        .setContentIntent(pendingIntent) // 设置PendingIntent
                        .setSmallIcon(R.drawable.chibi_maruko) // 设置状态栏内的小图标
                        .setPriority(Notification.PRIORITY_MIN)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText("正在前台运行") // 设置上下文内容
                        .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //修改安卓8.1以上系统报错
                    NotificationChannel notificationChannel = new NotificationChannel("002", "设备状态更新", NotificationManager.IMPORTANCE_MIN);
                    notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
                    notificationChannel.setShowBadge(false);//是否显示角标
                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.createNotificationChannel(notificationChannel);
                    builder.setChannelId("002");
                }
                // 获取构建好的Notification
                notification = builder.build();
                notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
                startForeground(GRAY_SERVICE_ID, notification);
            }
        }else if(operation.equals(Constant.DISCONNECT_COMMAND)){
            onDestroy();
        }

        return START_STICKY;
    }

    //这里是处理webscoket
    private void initSocketClient() {
        Log.e(TAG, "initSocketClient");
        //（如果加密，则为wss），服务器网址就是 URL。
        URI uri = URI.create(ws_ip);
        client = new WebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                //message就是接收到的消息
                Log.e(TAG, "WebSocketService收到的消息：" + message);
                wsMsgToUserMsg.convert(message);
            }

            @Override
            public void onOpen(ServerHandshake handShakeData) {//在webSocket连接开启时调用
                Log.e(TAG, "WebSocket 连接成功");
                login();
            }
            @Override
            public void onClose(int code, String reason, boolean remote) {//在连接断开时调用
                Log.e(TAG, "onClose() 连接断开_reason：" + reason + " code："+code);
                mHandler.removeCallbacks(heartBeatRunnable);
                mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);//开启心跳检测
            }

            @Override
            public void onError(Exception ex) {//在连接出错时调用
                ex.printStackTrace();
                Log.e(TAG, "onError() 连接出错：" + ex.getMessage());
                mHandler.removeCallbacks(heartBeatRunnable);
                mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);//开启心跳检测
            }
        };
        connect();
    }

    /**
     * 连接WebSocket
     */
    private void connect() {
        Log.e(TAG, "connect");
        new Thread(() -> {
            try {
                // connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                client.connectBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void  login(){
        String token= Variable.token;
        Log.e("login TOKEN_LOGIN:",token);
        if("".equals(token)){
            return;
        }
        WsLogin wsLogin=new WsLogin(token,"0");
        SendVo sendVO=new SendVo();
        sendVO.setContent(wsLogin);
        sendVO.setType("10");
        String sendJson =new Gson().toJson(sendVO);
        sendMsg(sendJson);

    }

    private String heartbeatContent="";
    public void  sendHeartbeat(){
        sendMsg(heartbeatContent);
    }
    /**
     * 发送消息
     */
    public void sendMsg(String msg) {
        if (null != client) {
            try {
                Log.e(TAG, "下发的 WebSocket 消息: "+msg );
                client.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e("onDestroy","111111");
        closeConnect();
        super.onDestroy();
    }

    /**
     * 断开连接
     */
    public void closeConnect() {
        mHandler.removeCallbacks(heartBeatRunnable);
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
        // 关闭前台服务并清除通知
        stopForeground(true);
    }

    //    -------------------------------------WebSocket心跳检测------------------------------------------------
    private static final long HEART_BEAT_RATE = 9800;

    //每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler(){};
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                    Log.e(TAG, "心跳包检测WebSocket连接状态：已关闭");
                } else if (client.isOpen()) {
                    Log.e(TAG, "心跳包检测WebSocket连接状态：已连接");
                } else {
                    Log.e(TAG, "心跳包检测WebSocket连接状态：已断开");
                }
            } else {
                //如果client已为空，重新初始化连接
                initSocketClient();
                Log.e(TAG, "心跳包检测WebSocket连接状态：client已为空，重新初始化连接");
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            sendHeartbeat();
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread(() -> {
            try {
                Log.e(TAG, "开启重连");
                client.reconnectBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public class AuxiliaryService extends Service {
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {

            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();

        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startNotification();

            return super.onStartCommand(intent, flags, startId);
        }

        /**
         * 启动通知
         */
        private void startNotification() {

            Notification notification = new Notification();
            this.startForeground(GRAY_SERVICE_ID, notification);
            stopSelf(); //关键  如果AuxiliaryService 没有与什么组件绑定  系统就会回收
            stopForeground(true);
        }
    }

}
