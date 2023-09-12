package com.example.bdpostapp.Utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.example.bdpostapp.Base.MainApplication;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Account;
import com.example.bdpostapp.Entity.GsonBean.Receiving.BaseRequest;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Chat;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Group;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Message;
import com.example.bdpostapp.Entity.GsonBean.Receiving.MessageInfo;
import com.example.bdpostapp.Entity.GsonBean.Receiving.MyMarker;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Normal;
import com.example.bdpostapp.Entity.GsonBean.Receiving.TerminalDetail;
import com.example.bdpostapp.Global.Constant;
import com.example.bdpostapp.Global.Variable;
import com.example.bdpostapp.MainActivity;
import com.example.bdpostapp.Request.AccountInterface;
import com.example.bdpostapp.ViewModel.DataViewModel;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

// 请求工具
public class RequestUtil {

    String TAG = "RequestUtil";
    AccountInterface request;
    DataViewModel dataViewModel;

// 单例 ----------------------------------------------------
    private static RequestUtil requestUtil;
    public static RequestUtil getInstance() {
        if(requestUtil == null){
            requestUtil = new RequestUtil();
        }
        return requestUtil;
    }

    public void init(AccountInterface request,DataViewModel dataViewModel){
        this.request = request;
        this.dataViewModel = dataViewModel;
    }

    public void getxxx(){
        try{

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void login(String account, String password, Activity activity){
        try{
            Log.e(TAG, "登录账号：" + account + " 密码：" + password );
            String password_md5 = TaoUtil.string2MD5(password);
            request.pwdLogin(account,password_md5)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<BaseRequest<Account>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            MainApplication.getInstance().showLoadingDialog(activity);
                        }

                        @Override
                        public void onNext(BaseRequest<Account> accountData) {
                            if(accountData.msg.equals("成功")){
                                Log.e(TAG, "登录成功");
                                MainApplication.getInstance().showToast("登陆成功",0);
                                dataViewModel.setAccount_info(accountData.data);
                                SharedPreferencesUtil.getInstance().setString(Constant.ACCOUNT_S,account);
                                SharedPreferencesUtil.getInstance().setString(Constant.PASSWORD_S,password);
                                Variable.token = accountData.data.token;
                                Log.e(TAG, "获取的token："+Variable.token);
                                activity.startActivity(new Intent(activity,MainActivity.class));
                                activity.finish();
//                            SharedPreferencesUtil.getInstance().setString(Constant.TOKEN,accountData.data.token);
                                getChatList();  // 开始获取聊天消息列表
                                MainApplication.getInstance().startWebSocketService();  // 开启websocket
                                // 获取枚举映射
                                getDeviceTypes();
                                getLocationTypes();
                                getLocationStatus();
                            }else {
                                MainApplication.getInstance().showToast("登陆失败："+accountData.msg,0);
                                MainApplication.getInstance().hideLoadingDialog();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            MainApplication.getInstance().hideLoadingDialog();
                            MainApplication.getInstance().showToast("登陆失败，网络连接错误",0);
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            MainApplication.getInstance().hideLoadingDialog();
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void getDeviceTypes(){
        try{
            request.getDeviceTypes(Variable.token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<BaseRequest<List<Normal>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseRequest<List<Normal>> loc_types) {
                            if(loc_types.msg.equals("成功")){
                                Log.e(TAG, "获取 定位类型 成功");
                                MapMarkerUtil.getInstance().init_device_map(loc_types.data);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getLocationTypes(){
        try{
            request.getLocationTypes(Variable.token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<BaseRequest<List<Normal>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseRequest<List<Normal>> loc_types) {
                            if(loc_types.msg.equals("成功")){
                                Log.e(TAG, "获取 定位类型 成功");
                                MapMarkerUtil.getInstance().init_loc_map(loc_types.data);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getLocationStatus(){
        try{
            request.getLocationStatus(Variable.token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<BaseRequest<List<Normal>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseRequest<List<Normal>> loc_types) {
                            if(loc_types.msg.equals("成功")){
                                Log.e(TAG, "获取 定位状态 成功");
                                MapMarkerUtil.getInstance().init_status_map(loc_types.data);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getChatList(){
        try{
            request.getChatList(Variable.token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<BaseRequest<List<Chat>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
//                        showLoadingDialog(now_activity);
                        }

                        @Override
                        public void onNext(BaseRequest<List<Chat>> accountData) {
                            if(accountData.msg.equals("成功")){
                                Log.e(TAG, "获取聊天列表成功");
                                MainApplication.getInstance().showToast("获取聊天列表成功",0);
//                            MainApplication.getInstance().hideLoadingDialog();
                                dataViewModel.setChat_list(accountData.data);
                            }else {
                                MainApplication.getInstance().showToast("获取聊天列表失败："+accountData.msg,0);
//                            MainApplication.getInstance().hideLoadingDialog();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
//                        MainApplication.getInstance().hideLoadingDialog();
                            MainApplication.getInstance().showToast("获取聊天列表失败",0);
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
//                        MainApplication.getInstance().hideLoadingDialog();
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 获取设备消息
    public void getMessage(String addr,int page){

        String url = Constant.BASE_URL + Constant.MESSAGE_LIST + addr;
        request.getMessageList(url, Variable.token,page+"","10")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<BaseRequest<MessageInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        showLoadingDialog(now_activity);
                    }

                    @Override
                    public void onNext(BaseRequest<MessageInfo> messageList) {
                        if(messageList.msg.equals("成功")){
//                            MainApplication.getInstance().showToast("获取消息列表成功："+messageList.msg,0);
                            List<Message> messages = messageList.data.items;
                            Collections.reverse(messages);  // 序列反转
                            // 处理接收文件
                            for (Message message : messages) {
                                if(message.chatType.name.equals("VOICE")){
                                    try {
                                        String filePath = FilePathUtil.getVoiceFile();
                                        String fileName = message.id + "RECEIVE.wav";
                                        // 判断文件是否存在
                                        File voice_file = new File(filePath+fileName);
                                        if(!voice_file.exists()){
                                            // 存储语音文件路径
                                            HashMap<String,String> header = new HashMap<>();
                                            header.put("key","token");
                                            header.put("value",Variable.token);
                                            String fileId = message.voiceInfo.enhanceFileId!=null ? message.voiceInfo.enhanceFileId:message.voiceInfo.fileId;
                                            TaoUtil.saveFileByURL(Constant.BASE_URL + Constant.FILE , fileId , fileName, header);
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                // 图片不需要下载，直接用 glide 加载链接

                            }
                            dataViewModel.setMessage_list(messageList.data.items);
                            Variable.maximum_page = messageList.data.totalPage;
                            // 清除未读消息
                            clearUnread(addr);
                        }else {
                            MainApplication.getInstance().showToast("获取消息列表失败："+messageList.msg,0);
//                            hideLoadingDialog();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                        hideLoadingDialog();
                        MainApplication.getInstance().showToast("获取消息列表失败",0);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
//                        hideLoadingDialog();
                    }
                });




    }


    public void clearUnread(String addr){
        String url_2 = Constant.BASE_URL + Constant.UNREAD_MESSAGE(addr);
        request.clearUnreadMessages(url_2, Variable.token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<BaseRequest<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseRequest<String> messageList) {
                        if(messageList.msg.equals("成功")){
                            Log.e(TAG, "清空未读消息: ");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    public void getMarker(LatLng farLeft,LatLng nearRight,float pixelInMeter,int zoom){
        try {
            request.getMarker(Variable.token, farLeft.latitude+"",farLeft.longitude+"",nearRight.latitude+"",nearRight.longitude+"",pixelInMeter+"",zoom+"")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<BaseRequest<List<MyMarker>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseRequest<List<MyMarker>> messageList) {
                            if(messageList.msg.equals("成功")){
                                Log.e(TAG, "获取 marker 点成功");
                                dataViewModel.setMarker_list(messageList.data);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }catch (Exception e){e.printStackTrace();}
    }

    public void getTerminalDetail(String addr){
        try {
            HashMap<String,String> addr_map = new HashMap<>();
            addr_map.put("addrs",addr);
            request.getTerminalDetail(Variable.token,addr_map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<BaseRequest<List<TerminalDetail>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseRequest<List<TerminalDetail>> messageList) {
                            if(messageList.msg.equals("成功")){
                                Log.e(TAG, "获取 terminal_detail 成功");
                                try{
                                    dataViewModel.setTerminal_detail(messageList.data.get(0));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getTerminalList(){
        try {
            request.getTerminalList(Variable.token,true)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<BaseRequest<List<Group>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseRequest<List<Group>> messageList) {
                            if(messageList.msg.equals("成功")){
                                Log.e(TAG, "获取 TerminalList 成功");
                                dataViewModel.setGroup_list(messageList.data);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
