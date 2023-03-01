package com.graduate.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.graduate.design.R;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ToastUtils;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameInRegister;
    private EditText passwordInRegister;
    private EditText emailInRegister;
    private Button registerBtnInRegister;

    private UserService userService = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInRegister = findViewById(R.id.usernameInRegister);
        passwordInRegister = findViewById(R.id.passwordInRegister);
        emailInRegister = findViewById(R.id.emailInRegister);
        registerBtnInRegister = findViewById(R.id.registerBtnInRegister);

        // 点击注册按钮
        registerBtnInRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameInRegister.getText().toString().trim();
                String password = passwordInRegister.getText().toString().trim();
                String email = emailInRegister.getText().toString().trim();

                int res = userService.register(username, password, email);

                // 请求失败
                if(res==1){
                    ToastUtils.showShortToastCenter("用户名已存在!");
                    return;
                }

                // 请求成功，跳转到登录页面
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
