package com.example.bdpostapp.Utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.example.bdpostapp.Entity.GsonBean.Receiving.MyMarker;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Normal;
import com.example.bdpostapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 地图 Marker 点工具
public class MapMarkerUtil {

    private static String TAG = "MapMarkerUtil";

    public Context my_context;
    public List<MyMarker> myMarker_list;  // 当前显示的 marker 点
    public AMap aMap;  // 需要操作的地图

    // marker 的类型标识
    public static final int TERMINAL_MARKER = 1;  // 设备位置
    public static final int GROUP_MARKER = 2;  // 组位置

    // marker 的使用范围
    public static final String PERSONNEL = "PERSONNEL";
    public static final String CAR = "CAR";
    public static final String TRUCKS = "TRUCKS";
    public static final String ANIMAL = "ANIMAL";
    public static final String STEAMER = "STEAMER";
    public static final String HELICOPTER = "HELICOPTER";
    public static final String AIRCRAFT = "AIRCRAFT";
    public static final String TRAIN = "TRAIN";
    public static final String MOTORBIKE = "MOTORBIKE";
    public static final String PAGING_TEAM = "PAGING_TEAM";
    public static final String FIRE_FIGHTING = "FIRE_FIGHTING";
    public static final String OTHER = "OTHER";

    // marker 的状态
    public static final String ON = "ON";
    public static final String SOS = "SOS";
    public static final String OFF = "OFF";
    public static final String NOT = "NOT";

    public static Map<String, Map<String, Integer>> resourceMap = new HashMap<>();  // 图标资源对应图
    public static Map<String, String> deviceTypeMap = new HashMap<>();  // 设备类型对应中文
    public static Map<String, String> locTypeMap = new HashMap<>();  // 定位类型对应中文
    public static Map<String, String> locStatusMap = new HashMap<>();  // 定位状态对应中文

// 单例 ----------------------------------------------------
    private static MapMarkerUtil mapMarkerUtil;
    public static MapMarkerUtil getInstance() {
        if(mapMarkerUtil == null){
            mapMarkerUtil = new MapMarkerUtil();
            mapMarkerUtil.init_resourceMap();
        }
        return mapMarkerUtil;
    }

    public void init(Context context,AMap aMap){
        this.my_context = context;
        this.aMap = aMap;
    }


    public void setMarker(List<MyMarker> myMarker_list){
        if(aMap==null || myMarker_list==null || myMarker_list.size()==0){return;}
        // 清除所有原有的 marker
        List<Marker> map_markers = aMap.getMapScreenMarkers();
        for (Marker map_marker : map_markers) {
//            Log.e(TAG, "marker 信息: " + map_marker.getTitle() + "/" + map_marker.getId() + "/" + map_marker.getDisplayLevel() );
            if(map_marker.getId().equals("MARKER2")){continue;}  // 这是高德的定位蓝点
            map_marker.remove();
        }
        // 添加现有的 my_marker
        for (MyMarker myMarker : myMarker_list) {
            if(myMarker.isAggrPoint){
                add_group_marker(myMarker);
            }else {
                add_terminal_marker(myMarker);
            }
        }
    }


    // 添加 组位置标记点
    public void add_group_marker(MyMarker myMarker){
        try{
            // 如果 地图层级 到了最大，那就不添加 组位置 marker ，直接改为 设备 marker
            if(aMap.getCameraPosition().zoom == 20){
                add_terminal_marker(myMarker);
            }else {
                // 生成一个 textview 设置框高大小，通过设置文字和背景的方式实现 组位置 图标
                TextView text = new TextView(my_context);
                text.setWidth(DataUtil.dp_to_px(my_context,18));
                text.setHeight(DataUtil.dp_to_px(my_context,18));
                text.setGravity(Gravity.CENTER);
                text.setTextSize(13);
                text.setText(myMarker.pointNum+"");
                // 根据 组位置点所包含的设备数量来设置不同颜色的 图标
                if(myMarker.pointNum<=10){
                    text.setBackground(my_context.getDrawable(R.drawable.oval_fill_yellow_1));
                }else {
                    text.setBackground(my_context.getDrawable(R.drawable.oval_fill_blue_3));
                }

                MarkerOptions group_marker = new MarkerOptions();
                group_marker.icon(BitmapDescriptorFactory.fromView(text)); // 通过 view 的方式生成 bitmap 并设置 marker 的图标
                group_marker.title(myMarker.addr);
                group_marker.displayLevel(GROUP_MARKER);  // 设置组位置标识（2）
                LatLng group_marker_position = new LatLng(myMarker.lat,myMarker.lng);
                group_marker.position(group_marker_position);
                Marker group_mrk = aMap.addMarker(group_marker);
//                group_mrk.setInfoWindowEnable(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 添加普通的标记点
    public void add_terminal_marker(MyMarker myMarker){
        try{
            TextView img = new TextView(my_context);
            img.setWidth(DataUtil.dp_to_px(my_context,15));
            img.setHeight(DataUtil.dp_to_px(my_context,15));
            img.setGravity(Gravity.CENTER);
            // 设置 marker 的图标
            set_icon(myMarker,img);
            MarkerOptions marker = new MarkerOptions();
            // 把这个 marker 的 title 设置为 “设备的卡号”
            marker.title(myMarker.addr);
            marker.displayLevel(TERMINAL_MARKER);  // 设置终端marker标识（1）
            LatLng latLng = new LatLng(myMarker.lat,myMarker.lng);
            marker.position(latLng);
            marker.icon(BitmapDescriptorFactory.fromView(img)); // 设置图标
            Marker terminal_mrk = aMap.addMarker(marker);
//            terminal_mrk.setInfoWindowEnable(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 根据位置点的 使用范围 和 状态 设置不同的图标
    public void  set_icon(MyMarker myMarker,TextView img) {
        try{
            String scope = myMarker.scope;
            String status = myMarker.status;
            if (resourceMap.containsKey(scope)) {
                Map<String, Integer> statusMap = resourceMap.get(scope);
                if (statusMap.containsKey(status)) {
                    img.setBackgroundResource(statusMap.get(status));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // 构建资源映射
    public void init_resourceMap(){

        Map<String, Integer> personnelResources = new HashMap<>();
        personnelResources.put(ON, R.mipmap.ren_1);
        personnelResources.put(SOS, R.mipmap.ren_2);
        personnelResources.put(OFF, R.mipmap.ren_3);
        personnelResources.put(NOT, R.mipmap.ren_4);
        resourceMap.put(PERSONNEL, personnelResources);

        Map<String, Integer> carResources = new HashMap<>();
        personnelResources.put(ON, R.mipmap.keche_1);
        personnelResources.put(SOS, R.mipmap.keche_2);
        personnelResources.put(OFF, R.mipmap.keche_3);
        personnelResources.put(NOT, R.mipmap.keche_4);
        resourceMap.put(CAR, carResources);

        Map<String, Integer> trucksResources = new HashMap<>();
        trucksResources.put(ON, R.mipmap.huoche1_1);
        trucksResources.put(SOS, R.mipmap.huoche1_2);
        trucksResources.put(OFF, R.mipmap.huoche1_3);
        trucksResources.put(NOT, R.mipmap.huoche1_4);
        resourceMap.put(TRUCKS, trucksResources);

        Map<String, Integer> animalResources = new HashMap<>();
        animalResources.put(ON, R.mipmap.dongwu_1);
        animalResources.put(SOS, R.mipmap.dongwu_2);
        animalResources.put(OFF, R.mipmap.dongwu_3);
        animalResources.put(NOT, R.mipmap.dongwu_4);
        resourceMap.put(ANIMAL, animalResources);

        Map<String, Integer> steamerResources = new HashMap<>();
        steamerResources.put(ON, R.mipmap.lunchuan_1);
        steamerResources.put(SOS, R.mipmap.lunchuan_2);
        steamerResources.put(OFF, R.mipmap.lunchuan_3);
        steamerResources.put(NOT, R.mipmap.lunchuan_4);
        resourceMap.put(STEAMER, steamerResources);

        Map<String, Integer> helicopterResources = new HashMap<>();
        helicopterResources.put(ON, R.mipmap.zhishengji_1);
        helicopterResources.put(SOS, R.mipmap.zhishengji_2);
        helicopterResources.put(OFF, R.mipmap.zhishengji_3);
        helicopterResources.put(NOT, R.mipmap.zhishengji_4);
        resourceMap.put(HELICOPTER, helicopterResources);

        Map<String, Integer> aircraftResources = new HashMap<>();
        aircraftResources.put(ON, R.mipmap.minhanfeij_1);
        aircraftResources.put(SOS, R.mipmap.minhanfeiji_2);
        aircraftResources.put(OFF, R.mipmap.minhanfeij_3);
        aircraftResources.put(NOT, R.mipmap.minhanfeij_4);
        resourceMap.put(AIRCRAFT, aircraftResources);

        Map<String, Integer> trainResources = new HashMap<>();
        trainResources.put(ON, R.mipmap.huoche_1);
        trainResources.put(SOS, R.mipmap.huoche_2);
        trainResources.put(OFF, R.mipmap.huoche_3);
        trainResources.put(NOT, R.mipmap.huoche_4);
        resourceMap.put(TRAIN, trainResources);

        Map<String, Integer> motorbikeResources = new HashMap<>();
        motorbikeResources.put(ON, R.mipmap.motuoche_1);
        motorbikeResources.put(SOS, R.mipmap.motuoche_2);
        motorbikeResources.put(OFF, R.mipmap.motuoche_3);
        motorbikeResources.put(NOT, R.mipmap.motuoche_4);
        resourceMap.put(MOTORBIKE, motorbikeResources);

        Map<String, Integer> pagingteamResources = new HashMap<>();
        motorbikeResources.put(ON, R.mipmap.xunhudui_1);
        motorbikeResources.put(SOS, R.mipmap.xunhudui_2);
        motorbikeResources.put(OFF, R.mipmap.xunhudui_3);
        motorbikeResources.put(NOT, R.mipmap.xunhudui_4);
        resourceMap.put(PAGING_TEAM, pagingteamResources);

        Map<String, Integer> firefightingResources = new HashMap<>();
        motorbikeResources.put(ON, R.mipmap.xiaofang_1);
        motorbikeResources.put(SOS, R.mipmap.xiaofang_2);
        motorbikeResources.put(OFF, R.mipmap.xiaofang_3);
        motorbikeResources.put(NOT, R.mipmap.xiaofang_4);
        resourceMap.put(FIRE_FIGHTING, firefightingResources);

        Map<String, Integer> otherResources = new HashMap<>();
        motorbikeResources.put(ON, R.mipmap.other_1);
        motorbikeResources.put(SOS, R.mipmap.other_2);
        motorbikeResources.put(OFF, R.mipmap.other_3);
        motorbikeResources.put(NOT, R.mipmap.other_4);
        resourceMap.put(OTHER, otherResources);


        deviceTypeMap.put("PD22", "北三车载");
        deviceTypeMap.put("PD20", "北三手持机");
        deviceTypeMap.put("PD18", "北三盒子");
        deviceTypeMap.put("PN06", "4G太阳能报位设备");
        deviceTypeMap.put("PN07", "4G太阳能报位设备(二代)");
        deviceTypeMap.put("T808", "808定位终端");
        deviceTypeMap.put("OTHER", "北斗设备");
    }

    public void init_device_map(List<Normal> types){
        deviceTypeMap.clear();
        for (Normal type : types) {
            deviceTypeMap.put(type.name,type.value);
        }
    }

    public void init_loc_map(List<Normal> types){
        locTypeMap.clear();
        for (Normal type : types) {
            locTypeMap.put(type.name,type.value);
        }
    }

    public void init_status_map(List<Normal> status){
        locStatusMap.clear();
        for (Normal type : status) {
            locStatusMap.put(type.name,type.value);
        }
    }

}
