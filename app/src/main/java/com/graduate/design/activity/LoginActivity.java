package com.graduate.design.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


import org.apache.commons.lang.StringUtils;

import com.graduate.design.R;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.EncryptionServiceImpl;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;

import java.util.Base64;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameInLogin;
    private EditText passwordInLogin;
    private Button loginBtnInLogin;
    private Button gotoRegisterBtn;
    private UserService userService;
    private EncryptionService encryptionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化页面
        InitViewUtils.initView(this);
        // 初始化数据
        initData();
        // 拿到页面元素
        getComponentsById();
        // 设置监听事件
        setListeners();
    }

    private void initData(){
        userService = new UserServiceImpl();
        encryptionService = new EncryptionServiceImpl();
    }

    private void getComponentsById(){
        usernameInLogin = findViewById(R.id.username_login);
        passwordInLogin = findViewById(R.id.password_login);
        loginBtnInLogin = findViewById(R.id.login_btn);
        gotoRegisterBtn = findViewById(R.id.goto_register_btn);
    }

    private void setListeners(){
        loginBtnInLogin.setOnClickListener(this);
        gotoRegisterBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                login();
                break;
            case R.id.goto_register_btn:
                gotoRegister();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void login(){
        String username = usernameInLogin.getText().toString().trim();
        String password = passwordInLogin.getText().toString().trim();

        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            ToastUtils.showShortToastCenter("用户名密码不能为空!");
            usernameInLogin.setText("");
            passwordInLogin.setText("");
            return;
        }

        // 使用SHA256生成用户主密钥
        byte[] mainSecret = encryptionService.getSecretKey(username, password);
        // 用主密钥加密用户密码后上传
        String encryptPassword = FileUtils.bytes2Base64(encryptionService.encryptByAES128(password, mainSecret));
        // 进行登录验证
        int res = userService.login(username, encryptPassword);

        // 登录失败
        if(res==1){
            ToastUtils.showShortToastCenter("用户名密码错误!");
            usernameInLogin.setText("");
            passwordInLogin.setText("");
            return;
        }

        // 登录成功
        // 设置主密钥为全局变量
        GraduateDesignApplication.setMainSecret(mainSecret);
        // 把原始密码设为全局变量，以供后续加密文件使用
        GraduateDesignApplication.setOriginPassword(password);
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        ActivityJumpUtils.jumpActivity(LoginActivity.this, intent, 100L, true);
    }

    private void gotoRegister(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        ActivityJumpUtils.jumpActivity(LoginActivity.this, intent, 100L, true);
    }
}
