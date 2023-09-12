package com.example.bdpostapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.bdpostapp.Base.BaseActivity;
import com.example.bdpostapp.Base.MainApplication;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Account;
import com.example.bdpostapp.Fragment.MapFragment;
import com.example.bdpostapp.Fragment.MessageFragment;
import com.example.bdpostapp.Global.Constant;
import com.example.bdpostapp.Utils.DataUtil;
import com.example.bdpostapp.Utils.PermissionUtil;
import com.example.bdpostapp.ViewModel.DataViewModel;
import com.example.bdpostapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    ActivityMainBinding View;
    DataViewModel dataViewModel;

    private MessageFragment messageFragment;
    private MapFragment mapFragent;

    private long backPressedTime = 0;
    private long backPressInterval = 2000;

    @Override
    protected Object setLayout() {
        View = ActivityMainBinding.inflate(getLayoutInflater());
        return View.getRoot();
    }

    @Override
    protected void beforeSetLayout() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

//        String account = "13800138000";
//        String password = "123abc!!";

//        String account = "admin";
//        String password = "admin@bd3!!";

        String account = "test_lxt";
        String password = "123456";

//        RequestUtil.getInstance().login(account,password,this);  // 登录账号

        init_control();
        initViewModel();
        initPermissions();

    }

    @Override
    protected void initData() {

    }

    @Override
    protected String getTAG() {
        return "MainActivity";
    }

    public void init_control(){
        messageFragment = new MessageFragment();
        mapFragent = new MapFragment();
        View.bottomBar.setContainer(R.id.fragment)  // 设置需要切换的控件
                .setFirstChecked(0)  // 设置默认页数
                .setTitleBeforeAndAfterColor("#7f7f7f", "#00BFFF")  // 设置文字选中和未选中的颜色
                .addItem(messageFragment,"消息",R.mipmap.msg_tab_pre, R.mipmap.msg_tab_on)
                .addItem(mapFragent,"地图",R.mipmap.location_tab_pre, R.mipmap.location_tab_on)
                .buildTwo();

        View.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                application.showWarnDialog(MainActivity.this, "是否退出登录", new View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View view) {
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        finish();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View view) {
                        application.hideWarnDialog();
                    }
                });
            }
        });

        View.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                finish();
            }
        });
    }

    public void initViewModel(){
        dataViewModel = application.dataViewModel;
        if(dataViewModel==null){return;}
        dataViewModel.getAccount_info().observe(this, new Observer<Account>() {
            @Override
            public void onChanged(Account account_info) {
                loge("获取账号信息："+ account_info.account);
                if(account_info.account!=null){View.account.setText(account_info.account);}
                if(account_info.role.value!=null){View.role.setText(account_info.role.value);}
                if(account_info.avatar!=null){
                    loge("有头像");
//                    String url = Constant.BASE_URL + Constant.FILE + "oss323032332f382f372f3634643063313935366361353537376434646335386261632e77656270";
                    String url = Constant.BASE_URL + Constant.FILE + account_info.avatar;
                    Glide.with(MainActivity.this)
                            .load(url)
                            .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.error_bg).error(R.mipmap.error_bg))
                            .transform(new CircleCrop())
                            .into(View.headIcon);
                }else {
                    Glide.with(MainActivity.this)
                            .load(R.drawable.corner_fill_white_2)
                            .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.error_bg).error(R.mipmap.error_bg))
                            .transform(new CircleCrop())
                            .into(View.headIcon);
                }

                if(account_info.name!=null){View.name.setText(account_info.platformName);}
                else {View.name.setText("未设置");}
                if(account_info.phone!=null){View.phone.setText(account_info.phone);}
                else {View.phone.setText("未绑定");}
                if(account_info.email!=null){View.email.setText(account_info.email);}
                else {View.email.setText("未绑定");}

                if(account_info.platformName!=null){View.platformName.setText(account_info.platformName);}
                else {View.platformName.setText("未命名");}
                if(account_info.platformLogo!=null){
                    loge("有平台头像");
                    String url = Constant.BASE_URL + Constant.FILE + account_info.platformLogo;
                    Glide.with(MainActivity.this)
                            .load(url)
                            .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.error_bg).error(R.mipmap.error_bg))
                            .transform(new CircleCrop())
                            .into(View.platformIcon);
                }else {
                    Glide.with(MainActivity.this)
                        .load(R.drawable.corner_fill_white_2)
                        .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.error_bg).error(R.mipmap.error_bg))
                        .transform(new CircleCrop())
                        .into(View.platformIcon);
                }

            }
        });
    }

    // 申请权限
    protected void initPermissions() {
        List<Integer> list = new ArrayList<>();
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_CODE_INTERNET)) {
            list.add(PermissionUtil.PERMISSION_REQUEST_CODE_INTERNET);
        }
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_ACCESS_NETWORK_STATE)) {
            list.add(PermissionUtil.PERMISSION_REQUEST_ACCESS_NETWORK_STATE);
        }
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_ACCESS_WIFI_STATE)) {
            // 后台定位需要手动打开
            list.add(PermissionUtil.PERMISSION_REQUEST_ACCESS_WIFI_STATE);
        }
        // 读写手机存储
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_READ_PHONE_STATE)) {
            list.add(PermissionUtil.PERMISSION_REQUEST_READ_PHONE_STATE);
        }
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_CHANGE_WIFI_STATE)) {
            list.add(PermissionUtil.PERMISSION_REQUEST_CHANGE_WIFI_STATE);
        }
//        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE)) {
//            list.add(PermissionUtil.PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE);
//        }

        // 定位
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_CODE_ACCESS_COARSE_LOCATION)) {
            list.add(PermissionUtil.PERMISSION_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION)) {
            list.add(PermissionUtil.PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION);
        }
//        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_CODE_ACCESS_BACKGROUND_LOCATION)) {
//            // 后台定位需要手动打开
//            list.add(PermissionUtil.PERMISSION_REQUEST_CODE_ACCESS_BACKGROUND_LOCATION);
//        }
        // 读写手机存储
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE)) {
            list.add(PermissionUtil.PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE)) {
            list.add(PermissionUtil.PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_CODE_FOREGROUND_SERVICE)) {
            list.add(PermissionUtil.PERMISSION_REQUEST_CODE_FOREGROUND_SERVICE);
        }
        if (!PermissionUtil.isPermissionGranted(this, PermissionUtil.PERMISSION_REQUEST_CODE_VIBRATE)) {
            list.add(PermissionUtil.PERMISSION_REQUEST_CODE_VIBRATE);
        }

        for (Integer integer : list) {
            Log.e(TAG, "没给权限: " + DataUtil.int2Hex(integer) );
        }

        // 所有权限都授予了,直接回调授权
        if (list.size() < 1) {
            onPermissionRequestResult(1, true);
        } else {
            int[] permissions = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                // 批量申请权限列表
                permissions[i] = list.get(i);
            }
            // 申请权限
            PermissionUtil.requestPermissionsByRequestCodes(this, permissions, PermissionUtil.PERMISSION_REQUEST_ALL);
        }

        // 检查存储权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!Environment.isExternalStorageManager()){
                MainApplication.getInstance().showWarnDialog(this, "为保证功能正常使用，请授予所有文件权限", new View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View view) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        application.hideWarnDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View view) {
                        MainApplication.getInstance().hideWarnDialog();
                        application.showToast("某些功能将无法正常使用!",0);
                    }
                });
            }
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                loge("未授权读写文件");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 13);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        application.stopWebSocketService();
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if(currentTime - backPressedTime < backPressInterval){
            super.onBackPressed();
        }else {
            application.showToast("再次点击返回按键退出程序",0);
            backPressedTime = currentTime;
        }

    }
}