package com.example.bdpostapp

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.widget.*
import com.example.bdpostapp.Base.BaseActivity
import com.example.bdpostapp.Base.MainApplication
import com.example.bdpostapp.Global.Constant
import com.example.bdpostapp.Utils.RequestUtil
import com.example.bdpostapp.Utils.SharedPreferencesUtil
import com.example.bdpostapp.Utils.TaoUtil
import com.example.bdpostapp.databinding.ActivityLoginBinding


class LoginActivity : BaseActivity() {

    lateinit var viewBinding:ActivityLoginBinding
    // 双击退出程序
    private var backPressedTime: Long = 0
    private val backPressInterval: Long = 2000 // 2 seconds

    override fun setLayout(): Any {
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun beforeSetLayout() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        init_control()
    }

    override fun initData() {
        //设置最后一次登录的账号
        val lastAccount: String? = SharedPreferencesUtil.getInstance().getString(Constant.LAST_LOGIN_ACCOUNT,"")
        if (!TextUtils.isEmpty(lastAccount)) {
//            phoneEt.setText(lastAccount)
            viewBinding.accountET.setText(lastAccount)
        }
        val lastPassword = SharedPreferencesUtil.getInstance().getString(Constant.LAST_LOGIN_PASSWORD,"")
        viewBinding.accountPwdEt.setText(lastPassword)
//        loginTitle.text="欢迎登录"+getString(R.string.app_name)
    }

    override fun getTAG(): String {
        return "LoginActivity"
    }

    fun init_control(){

        // 登录
        viewBinding.accountLogin.setOnClickListener {

            val account=viewBinding.accountET.text.toString()
            if(account==""){
                MainApplication.getInstance().showToast("请输入账号!",0)
                return@setOnClickListener
            }
            val pwd=viewBinding.accountPwdEt.text.toString()
            if(pwd==""){
                MainApplication.getInstance().showToast("请输入密码!",0)
                return@setOnClickListener
            }
            SharedPreferencesUtil.getInstance().setString(Constant.LAST_LOGIN_ACCOUNT, account)
            SharedPreferencesUtil.getInstance().setString(Constant.LAST_LOGIN_PASSWORD, pwd)
//            RequestUtil.getInstance().login(account,pwd,this);  // 登录账号
            if(TaoUtil.isHaveNetwork()){
//                RequestUtil.getInstance().login("test_lxt","123456",this);  // 登录账号
//                RequestUtil.getInstance().login("admin","admin@bd3!!",this);  // 登录账号
//                RequestUtil.getInstance().login("13800138000","123abc!!",this);
//                RequestUtil.getInstance().login("maidong590","123456",this);  // 登录账号
                RequestUtil.getInstance().login(account,pwd,this);  // 登录账号

            }else{
                MainApplication.getInstance().showToast("当前无网络连接，请检查网络状态!",0)
            }
        }

        // 隐藏密码
        viewBinding.pwdHideImg.setOnClickListener {
            if(viewBinding.accountPwdEt.isSelected){
                viewBinding.accountPwdEt.isSelected=false
                viewBinding.accountPwdEt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                viewBinding.pwdHideImg.setImageDrawable(getDrawable(R.mipmap.hide_pwd))
            }else{
                viewBinding.accountPwdEt.isSelected=true
                viewBinding.pwdHideImg.setImageDrawable(getDrawable(R.mipmap.pwd_show))
                viewBinding.accountPwdEt.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
        }

    }


    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < backPressInterval) {
            super.onBackPressed()
        } else {
            application.showToast("再次点击返回按键退出程序",0)
            backPressedTime = currentTime
        }
    }




}