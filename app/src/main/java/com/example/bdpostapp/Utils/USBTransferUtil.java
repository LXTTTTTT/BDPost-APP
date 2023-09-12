package com.example.bdpostapp.Utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.bdpostapp.Base.MainApplication;
import com.example.bdpostapp.BuildConfig;
import com.example.bdpostapp.Global.Variable;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// usb 数据传输工具
public class USBTransferUtil {

    private String TAG = "USBTransferUtil";
    private MainApplication APP = MainApplication.getInstance();  // 主程序，替换为你自己的
    private UsbManager manager = (UsbManager) APP.getSystemService(Context.USB_SERVICE);  // usb管理器

    private BroadcastReceiver usbReceiver;  // 广播监听：判断usb设备授权操作
    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".INTENT_ACTION_GRANT_USB";  // usb权限请求标识
    private final String IDENTIFICATION = " USB-Serial Controller D";  // 目标设备标识

// 顺序： manager - availableDrivers（所有可用设备） - UsbSerialDriver（目标设备对象） - UsbDeviceConnection（设备连接对象） - UsbSerialPort（设备的端口，一般只有1个）

    private List<UsbSerialDriver> availableDrivers = new ArrayList<>();  // 所有可用设备
    private UsbSerialDriver usbSerialDriver;  // 当前连接的设备
    private UsbDeviceConnection usbDeviceConnection;  // 连接对象
    private UsbSerialPort usbSerialPort;  // 设备端口对象，通过这个读写数据
    private SerialInputOutputManager inputOutputManager;  // 数据输入输出流管理器

// 连接参数，按需求自行修改 ---------------------
    private int baudRate = 115200;  // 波特率
    private int dataBits = 8;  // 数据位
    private int stopBits = UsbSerialPort.STOPBITS_1;  // 停止位
    private int parity = UsbSerialPort.PARITY_NONE;// 奇偶校验

// 单例 -------------------------
    private static USBTransferUtil usbTransferUtil;
    public static USBTransferUtil getInstance() {
        if(usbTransferUtil == null){
            usbTransferUtil = new USBTransferUtil();
        }
        return usbTransferUtil;
    }


    public void connect(){
        // “Variable.isConnectUSB” 我的变量标识，自行删除或修改
        if(!Variable.isConnectUSB){
            registerReceiver();  // 注册广播监听
            refreshDevice();  // 拿到已连接的usb设备列表
            connectDevice();  // 建立连接
        }
    }

    public void openORclose(){
        // 没连接的话
        if(!Variable.isConnectUSB){
            registerReceiver();  // 注册广播监听
            refreshDevice();  // 拿到已连接的usb设备列表
            connectDevice();  // 建立连接
        }else {
            disconnect();  // 断开连接
        }
    }

    // 注册usb授权监听广播
    public void registerReceiver(){
        usbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
                    // 授权操作完成，连接
//                    boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);  // 不知为何获取到的永远都是 false 因此无法判断授权还是拒绝
                    connectDevice();
                }
            }
        };
        APP.registerReceiver(usbReceiver,new IntentFilter(INTENT_ACTION_GRANT_USB));
    }

    // 刷新当前可用 usb设备
    public void refreshDevice(){
        availableDrivers.clear();
        availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        Log.e(TAG, "当前可用 usb 设备数量: " + availableDrivers.size() );

        // 调试，先打开
        if(Variable.DebugMode){
            APP.showToast("当前可用 usb 设备数量: " + availableDrivers.size(),0);
        }

        // 有设备可以连接
        if(availableDrivers.size() != 0){

            // 当时开发用的是定制平板电脑有 2 个usb口，所以搜索到两个
            if(availableDrivers.size()>1){
                for (int i = 0; i < availableDrivers.size(); i++) {
                    UsbSerialDriver availableDriver = availableDrivers.get(i);
                    // 我是通过 ProductName 这个参数来识别我要连接的设备
                    if(availableDriver.getDevice().getProductName().equals(IDENTIFICATION)){
                        usbSerialDriver = availableDriver;
                    }
                }
            }
            // 通常手机只有充电口 1 个
            else {
                usbSerialDriver = availableDrivers.get(0);
            }
            usbSerialPort = usbSerialDriver.getPorts().get(0);  // 一般设备的端口都只有一个，具体要参考设备的说明文档
            // 同时申请设备权限
            if(!manager.hasPermission(usbSerialDriver.getDevice())){
                int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(APP, 0, new Intent(INTENT_ACTION_GRANT_USB), flags);
                manager.requestPermission(usbSerialDriver.getDevice(), usbPermissionIntent);
            }
        }
        // 没有设备
        else {
            APP.showToast("请先接入设备",0);
        }


    }

    // 连接设备
    public void connectDevice(){
        if(usbSerialDriver == null || inputOutputManager != null){return;}
        // 判断是否拥有权限
        boolean hasPermission = manager.hasPermission(usbSerialDriver.getDevice());
        if(hasPermission){
            usbDeviceConnection = manager.openDevice(usbSerialDriver.getDevice());  // 拿到连接对象
            if(usbSerialPort == null){return;}
            try {
                usbSerialPort.open(usbDeviceConnection);  // 打开串口
                usbSerialPort.setParameters(baudRate, dataBits, stopBits, parity);  // 设置串口参数：波特率 - 115200 ， 数据位 - 8 ， 停止位 - 1 ， 奇偶校验 - 无
                startReceiveData();  // 开启读数据线程
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            APP.showToast("请先授予权限再连接",0);
        }

    }

    // 15 0000000000 01 01 00EA320680C3C9010002 1599D564

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private byte[] readBuffer = new byte[1024 * 2];  // 缓冲区
    StringBuffer buffer_hex = new StringBuffer("");
    // 开启数据接收监听
    public void startReceiveData(){
        if(usbSerialPort == null || !usbSerialPort.isOpen()){return;}
        inputOutputManager = new SerialInputOutputManager(usbSerialPort, new SerialInputOutputManager.Listener() {
            @Override
            public void onNewData(byte[] data) {
//                String data_hex = DataUtil.bytes2Hex(data);
//                Log.i(TAG, "收到 usb 数据: " + data_hex);
//                buffer_hex.append(data_hex);
//                if(buffer_hex.length()>4 && (buffer_hex.substring(buffer_hex.length() - 4)).equals("0d0a")){
//                    String data_str = DataUtil.hex2String(buffer_hex.toString());
//                    ProtocolUtil.parseData(data_str);
//                    buffer_hex.setLength(0);
//                }


                baos.write(data,0,data.length);
                readBuffer = baos.toByteArray();
                // 根据需求设置停止位：由于我需要接收的是北斗指令，指令格式最后两位为 “回车换行(\r\n)” 所以我只需要判断数据末尾两位
                // 设置停止位，当最后两位为 \r\n 时就传出去
                if (readBuffer.length >= 2 && readBuffer[readBuffer.length - 2] == (byte)'\r' && readBuffer[readBuffer.length - 1] == (byte)'\n') {

                    String data_str = DataUtil.bytes2string(readBuffer);
                    String data_hex = DataUtil.bytes2Hex(readBuffer);
                    Log.i(TAG, "收到 usb 数据: " + data_str);

                    String[] data_hex_array = data_hex.split("0d0a");  // 分割后处理
                    for (String s : data_hex_array) {
                        String s_str = DataUtil.hex2String(s);
                        Pattern pattern = Pattern.compile("INI|IDX|ADR|FRE|PTL|ICC|EQX|SEX|AEA|ISX|TTX|MTX|BTX|LOC|NET|CGS");
                        Matcher matcher = pattern.matcher(s_str);
                        if (matcher.find()) {

                        }
                    }
                    baos.reset();  // 重置
                }

            }
            @Override
            public void onRunError(Exception e) {
                Log.e(TAG, "usb 断开了" );
                disconnect();
                e.printStackTrace();
            }
        });
        inputOutputManager.start();
        Variable.isConnectUSB = true;  // 修改连接标识
        APP.showToast("连接成功" ,Toast.LENGTH_SHORT);
    }

    // 下发数据
    public void write(String data_hex){
        if(usbSerialPort != null){
            Log.e(TAG, "当前usb状态: isOpen-" + usbSerialPort.isOpen() );
            // 当串口打开时再下发
            if(usbSerialPort.isOpen()){
                byte[] data_bytes = DataUtil.hex2bytes(data_hex);  // 将字符数据转化为 byte[]
                if (data_bytes == null || data_bytes.length == 0) return;
                // 调试，先打开
                if(Variable.DebugMode){
                    APP.showToast("USB 下发指令: " + DataUtil.hex2String(data_hex),Toast.LENGTH_SHORT);
                }
                try {
                    usbSerialPort.write(data_bytes,100);  // 写入数据
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                APP.showToast(" usb 未连接" ,Toast.LENGTH_SHORT);
            }
        }
    }


    // 断开连接
    public void disconnect(){
        try{
            // 停止数据接收监听
            if(inputOutputManager != null){
                inputOutputManager.stop();
                inputOutputManager = null;
            }
            // 关闭端口
            if(usbSerialPort != null){
                usbSerialPort.close();
                usbSerialPort = null;
            }
            // 关闭连接
            if(usbDeviceConnection != null){
                usbDeviceConnection.close();
                usbDeviceConnection = null;
            }
            // 清除设备
            if(usbSerialDriver != null){
                usbSerialDriver = null;
            }
            // 清空设备列表
            availableDrivers.clear();
            // 注销广播监听
            APP.unregisterReceiver(usbReceiver);
            APP.dataViewModel.initData();
            Variable.isConnectUSB = false;  // 修改标识
            APP.showToast("断开连接",0);

        }catch (Exception e){
            e.printStackTrace();
        }
    }





}
