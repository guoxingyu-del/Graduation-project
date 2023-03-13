package com.graduate.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.graduate.design.R;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;

public class MineActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton gotoDiskButton;
    private Button changePasswordButton;
    private Button settingsButton;
    private Button aboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        // 初始化页面元素
        InitViewUtils.initView(this);
        // 获取页面元素
        getComponentsById();
        // 为按钮元素设置点击事件
        setListeners();
    }

    private void getComponentsById(){
        gotoDiskButton = findViewById(R.id.goto_disk_btn);
        changePasswordButton = findViewById(R.id.change_password_btn);
        settingsButton = findViewById(R.id.settings_btn);
        aboutButton = findViewById(R.id.about_btn);
    }

    private void setListeners(){
        gotoDiskButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goto_disk_btn:
                gotoDisk();
                break;
            case R.id.change_password_btn:
                gotoChangePassword();
                break;
            case R.id.settings_btn:
                gotoSettings();
                break;
            case R.id.about_btn:
                gotoAbout();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void gotoDisk(){
        finish();
    }

    private void gotoChangePassword(){
        Intent intent = new Intent(MineActivity.this, ChangePasswordActivity.class);
        ActivityJumpUtils.jumpActivity(MineActivity.this, intent, 100L, false);
    }

    private void gotoSettings(){
        Intent intent = new Intent(MineActivity.this, SettingsActivity.class);
        ActivityJumpUtils.jumpActivity(MineActivity.this, intent, 100L, false);
    }

    private void gotoAbout(){
        Intent intent = new Intent(MineActivity.this, AboutActivity.class);
        ActivityJumpUtils.jumpActivity(MineActivity.this, intent, 100L, false);
    }
}
