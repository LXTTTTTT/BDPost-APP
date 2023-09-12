package com.example.bdpostapp.View;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.bdpostapp.Base.MainApplication;
import com.example.bdpostapp.Entity.GsonBean.Receiving.TerminalDetail;
import com.example.bdpostapp.R;
import com.example.bdpostapp.Utils.DataUtil;
import com.example.bdpostapp.Utils.MapMarkerUtil;
import com.example.bdpostapp.Utils.TaoUtil;
import com.example.bdpostapp.databinding.DialogCustomBinding;

import java.util.List;


// 自定义内容 dialog
public class CustomDialog extends DialogFragment {

    private String TAG = "CustomDialog";

    DialogCustomBinding View;
    int info_text_size = 13;  // 信息文本的字体大小
    int info_pedding_size = DataUtil.dp_to_px(MainApplication.getInstance(), 5);  // 信息文本的上部填充距离

    public CustomDialog() {
    }

    public void setData(){

    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

//        View.account.setText(Variable.getAccount());

        // 设置他的宽度为 屏幕的 0.9 倍，不用的话就注释
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.corner_fill_white_1);

            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.y = 300;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }
//        setCancelable(false);

        MainApplication.getInstance().dataViewModel.getTerminal_detail().observe((LifecycleOwner) getActivity(), new Observer<TerminalDetail>() {
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
                    String terminal_name = info.addr;  // 名称：卡号
                    String terminal_remark = info.remark;  // 备注
                    String terminal_loctype = info.loc.locType;  // 定位信息
                    String terminal_status = info.status;  // 设备状态
                    String terminal_loctime = info.loc.time;  // 定位时间
                    String terminal_type = info.type;  // 设备类型
                    double lng = info.loc.lng;  // 经度
                    double lat = info.loc.lat;  // 纬度
                    double alt = info.loc.alt;  // 高度
                    double speed = info.loc.speed;  // 速度
                    double dir = info.loc.dir;  // 方向
                    long terminal_commtime = info.lastCommTime;  // 通信时间
                    String terminal_locstatus = info.loc.locStatus;  // 定位状态
                    // 名称
                    if (terminal_remark == null) {View.remark.setText(terminal_name);} else {View.remark.setText(terminal_remark + " (" + terminal_name + ")");}
                    // 根据设备状态设置设备信息的文本
                    if (terminal_status.equals(MapMarkerUtil.ON)) {
                        View.terminalDetailStatus.setText("在线");
                        View.terminalDetailStatus.setTextColor(Color.GREEN);
                    } else if (terminal_status.equals(MapMarkerUtil.OFF)) {
                        View.terminalDetailStatus.setText("离线");
                        View.terminalDetailStatus.setTextColor(Color.GRAY);
                    } else if (terminal_status.equals(MapMarkerUtil.SOS)) {
                        View.terminalDetailStatus.setText("紧急救援");
                        View.terminalDetailStatus.setTextColor(Color.RED);
                    } else if (terminal_status.equals(MapMarkerUtil.NOT)) {
                        View.terminalDetailStatus.setText("未连接");
                        View.terminalDetailStatus.setTextColor(Color.DKGRAY);
                    }else {View.terminalDetailStatus.setText("未知");}
                    // 定位类型
                    String terminal_loctype_v = MapMarkerUtil.locTypeMap.get(terminal_loctype);
                    if(terminal_loctype_v != null){View.terminalDetailLoctype.setText(terminal_loctype_v);}
                    else {View.terminalDetailLoctype.setText(terminal_loctype);}
                    // 定位时间
                    View.terminalDetailLoctime.setText(terminal_loctime);
                    // 设备类型
                    String deviceTypeDescription = MapMarkerUtil.deviceTypeMap.get(terminal_type);
                    if (deviceTypeDescription != null) {View.terminalDetailType.setText(deviceTypeDescription);}
                    else {View.terminalDetailType.setText("未知设备类型");}
                    // 定位信息
                    View.terminalDetailLng.setText(TaoUtil.changeToDFM(lng));
                    View.terminalDetailLat.setText(TaoUtil.changeToDFM(lat));
                    View.terminalDetailAlt.setText("" + alt);
                    // 参数列表：动态添加
                    View.terminalInfoList.removeAllViews();  // 清空
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
                    View.terminalInfoList.addView(speed_info_line);
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
                    View.terminalInfoList.addView(dir_info_line);
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
                    View.terminalInfoList.addView(cimmtime_info_line);
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
                    View.terminalInfoList.addView(locstatus_info_line);
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
                            View.terminalInfoList.addView(info_line);
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
                            View.terminalInfoList.addView(info_line);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 去掉dialog默认标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View = DialogCustomBinding.inflate(inflater);
        View.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                dismiss();
            }
        });
        return View.getRoot();
    }


}
