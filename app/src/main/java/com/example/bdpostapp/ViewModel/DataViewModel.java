package com.example.bdpostapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.bdpostapp.Entity.GsonBean.Receiving.Account;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Chat;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Group;
import com.example.bdpostapp.Entity.GsonBean.Receiving.MyMarker;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Message;
import com.example.bdpostapp.Entity.GsonBean.Receiving.TerminalDetail;

import java.util.ArrayList;
import java.util.List;

// 数据视图模型
public class DataViewModel extends ViewModel {

    private String TAG = "DataViewModel";

    private MutableLiveData<Account> account_info = new MutableLiveData<>();  // 账号信息
    private MutableLiveData<List<Chat>> chat_list = new MutableLiveData<>();  // 聊天列表
    private MutableLiveData<List<Message>> message_list = new MutableLiveData<>();  // 消息列表
    private MutableLiveData<List<MyMarker>> marker_list = new MutableLiveData<>();  // 地图页的 marker 列表
    private MutableLiveData<TerminalDetail> terminal_detail = new MutableLiveData<>();  // 地图页的 marker 列表
    private MutableLiveData<List<Group>> group_list = new MutableLiveData<>();  // 设备列表
//    {
//        initData();
//    }
    public DataViewModel(){
        initData();
    }
    public void initData(){
        account_info.postValue(new Account());
        chat_list.postValue(new ArrayList<>());
        message_list.postValue(new ArrayList<>());
        marker_list.postValue(new ArrayList<>());
        terminal_detail.postValue(new TerminalDetail());
        group_list.postValue(new ArrayList());
    }

    public LiveData<Account> getAccount_info() {return account_info;}
    public void setAccount_info(Account account_info){this.account_info.postValue(account_info);}

    public LiveData<List<Chat>> getChat_list() {return chat_list;}
    public void setChat_list(List<Chat> chat_list){
        this.chat_list.postValue(chat_list);
    }

    public LiveData<List<Message>> getMessage_list() {return message_list;}
    public void setMessage_list(List<Message> message_list){this.message_list.postValue(message_list);}

    public LiveData<List<MyMarker>> getMarker_list() {return marker_list;}
    public void setMarker_list(List<MyMarker> myMarker_list){this.marker_list.postValue(myMarker_list);}

    public LiveData<TerminalDetail> getTerminal_detail() {return terminal_detail;}
    public void setTerminal_detail(TerminalDetail terminal_detail){this.terminal_detail.postValue(terminal_detail);}

    public LiveData<List<Group>> getGroup_list() {return group_list;}
    public void setGroup_list(List<Group> group_list){
        this.group_list.postValue(group_list);
    }
}