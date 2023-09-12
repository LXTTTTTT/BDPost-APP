package com.example.bdpostapp.Utils.WebSocket;

import android.util.Log;

import com.example.bdpostapp.Global.Constant;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketUtil {
    public String TAG = "";
// 单例 ------------------------------------------------
    private static WebSocketUtil websocketUtil;
    public static WebSocketUtil getInstance() {
        if(websocketUtil == null){
            websocketUtil = new WebSocketUtil();
        }
        return websocketUtil;
    }

    private WebSocket webSocket;

    public WebSocketUtil() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(Constant.WEBSOCKET_URL).build();
        webSocket = client.newWebSocket(request, new MyWebSocketListener());
    }

    public void sendMessage(String message) {
        webSocket.send(message);
    }

    public void closeWebSocket() {
        webSocket.close(1000, "User initiated");
    }

    private class MyWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            // WebSocket 连接已打开
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            // 收到文本消息
            Log.e(TAG, "收到 WebSocket 数据，onMessage: "+text );
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            // WebSocket 已关闭
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            // 连接失败或发生错误
        }
    }

}
