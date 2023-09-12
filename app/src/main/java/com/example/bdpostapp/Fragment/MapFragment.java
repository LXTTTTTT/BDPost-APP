package com.example.bdpostapp.Fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.VisibleRegion;
import com.example.bdpostapp.Base.BaseFragment;
import com.example.bdpostapp.Base.MainApplication;
import com.example.bdpostapp.DeviceActivity;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Group;
import com.example.bdpostapp.Entity.GsonBean.Receiving.MyMarker;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Terminal;
import com.example.bdpostapp.Entity.GsonBean.Receiving.TerminalDetail;
import com.example.bdpostapp.Global.Constant;
import com.example.bdpostapp.Global.Variable;
import com.example.bdpostapp.R;
import com.example.bdpostapp.Utils.DataUtil;
import com.example.bdpostapp.Utils.FilePathUtil;
import com.example.bdpostapp.Utils.FileUtil;
import com.example.bdpostapp.Utils.MapMarkerUtil;
import com.example.bdpostapp.Utils.NotificationCenter;
import com.example.bdpostapp.Utils.PermissionUtil;
import com.example.bdpostapp.Utils.RequestUtil;
import com.example.bdpostapp.Utils.TaoUtil;
import com.example.bdpostapp.View.MapTypeDialog;
import com.example.bdpostapp.ViewModel.DataViewModel;
import com.example.bdpostapp.databinding.FragmentMapBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends BaseFragment implements NotificationCenter.NotificationCenterDelegate{

    String TAG = "MapFragment";
    private FragmentMapBinding viewBinding; // Rename the variable to avoid conflicts
    DataViewModel dataViewModel;
    Handler handler = new Handler();

    MapTypeDialog mapTypeDialog;  // 选择图层 dialog

    AMap aMap;  // 地图操作对象

    private final int NORMAL = 1;
    private final int CHOICE_DEVICE = 2;
    private final int CHANGE_MARKER = 3;
    private final int type4 = 1;
    int movement_type = NORMAL;  // 当前的地图视角切换模式：根据切换模式在地图视角移动时做不同的操作

    LatLng farLeft;  // 当前地图 左上角经纬度
    LatLng nearRight;  // 当前地图 右下角经纬度
    float pixelInMeter;  // 当前地图 每像素对应距离
    int zoom;  // 当前地图 缩放层级，四舍五入

    Marker current_marker;  // 当前选中的 marker
    String current_addr="";  // 当前选中的 marker 卡号
    boolean first_time = true;  // 首次加载地图移动到第一个设备
    @Override
    public void beforeSetLayout() {

    }

    @NonNull
    @Override
    public Object setLayout() {
        viewBinding = FragmentMapBinding.inflate(getLayoutInflater());
        return viewBinding.getRoot();
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        viewBinding.map.onCreate(savedInstanceState);// 此方法必须重写
        RequestUtil.getInstance().getTerminalList();  // 拿到第一个设备的位置
        init_control();
        init_map();
        init_gps();
        init_viewmodel();
    }

    @Override
    public void initData() {
        NotificationCenter.standard().addveObserver(this, Constant.CHANGE_MARKER);
        NotificationCenter.standard().addveObserver(this, Constant.RECEIVE_OK_SOS);
    }

    @NonNull
    @Override
    public String getTAG() {
        return "MapFragment";
    }



    public void init_map(){
        if (aMap == null) {
            aMap = viewBinding.map.getMap();
            MapMarkerUtil.getInstance().init(getActivity(),aMap);  // 绑定 marker 工具
            // 拿到当前视角的坐标并获取 marker
            farLeft = new LatLng(40.390244126857,116.05125482032554); //可视区域的左上角。
            nearRight = new LatLng(39.4549851333403,116.74447038726093); //可视区域的右下角
            pixelInMeter = 117.2382f; //每像素对应距离
            zoom = 10; //地图缩放层级，四舍五入
            // 每15秒获取一次当前视角的 marker
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        RequestUtil.getInstance().getMarker(farLeft,nearRight,pixelInMeter,zoom);
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            loge("onCreate: 地图生成成功："+farLeft.longitude+"/"+farLeft.latitude+"/"+nearRight.longitude+"/"+nearRight.latitude+"/"+pixelInMeter+"/"+zoom);
        }
        // 地图操作
        aMap.getUiSettings().setZoomControlsEnabled(false);  // 隐藏地图放大缩小按键
        aMap.getUiSettings().setRotateGesturesEnabled(false);  // 关闭地图旋转手势
        // 定位蓝点
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle(); //初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER); //连续定位，定位点依照设备方向旋转。
        myLocationStyle.interval(10*1000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true); //设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0)); // 设置圆形的边框颜色为透明
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0)); // 设置圆形的填充颜色为透明
        myLocationStyle.myLocationIcon( BitmapDescriptorFactory.fromView(getLayoutInflater().inflate(R.drawable.map_marker_current_location_layout, null)));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的样式属性Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点
        // 地图视角移动监听
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {}

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                VisibleRegion visibleRegion = aMap.getProjection().getVisibleRegion();
                farLeft = visibleRegion.farLeft; //可视区域的左上角。
                nearRight = visibleRegion.nearRight; //可视区域的右下角
                pixelInMeter = aMap.getScalePerPixel(); //每像素对应距离
                zoom = Math.round(aMap.getCameraPosition().zoom); //地图缩放层级，四舍五入
                // 根据地图切换模式做出对应操作
                switch (movement_type){
                    // 普通视角移动：获取marker
                    case NORMAL:
                        // 获取位置信息
                        loge("地图层级："+zoom + "  左上角：经纬度" + farLeft + "  右下角：经纬度" + nearRight + "地图像素对应距离：" + pixelInMeter);
                        RequestUtil.getInstance().getMarker(farLeft,nearRight,pixelInMeter,zoom);
                        break;
                    // 点击marker：不操作并把操作模式改回来
                    case CHOICE_DEVICE:
                        movement_type = NORMAL;  // 显示完后要变回来
                        break;
                    case CHANGE_MARKER:
                        RequestUtil.getInstance().getMarker(farLeft,nearRight,pixelInMeter,zoom);
                        movement_type = NORMAL;  // 显示完后要变回来
                        break;
                }
            }
        });
        // 地图 marker 点击事件
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String addr = marker.getTitle();
                loge("点击了标记点 title："+ addr + " 标识："+marker.getOptions().getDisplayLevel());
                switch (marker.getOptions().getDisplayLevel()){
                    case MapMarkerUtil.TERMINAL_MARKER:
                        // 设备点
                        movement_type = CHOICE_DEVICE;  // 修改地图视角切换模式
                        current_marker = marker;  // 改变当前marker对象
                        current_addr = marker.getTitle();
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),aMap.getMaxZoomLevel()-2));  // 放大地图
                        RequestUtil.getInstance().getTerminalDetail(addr);  // 获取这个marker的信息
//                        viewBinding.terminalDetail.setVisibility(View.VISIBLE);
//                        showLinearLayout(viewBinding.terminalDetail);
                        if(viewBinding.terminalDetail.getVisibility()==View.GONE){
                            animateView(viewBinding.terminalDetail);
                        }
//                        application.showCustomDialog(getActivity());  // 显示信息弹窗
                        marker.showInfoWindow();  // 显示marker点window
                        break;

                    case MapMarkerUtil.GROUP_MARKER:
                        movement_type = NORMAL;
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),aMap.getCameraPosition().zoom + 2));  // 放大地图
                        break;
                    default:
                        break;
                }
                return true;  // 消费点击事件
            }
        });
    }

    public void init_control(){
        List<String> list = new ArrayList<>();
        list.add("卫星图");
        list.add("标准图");
        mapTypeDialog = new MapTypeDialog(getContext(), new MapTypeDialog.OnSelectListener() {
            @Override
            public void onNormal() {
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            }

            @Override
            public void onSatellite() {
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
            }

            @Override
            public void onTopographic() {
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);
            }
        });

        viewBinding.mapTypeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aMap==null){
                    application.showToast("地图加载中！",0);
                    return;
                }
                MapTypeDialog.show(mapTypeDialog);
            }
        });

        viewBinding.downloadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), com.amap.api.maps.offlinemap.OfflineMapActivity.class));
            }
        });

        viewBinding.locationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCenter();
            }
        });

        viewBinding.amplification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aMap.animateCamera(CameraUpdateFactory.zoomTo(aMap.getCameraPosition().zoom+1));
            }
        });

        viewBinding.narrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aMap.animateCamera(CameraUpdateFactory.zoomTo(aMap.getCameraPosition().zoom-1));
//                MainApplication.getInstance().showNotification("测试");
            }
        });

        viewBinding.deviceMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DeviceActivity.class));
            }
        });

        viewBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                hideLinearLayout(viewBinding.terminalDetail);
                animateView(viewBinding.terminalDetail);
//                viewBinding.terminalDetail.setVisibility(View.GONE);
            }
        });

    }

    public void init_gps(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            application.showWarnDialog(null, "请打开手机GPS定位，\n否则APP将不能正常使用", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 22);
                    application.hideWarnDialog();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    application.hideWarnDialog();
                }
            });
            return;
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        application.getInstance().init_location();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                showCenter();
//                            }
//                        }, 4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return;
        }

        application.init_location();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showCenter();
//            }
//        }, 10000);
    }

    private void showCenter() {
        try {
            if((Variable.latitude == 0.0d || Variable.longitude == 0.0d)){
                application.showToast("暂无位置信息",0);
                return;
            }
            moveCamera(0,Variable.latitude, Variable.longitude,aMap.getMaxZoomLevel()-6);
//            aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//                    if (marker.isInfoWindowShown()) {
//                        marker.hideInfoWindow();
//                    } else {
//                        marker.showInfoWindow();
//                    }
//                    return true;
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 移动视角
    private void moveCamera(int type, double latitude, double longitude, float zoom){
        if(aMap==null){return;}
        if(type==0){
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        }else {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        }

    }



    // 以动画效果 显示/隐藏 控件
    public void animateView(View view) {
        int visibility;
        if (view.getVisibility() == View.VISIBLE) {visibility = View.GONE;}
        else {visibility = View.VISIBLE;}
        if (visibility == View.VISIBLE) {
            view.setAlpha(0f); // 初始化透明度为0，将视图设为不可见
            view.setVisibility(View.VISIBLE);
        }
        view.animate()
                .translationY(visibility == View.VISIBLE ? 0 : view.getHeight()) // 设置Y轴平移
                .alpha(visibility == View.VISIBLE ? 1f : 0f) // 设置透明度：出现/消失时的渐变效果
                .setDuration(400) // 动画持续时间
                .setInterpolator(new AccelerateInterpolator()) // 设置插值器
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(visibility);
                    }
                })
                .start();
    }


    int info_text_size = 13;  // 信息文本的字体大小
    int info_pedding_size = DataUtil.dp_to_px(MainApplication.getInstance(), 5);  // 信息文本的上部填充距离
    public void init_viewmodel(){
        dataViewModel = application.dataViewModel;
        if(dataViewModel==null){return;}
        dataViewModel.getMarker_list().observe(this, new Observer<List<MyMarker>>() {
            @Override
            public void onChanged(List<MyMarker> myMarkerList) {
                loge("已有 marker 数量："+ myMarkerList.size());
                MapMarkerUtil.getInstance().setMarker(myMarkerList);
//                if(movement_type==CHANGE_MARKER){
//                    for (Marker marker : aMap.getMapScreenMarkers()) {
//                        if(marker.getTitle().equals(current_addr)){
//                            marker.showInfoWindow();
//                        }
//                    }
//                }

            }
        });

        dataViewModel.getGroup_list().observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(List<Group> groups) {
                if(!first_time){return;}
                if(groups.size()>0){
                    Group list = groups.get(0);
                    if(list.terminals.size()>0){
                        Terminal terminal = list.terminals.get(0);
                        String addr = terminal.addr;
                        NotificationCenter.standard().postNotification(Constant.CHANGE_MARKER,addr);
                        first_time = false;
                    }
                }

            }
        });
        dataViewModel.getTerminal_detail().observe(this, new Observer<TerminalDetail>() {
            @Override
            public void onChanged(TerminalDetail terminalDetail) {
                try {
                    TerminalDetail.TerminalInfo info = terminalDetail.terminalInfo;
                    Log.e(TAG, "拿到终端信息："+info.addr);
                    // 从设备列表中点击返回来的  设备  有可能没有 位置信息，做个判断过滤掉
                    if(info.status.equals(MapMarkerUtil.NOT)){
                        MainApplication.getInstance().showToast("当前设备未连接",0);
                        return;
                    }
                    // 正常的设备
                    // 从数据对象中取出参数设置到文本控件
                    String terminal_name;
                    if(info.addr==null){terminal_name="";}else {terminal_name=info.addr;}
                    String terminal_remark;
                    if(info.remark==null){terminal_remark="";}else {terminal_remark=info.remark;}
                    String terminal_loctype;
                    if(info.loc==null){terminal_loctype="";}else {terminal_loctype=info.loc.locType;}
                    String terminal_status;
                    if(info.status==null){terminal_status="";}else {terminal_status=info.status;}
                    String terminal_loctime;
                    if(info.loc==null){terminal_loctime="";}else {terminal_loctime=info.loc.time;}
                    String terminal_type;
                    if(info.type==null){terminal_type="";}else {terminal_type=info.type;}

                    double lng;  // 经度
                    double lat;  // 纬度
                    double alt;  // 高度
                    double speed;  // 速度
                    double dir;  // 方向
                    String terminal_locstatus;  // 定位状态
                    if(info.loc==null){
                        lng = 0.0;  // 经度
                        lat = 0.0;  // 纬度
                        alt = 0.0;  // 高度
                        speed = 0.0;  // 速度
                        dir = 0.0;  // 方向
                        terminal_locstatus = "无";
                    }else {
                        lng = info.loc.lng;  // 经度
                        lat = info.loc.lat;  // 纬度
                        alt = info.loc.alt;  // 高度
                        speed = info.loc.speed;  // 速度
                        dir = info.loc.dir;  // 方向
                        terminal_locstatus = info.loc.locStatus;
                    }

                    long terminal_commtime = info.lastCommTime;  // 通信时间

                    // 名称
                    if (terminal_remark == null||terminal_remark.equals("")) {viewBinding.remark.setText(terminal_name);} else {viewBinding.remark.setText(terminal_remark + " (" + terminal_name + ")");}
                    // 根据设备状态设置设备信息的文本
                    if (terminal_status.equals(MapMarkerUtil.ON)) {
                        viewBinding.terminalDetailStatus.setText("在线");
                        viewBinding.terminalDetailStatus.setTextColor(Color.GREEN);
                    } else if (terminal_status.equals(MapMarkerUtil.OFF)) {
                        viewBinding.terminalDetailStatus.setText("离线");
                        viewBinding.terminalDetailStatus.setTextColor(Color.GRAY);
                    } else if (terminal_status.equals(MapMarkerUtil.SOS)) {
                        viewBinding.terminalDetailStatus.setText("紧急救援");
                        viewBinding.terminalDetailStatus.setTextColor(Color.RED);
                    } else if (terminal_status.equals(MapMarkerUtil.NOT)) {
                        viewBinding.terminalDetailStatus.setText("未连接");
                        viewBinding.terminalDetailStatus.setTextColor(Color.DKGRAY);
                    }else {viewBinding.terminalDetailStatus.setText("未知");}
                    // 定位类型
                    String terminal_loctype_v = MapMarkerUtil.locTypeMap.get(terminal_loctype);
                    if(terminal_loctype_v != null){viewBinding.terminalDetailLoctype.setText(terminal_loctype_v);}
                    else {viewBinding.terminalDetailLoctype.setText(terminal_loctype);}
                    // 定位时间
                    viewBinding.terminalDetailLoctime.setText(terminal_loctime);
                    // 设备类型
                    String deviceTypeDescription = MapMarkerUtil.deviceTypeMap.get(terminal_type);
                    if (deviceTypeDescription != null) {viewBinding.terminalDetailType.setText(deviceTypeDescription);}
                    else {viewBinding.terminalDetailType.setText("未知设备类型");}
                    // 定位信息
                    viewBinding.terminalDetailLng.setText(TaoUtil.changeToDFM(lng));
                    viewBinding.terminalDetailLat.setText(TaoUtil.changeToDFM(lat));
                    viewBinding.terminalDetailAlt.setText("" + alt);
                    // 参数列表：动态添加
                    viewBinding.terminalInfoList.removeAllViews();  // 清空
                    // 设备信息：速度
                    LinearLayout.LayoutParams speed_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout speed_info_line = new LinearLayout(getActivity());
                    speed_info_line.setLayoutParams(speed_params);
                    speed_info_line.setOrientation(LinearLayout.HORIZONTAL);
                    speed_info_line.setPadding(0, 0, 0, 0);
                    // 设备信息：名字文本
                    TextView speed_info_name = new TextView(getActivity());
                    speed_info_name.setTextSize(info_text_size);
                    speed_info_name.setText("速度：");
                    speed_info_name.setTextColor(Color.WHITE);
                    speed_info_line.addView(speed_info_name);
                    // 设备信息：信息文本
                    TextView speed_info_value = new TextView(getActivity());
                    speed_info_value.setTextSize(info_text_size);
                    speed_info_value.setText(speed+" km/h");
                    speed_info_value.setTextColor(Color.WHITE);
                    speed_info_line.addView(speed_info_value);
                    viewBinding.terminalInfoList.addView(speed_info_line);
                    // 设备信息：方向
                    LinearLayout.LayoutParams dir_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout dir_info_line = new LinearLayout(getActivity());
                    dir_info_line.setLayoutParams(dir_params);
                    dir_info_line.setOrientation(LinearLayout.HORIZONTAL);
                    dir_info_line.setPadding(0, info_pedding_size, 0, 0);
                    // 设备信息：名字文本
                    TextView dir_info_name = new TextView(getActivity());
                    dir_info_name.setTextSize(info_text_size);
                    dir_info_name.setText("方向：");
                    dir_info_name.setTextColor(Color.WHITE);
                    dir_info_line.addView(dir_info_name);
                    // 设备信息：信息文本
                    TextView dir_info_value = new TextView(getActivity());
                    dir_info_value.setTextSize(info_text_size);
                    if(dir!=-1){dir_info_value.setText("" + dir);}else {dir_info_value.setText("暂无");}
                    dir_info_value.setTextColor(Color.WHITE);
                    dir_info_line.addView(dir_info_value);
                    viewBinding.terminalInfoList.addView(dir_info_line);
                    // 设备信息：通信时间
                    LinearLayout.LayoutParams cimmtime_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout cimmtime_info_line = new LinearLayout(getActivity());
                    cimmtime_info_line.setLayoutParams(cimmtime_params);
                    cimmtime_info_line.setOrientation(LinearLayout.HORIZONTAL);
                    cimmtime_info_line.setPadding(0, info_pedding_size, 0, 0);
                    // 设备信息：名字文本
                    TextView cimmtime_info_name = new TextView(getActivity());
                    cimmtime_info_name.setTextSize(info_text_size);
                    cimmtime_info_name.setText("通信时间：");
                    cimmtime_info_name.setTextColor(Color.WHITE);
                    cimmtime_info_line.addView(cimmtime_info_name);
                    // 设备信息：信息文本
                    TextView cimmtime_info_value = new TextView(getActivity());
                    cimmtime_info_value.setTextSize(info_text_size);
                    cimmtime_info_value.setText(DataUtil.stamp2Date(terminal_commtime));
                    cimmtime_info_value.setTextColor(Color.WHITE);
                    cimmtime_info_line.addView(cimmtime_info_value);
                    viewBinding.terminalInfoList.addView(cimmtime_info_line);
                    // 设备信息：定位状态
                    LinearLayout.LayoutParams locstatus_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout locstatus_info_line = new LinearLayout(getActivity());
                    locstatus_info_line.setLayoutParams(locstatus_params);
                    locstatus_info_line.setOrientation(LinearLayout.HORIZONTAL);
                    locstatus_info_line.setPadding(0, info_pedding_size, 0, 0);
                    // 定位状态：名字文本
                    TextView locstatus_info_name = new TextView(getActivity());
                    locstatus_info_name.setTextSize(info_text_size);
                    locstatus_info_name.setText("定位状态：");
                    locstatus_info_name.setTextColor(Color.WHITE);
                    locstatus_info_line.addView(locstatus_info_name);
                    // 定位状态：信息文本
                    TextView locstatus_info_value = new TextView(getActivity());
                    locstatus_info_value.setTextSize(info_text_size);
                    String locStatus_v = MapMarkerUtil.locStatusMap.get(terminal_locstatus);
                    if (locStatus_v != null) {locstatus_info_value.setText(locStatus_v);}
                    else {locstatus_info_value.setText(terminal_locstatus);}
                    locstatus_info_value.setTextColor(Color.WHITE);
                    locstatus_info_line.addView(locstatus_info_value);
                    viewBinding.terminalInfoList.addView(locstatus_info_line);
                    // 如果还有其他信息的话动态添加进设备信息栏里面
                    List<TerminalDetail.TerminalInfo.Info> other_infos = info.infos;
                    if(other_infos!=null && other_infos.size()>0){
                        for (TerminalDetail.TerminalInfo.Info other_info : other_infos) {
                            // 设备信息：背景栏
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            LinearLayout info_line = new LinearLayout(getActivity());
                            info_line.setLayoutParams(params);
                            info_line.setOrientation(LinearLayout.HORIZONTAL);
                            info_line.setPadding(0, info_pedding_size, 0, 0);
                            // 设备信息：名字文本
                            TextView info_name = new TextView(getActivity());
                            info_name.setTextSize(info_text_size);
                            info_name.setText(other_info.name + "：");
                            info_name.setTextColor(Color.WHITE);
                            info_line.addView(info_name);
                            // 设备信息：信息文本
                            TextView info_value = new TextView(getActivity());
                            info_value.setTextSize(info_text_size);
                            info_value.setText(other_info.value);
                            info_value.setTextColor(Color.WHITE);
                            info_line.addView(info_value);
                            viewBinding.terminalInfoList.addView(info_line);
                        }
                    }
                    // 如果还有自定义信息的话动态添加进设备信息栏里面
                    List<TerminalDetail.TerminalInfo.Field> fields = info.fields;
                    if (fields!=null && fields.size()>0) {
                        for (TerminalDetail.TerminalInfo.Field field : fields) {
                            // 设备信息：背景栏
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            LinearLayout info_line = new LinearLayout(getActivity());
                            info_line.setLayoutParams(params);
                            info_line.setOrientation(LinearLayout.HORIZONTAL);
                            info_line.setPadding(0, info_pedding_size, 0, 0);
                            // 设备信息：名字文本
                            TextView info_name = new TextView(getActivity());
                            info_name.setTextSize(info_text_size);
                            info_name.setText(field.name + "：");
                            info_name.setTextColor(Color.WHITE);
                            info_line.addView(info_name);
                            // 设备信息：信息文本
                            TextView info_value = new TextView(getActivity());
                            info_value.setTextSize(info_text_size);
                            info_value.setText(field.value);
                            info_value.setTextColor(Color.WHITE);
                            info_line.addView(info_value);
                            viewBinding.terminalInfoList.addView(info_line);
                        }
                    }

                    // 是否需要改变视角
                    if(movement_type==CHANGE_MARKER){
                        if(lng==0.0){
                            application.showToast("当前设备无定位信息",0);
                        }else {
                            moveCamera(0,lat,lng,aMap.getMaxZoomLevel()-2);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        // 改变marker
        if(id == Constant.CHANGE_MARKER){
            if(args==null){return;}
            String addr = (String) args[0];
            current_addr = addr;
            RequestUtil.getInstance().getTerminalDetail(addr);  // 获取这个marker的信息
            if(viewBinding.terminalDetail.getVisibility()==View.GONE){
                animateView(viewBinding.terminalDetail);
            }
            movement_type = CHANGE_MARKER;
        }
        // 收到 sos 或报平安
        else if(id == Constant.RECEIVE_OK_SOS){
            if(args==null){return;}
            String addr = (String) args[0];
            current_addr = addr;
            RequestUtil.getInstance().getTerminalDetail(addr);  // 获取这个marker的信息
            if(viewBinding.terminalDetail.getVisibility()==View.GONE){
                animateView(viewBinding.terminalDetail);
            }
            movement_type = CHANGE_MARKER;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NotificationCenter.standard().removeObserver(this);  // 释放资源
        viewBinding.map.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        viewBinding.map.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        viewBinding.map.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        viewBinding.map.onSaveInstanceState(outState);
    }


}
