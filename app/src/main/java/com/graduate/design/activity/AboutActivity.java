package com.graduate.design.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.graduate.design.R;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backButton;
    private ImageButton backImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // 初始化页面
        InitViewUtils.initView(this);
        // 拿到页面元素
        getComponentsById();
        // 设置监听事件
        setListeners();
    }


    private void getComponentsById(){
        backButton = findViewById(R.id.back_btn_disk);
        backImageButton = findViewById(R.id.back_image_btn_disk);
    }

    private void setListeners(){
        backButton.setOnClickListener(this);
        backImageButton.setOnClickListener(this);
    }

    // 为按钮元素设置对应的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn_disk:
            case R.id.back_image_btn_disk:
                goBackToMine();
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
}
