package com.graduate.design;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.LinearLayout;

import com.graduate.design.activity.LoginActivity;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;
import com.molihuan.pathselector.configs.PathSelectorConfig;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout splashButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化页面
        InitViewUtils.initView(this);
        // 初始化数据
        initData();
        // 拿到页面元素
        getComponentsById();
        // 设置监听事件
        setListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData(){
        // 开启调试模式
        PathSelectorConfig.setDebug(true);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().
                detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
    }



    private void getComponentsById(){
        splashButton = findViewById(R.id.splash_btn);
    }

    private void setListeners(){
        splashButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.splash_btn:
                gotoLogin();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void gotoLogin(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        ActivityJumpUtils.jumpActivity(MainActivity.this, intent, 100L, false);
    }
}