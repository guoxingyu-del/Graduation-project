package com.graduate.design;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import com.graduate.design.activity.LoginActivity;
import com.graduate.design.activity.RegisterActivity;
import com.graduate.design.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {
    private Button loginBtnInMain;
    private Button registerBtnInMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set Thread policy
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().
                detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        // 读写手机文件权限管理
        PermissionUtils.verifyStoragePermissions(this);

        loginBtnInMain = findViewById(R.id.loginBtnInMain);
        registerBtnInMain = findViewById(R.id.registerBtnInMain);

        // 点击按钮跳转到登录或注册页面
        loginBtnInMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        registerBtnInMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}