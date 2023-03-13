package com.graduate.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.graduate.design.R;
import com.graduate.design.proto.UserLogin;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backButton;
    private ImageButton backImageButton;
    private Button logoutButton;
    private String token;
    private UserLogin.UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化页面
        InitViewUtils.initView(this);
        // 初始化数据
        initData();
        // 拿到页面元素
        getComponentsById();
        // 设置监听事件
        setListeners();
    }

    private void initData(){
        token = GraduateDesignApplication.getToken();
        userInfo = GraduateDesignApplication.getUserInfo();
    }

    private void getComponentsById(){
        backButton = findViewById(R.id.back_btn_disk);
        backImageButton = findViewById(R.id.back_image_btn_disk);
        logoutButton = findViewById(R.id.logout);
    }

    private void setListeners(){
        backButton.setOnClickListener(this);
        backImageButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
    }

    // 为按钮元素设置对应的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn_disk:
            case R.id.back_image_btn_disk:
                goBackToMine();
                break;
            case R.id.logout:
                logout();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    // 返回到我的页面
    private void goBackToMine(){
        finish();
    }

    private void logout(){
        // 登出
        token = null;
        userInfo = null;
        // 跳转到登录页面
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        ActivityJumpUtils.jumpActivity(SettingsActivity.this, intent, 100L, true);
    }
}
