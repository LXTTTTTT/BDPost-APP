package com.example.bdpostapp.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bdpostapp.Adapter.ChatListAdapter;
import com.example.bdpostapp.Base.BaseFragment;
import com.example.bdpostapp.Base.MainApplication;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Chat;
import com.example.bdpostapp.Global.Constant;
import com.example.bdpostapp.MessageActivity;
import com.example.bdpostapp.Request.AccountInterface;
import com.example.bdpostapp.Utils.NotificationCenter;
import com.example.bdpostapp.Utils.PermissionUtil;
import com.example.bdpostapp.ViewModel.DataViewModel;
import com.example.bdpostapp.databinding.FragmentMessageBinding;

import java.util.List;

public class MessageFragment extends BaseFragment{

    private FragmentMessageBinding viewBinding;
    DataViewModel dataViewModel;
    ChatListAdapter adapter;

    public void beforeSetLayout() {

    }


    public View setLayout() {
        viewBinding = FragmentMessageBinding.inflate(getLayoutInflater());
        return viewBinding.getRoot();
    }

    public void initView(Bundle savedInstanceState) {
        init_control();
        initViewModel();

    }

    @Override
    public void initData() {
    }

    public String getTAG() {
        return "MessageFragment";
    }

    public void initViewModel(){
        dataViewModel = application.dataViewModel;
        if(dataViewModel==null){return;}
        dataViewModel.getChat_list().observe(this, new Observer<List<Chat>>() {
            @Override
            public void onChanged(List<Chat> chatList) {
                loge("获取到了列表消息数量："+chatList.size());
                adapter.upDateList(chatList);
            }
        });

    }

    public void init_control(){

        adapter = new ChatListAdapter(getActivity(), new ChatListAdapter.onItemClickListener() {
            @Override
            public void onItemClickListener(String addr,String remark) {
                loge("点击的备注是："+remark+" 卡号："+addr);
                // 进入消息页
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                intent.putExtra(Constant.REMARK,remark);
                intent.putExtra(Constant.ADDR,addr);
                getActivity().startActivity(intent);
            }
        });
        viewBinding.messageList.setAdapter(adapter);
        viewBinding.messageList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}