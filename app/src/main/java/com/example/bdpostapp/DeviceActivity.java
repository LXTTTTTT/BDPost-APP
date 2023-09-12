package com.example.bdpostapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.lifecycle.Observer;

import com.example.bdpostapp.Adapter.ExpandableListAdapter;
import com.example.bdpostapp.Base.BaseActivity;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Group;
import com.example.bdpostapp.Global.Constant;
import com.example.bdpostapp.Utils.NotificationCenter;
import com.example.bdpostapp.Utils.RequestUtil;
import com.example.bdpostapp.ViewModel.DataViewModel;
import com.example.bdpostapp.databinding.ActivityDeviceBinding;
import com.example.bdpostapp.databinding.ActivityMessageBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceActivity extends BaseActivity implements NotificationCenter.NotificationCenterDelegate{

    ActivityDeviceBinding viewBinding;
    DataViewModel dataViewModel;

    String remark;
    String addr;
    ExpandableListAdapter adapter;
    List<Group> showList = new ArrayList<>();  // 当前显示的列表数据

    @Override
    protected Object setLayout() {
        viewBinding = ActivityDeviceBinding.inflate(getLayoutInflater());
        return viewBinding.getRoot();
//        return R.layout.activity_message;
    }

    @Override
    protected void beforeSetLayout() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        remark = getIntent().getStringExtra(Constant.REMARK);
        addr = getIntent().getStringExtra(Constant.ADDR);

        NotificationCenter.standard().addveObserver(this,Constant.RECEIVE_OK_SOS);

        RequestUtil.getInstance().getTerminalList();
        initViewModel();
        init_control();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected String getTAG() {
        return "DeviceActivity";
    }

    public void initViewModel(){
        dataViewModel = application.dataViewModel;
        if(dataViewModel==null){return;}
        dataViewModel.getGroup_list().observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(List<Group> groupList) {
                loge("已有设备列表数量："+groupList.size());
                showList.clear();
                showList.addAll(groupList);
                adapter.setData(showList);
                for (int i = 0; i < groupList.size(); i++) {
                    try {
                        viewBinding.deviceList.expandGroup(i);
                    }catch (Exception e){e.printStackTrace();}
                }
                String search_text = viewBinding.searchBar.getText().toString();
                if(!search_text.equals("")){adapter.searchData(search_text);}
            }
        });


    }


    public void init_control(){

        List<String> groupList = new ArrayList<>();
        groupList.add("Group 1");
        groupList.add("Group 2");
        groupList.add("Group 3");

        HashMap<String, List<String>> childMap = new HashMap<>();
        childMap.put("Group 1", new ArrayList<>(java.util.Arrays.asList("Child 1", "Child 2", "Child 3")));
        childMap.put("Group 2", new ArrayList<>(java.util.Arrays.asList("Child A", "Child B")));
        childMap.put("Group 3", new ArrayList<>(java.util.Arrays.asList("Child X", "Child Y", "Child Z")));

        adapter = new ExpandableListAdapter(this,dataViewModel.getGroup_list().getValue());
        adapter.setOnItemClickListener(new ExpandableListAdapter.onItemClickListener() {
            @Override
            public void onItemClickListener(String addr) {
                loge("点击了："+addr);
                if(addr.equals("NOT")){
                    application.showToast("当前设备未连接",0);
                }else {
                    finish();
                    NotificationCenter.standard().postNotification(Constant.CHANGE_MARKER,addr);
                }
            }
        });
        viewBinding.deviceList.setAdapter(adapter);

        viewBinding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search_text = viewBinding.searchBar.getText().toString();
                adapter.searchData(search_text);
            }
        });

        viewBinding.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewBinding.searchBar.setText("");
            }
        });


        viewBinding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(viewBinding.searchBar.getText().toString().length()>0){
                    viewBinding.clearButton.setVisibility(View.VISIBLE);
                }else{
                    viewBinding.clearButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        viewBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

// 通知中心消息 ------------------------------
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if(id == Constant.RECEIVE_OK_SOS){
            RequestUtil.getInstance().getTerminalList();
        }
    }

    @Override
    protected void onDestroy() {
        NotificationCenter.standard().removeObserver(this);  // 释放资源
        super.onDestroy();
    }
}
