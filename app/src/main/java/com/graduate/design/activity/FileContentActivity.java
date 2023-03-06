package com.graduate.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.graduate.design.R;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;

public class FileContentActivity extends AppCompatActivity {
    private UserService userService;
    private ImageButton backImageButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_content);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_color));

        userService = new UserServiceImpl();

        // 如何拿到点开文件的id，activity之间如何通信
        // userService.getNode().getContent();

        backImageButton = findViewById(R.id.back_image_btn);
        backButton = findViewById(R.id.back_btn);

        // 点击返回按钮，返回文件页面，这里把按钮分成了文字和图标按钮
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileContentActivity.this, DiskActivity.class);
                ActivityJumpUtils.jumpActivity(FileContentActivity.this, intent, 100L, true);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileContentActivity.this, DiskActivity.class);
                ActivityJumpUtils.jumpActivity(FileContentActivity.this, intent, 100L, true);
            }
        });


    }
}
