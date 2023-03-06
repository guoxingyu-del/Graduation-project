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

import com.graduate.design.R;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.ToastUtils;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameInRegister;
    private EditText passwordInRegister;
    private EditText emailInRegister;
    private Button registerBtnInRegister;
    private Button gotoLoginBtn;

    private UserService userService = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statusbar_color));

        usernameInRegister = findViewById(R.id.username_register);
        passwordInRegister = findViewById(R.id.password_register);
        emailInRegister = findViewById(R.id.email_register);
        registerBtnInRegister = findViewById(R.id.register_btn);
        gotoLoginBtn = findViewById(R.id.goto_login_btn);


        // 点击注册按钮
        registerBtnInRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameInRegister.getText().toString().trim();
                String password = passwordInRegister.getText().toString().trim();
                String email = emailInRegister.getText().toString().trim();

                int res = userService.register(username, password, email);

                // 判断注册信息是否合法
                // 用户名只能包含字母、数字、下划线、连字符，且长度为7到14个字符
                String usernameRegex = "^[a-zA-Z0-9_]{7,14}$";
                if(!Pattern.matches(usernameRegex, username)){
                    ToastUtils.showShortToastCenter("用户名只能包含字母、数字、下划线，长度为7~14");
                    usernameInRegister.setText("");
                    passwordInRegister.setText("");
                    emailInRegister.setText("");
                    return;
                }

                // 验证邮箱是否合法
                String emailRegex = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
                if(!Pattern.matches(emailRegex, email)){
                    ToastUtils.showShortToastCenter("邮箱格式错误");
                    usernameInRegister.setText("");
                    passwordInRegister.setText("");
                    emailInRegister.setText("");
                    return;
                }

                // 请求失败
                if(res==1){
                    ToastUtils.showShortToastCenter("用户名已存在!");
                    return;
                }

                // 请求成功，跳转到登录页面
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                ActivityJumpUtils.jumpActivity(RegisterActivity.this, intent, 100L, true);
            }
        });

        gotoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到登录页面
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                ActivityJumpUtils.jumpActivity(RegisterActivity.this, intent, 100L, true);
            }
        });
    }
}
