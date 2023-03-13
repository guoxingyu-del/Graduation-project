package com.graduate.design.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.graduate.design.R;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;

public class FileContentActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backImageButton;
    private Button backButton;
    private TextView fileTitleView;
    private TextView fileContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_content);

        // 初始化页面
        InitViewUtils.initView(this);
        // 拿到页面元素
        getComponentsById();
        // 设置监听事件
        setListeners();
        // 设置文件名称和文件内容
        setFileNameAndContent();
    }

    private void getComponentsById(){
        fileTitleView = findViewById(R.id.file_title);
        fileContentView = findViewById(R.id.file_content);
        backImageButton = findViewById(R.id.back_image_btn);
        backButton = findViewById(R.id.back_btn);
    }

    private void setListeners(){
        backImageButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    private void setFileNameAndContent(){
        // 从disk页面拿到点击的文件名称和内容
        String fileName = getIntent().getStringExtra("fileName");
        String fileContent = getIntent().getStringExtra("fileContent");

        fileTitleView.setText(fileName);
        fileContentView.setText(fileContent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
            case R.id.back_image_btn:
                goBackDisk();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void goBackDisk(){
        finish();
    }
}
