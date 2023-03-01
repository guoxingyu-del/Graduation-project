package com.graduate.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang.StringUtils;

import com.graduate.design.R;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ToastUtils;


public class LoginActivity extends AppCompatActivity {

    private EditText usernameInLogin;
    private EditText passwordInLogin;
    private Button loginBtnInLogin;

    private UserService userService = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInLogin = findViewById(R.id.usernameInLogin);
        passwordInLogin = findViewById(R.id.passwordInLogin);
        loginBtnInLogin = findViewById(R.id.loginBtnInLogin);

        // 点击登录按钮
        loginBtnInLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameInLogin.getText().toString().trim();
                String password = passwordInLogin.getText().toString().trim();

                if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
                    ToastUtils.showShortToastCenter("用户名密码不能为空!");
                    return;
                }

                // 进行登录验证
                int res = userService.login(username, password);

                // 登录失败
                if(res==1){
                    ToastUtils.showShortToastCenter("用户名密码错误!");
                    return;
                }

                // 登录成功
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
