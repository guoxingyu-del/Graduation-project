package com.graduate.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.protobuf.ByteString;
import com.graduate.design.R;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;

public class FileContentActivity extends AppCompatActivity {
    private UserService userService;
    private ImageButton backImageButton;
    private Button backButton;
    private TextView fileTitleView;
    private TextView fileContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_content);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_color));

        userService = new UserServiceImpl();

        // 从disk页面拿到点击的文件名称和内容
        String fileName = getIntent().getStringExtra("fileName");
        String fileContent = getIntent().getStringExtra("fileContent");

        fileTitleView = findViewById(R.id.file_title);
        fileContentView = findViewById(R.id.file_content);

        fileTitleView.setText(fileName);
        fileContentView.setText(fileContent);


        backImageButton = findViewById(R.id.back_image_btn);
        backButton = findViewById(R.id.back_btn);

        // 点击返回按钮，返回文件页面，这里把按钮分成了文字和图标按钮
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
