package com.example.bdpostapp.Base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment :Fragment() {

    private lateinit var TAG:String
    lateinit var application:MainApplication;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)  // 开启菜单项
        if (getTAG() != null) { TAG = getTAG() } else { TAG = "unknow_fragment" }  // 设置 TAG
        beforeSetLayout()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        application = MainApplication.getInstance()
        var view:View;
        view = if(setLayout() is Int) inflater.inflate((setLayout() as Int),container,false) else (setLayout() as View)  // 使用 R.layout 或 ViewBinding
//        view = inflater.inflate(setLayout(),container,false);  // 绑定布局

        initView(savedInstanceState)  // 初始化控件事件
        initData()


        return view
    }

    abstract fun beforeSetLayout()
    abstract fun setLayout():Any
    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun initData()
    abstract fun getTAG():String

    public fun loge(log:String){
        Log.e(TAG, log )
    }

}