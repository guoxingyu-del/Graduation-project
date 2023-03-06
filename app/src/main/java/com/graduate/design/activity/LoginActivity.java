package com.graduate.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.apache.commons.lang.StringUtils;

import com.graduate.design.R;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.ToastUtils;


public class LoginActivity extends AppCompatActivity {

    private EditText usernameInLogin;
    private EditText passwordInLogin;
    private Button loginBtnInLogin;
    private Button gotoRegisterBtn;

    private UserService userService = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statusbar_color));

        usernameInLogin = findViewById(R.id.username_login);
        passwordInLogin = findViewById(R.id.password_login);
        loginBtnInLogin = findViewById(R.id.login_btn);
        gotoRegisterBtn = findViewById(R.id.goto_register_btn);

        // 点击登录按钮
        loginBtnInLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameInLogin.getText().toString().trim();
                String password = passwordInLogin.getText().toString().trim();

                if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
                    ToastUtils.showShortToastCenter("用户名密码不能为空!");
                    usernameInLogin.setText("");
                    passwordInLogin.setText("");
                    return;
                }

                // 进行登录验证
                int res = userService.login(username, password);

                // 登录失败
                if(res==1){
                    ToastUtils.showShortToastCenter("用户名密码错误!");
                    usernameInLogin.setText("");
                    passwordInLogin.setText("");
                    return;
                }

                // 登录成功
                Intent intent = new Intent(LoginActivity.this, DiskActivity.class);
                ActivityJumpUtils.jumpActivity(LoginActivity.this, intent, 100L, false);
            }
        });

        gotoRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到注册页面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                ActivityJumpUtils.jumpActivity(LoginActivity.this, intent, 100L, false);
            }
        });
    }
}
