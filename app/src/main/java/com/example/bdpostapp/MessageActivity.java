package com.example.bdpostapp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bdpostapp.Adapter.MessageListAdapter;
import com.example.bdpostapp.Base.BaseActivity;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Message;
import com.example.bdpostapp.Global.Constant;
import com.example.bdpostapp.Global.Variable;
import com.example.bdpostapp.Request.AccountInterface;
import com.example.bdpostapp.Utils.AudioTrackUtil;
import com.example.bdpostapp.Utils.NotificationCenter;
import com.example.bdpostapp.Utils.RequestUtil;
import com.example.bdpostapp.ViewModel.DataViewModel;
import com.example.bdpostapp.databinding.ActivityMessageBinding;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

import java.util.List;

public class MessageActivity extends BaseActivity implements NotificationCenter.NotificationCenterDelegate{

    ActivityMessageBinding messageView;
    AccountInterface request;
    DataViewModel dataViewModel;

    String remark;
    String addr;
    MessageListAdapter adapter;
    int new_chat_id = 0;
    int now_page = 1;

    @Override
    protected Object setLayout() {
        messageView = ActivityMessageBinding.inflate(getLayoutInflater());
        return messageView.getRoot();
//        return R.layout.activity_message;
    }

    @Override
    protected void beforeSetLayout() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        remark = getIntent().getStringExtra(Constant.REMARK);
        addr = getIntent().getStringExtra(Constant.ADDR);
        if(remark!=null && !remark.equals("")){
            messageView.title.setText(remark);
        }else {
            if(addr!=null){
                messageView.title.setText(addr);
            }
        }

        NotificationCenter.standard().addveObserver(this,Constant.RECEIVE_MESSAGE);
        NotificationCenter.standard().addveObserver(this,Constant.NEW_IMAGE_MSG);

        RequestUtil.getInstance().getMessage(addr,now_page);
//        get_message(now_page);
        initViewModel();
        init_control();

    }

    @Override
    protected void initData() {

    }

    @Override
    protected String getTAG() {
        return "MessageActivity";
    }

    public void initViewModel(){
        dataViewModel = application.dataViewModel;
        if(dataViewModel==null){return;}
        dataViewModel.getMessage_list().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messageList) {
                loge("已有列表消息数量："+messageList.size());
                if(now_page==1){
                    adapter.notifyData(messageList);
                    adapter.scrollToBottom();
                }else {
                    adapter.notifyDataBefore(messageList);
                }
                if(messageView.refreshLayout.isRefreshing()){messageView.refreshLayout.finishRefresh();}
            }
        });

    }

    public void init_control(){

        messageView.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        messageView.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loge("刷新消息列表");
                if(now_page == Variable.maximum_page){
                    messageView.refreshLayout.finishRefresh(500);
                    application.showToast("没有更多了!",0);
                    return;
                }
                now_page++;
                RequestUtil.getInstance().getMessage(addr,now_page);
            }
        });

        adapter = new MessageListAdapter(this,dataViewModel.getMessage_list().getValue());
        messageView.messageList.setAdapter(adapter);
        adapter.setRecyclerView(messageView.messageList);
        messageView.messageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

// 通知中心消息 ------------------------------
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if(id == Constant.RECEIVE_MESSAGE){
            if(args==null){return;}
            JSONObject data = (JSONObject) args[0];
            try {
                JSONObject jsonObject = data.getJSONObject("chatInfo");
                // 判断收到的消息是否来自于当前设备
                if(jsonObject.getString("from").equals(addr)){
//                    new_chat_id = jsonObject.getInt("chatId");
                    loge("消息是这个设备的");
                    now_page = 1;
                    RequestUtil.getInstance().getMessage(addr,now_page);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        else if(id == Constant.NEW_IMAGE_MSG){
            if(args==null){return;}
            String data = (String) args[0];
            try {
                List<Message> messages = dataViewModel.getMessage_list().getValue();
                // 判断这个消息的id是不是刚刚接收到的
                if(messages.get(messages.size() - 1).id.equals(data)){
                    now_page = 1;
                    RequestUtil.getInstance().getMessage(addr,now_page);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        NotificationCenter.standard().removeObserver(this);  // 释放资源
        if (AudioTrackUtil.getInstance().isStart()) {
            AudioTrackUtil.getInstance().stopPlay();
        }
        super.onDestroy();
    }
}
