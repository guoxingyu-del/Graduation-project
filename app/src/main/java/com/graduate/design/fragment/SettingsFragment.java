package com.graduate.design.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.activity.LoginActivity;
import com.graduate.design.proto.UserLogin;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private Button backButton;
    private ImageButton backImageButton;
    private Button logoutButton;
    private String token;
    private UserLogin.UserInfo userInfo;
    private HomeActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化数据
        initData();
        // 拿到页面元素
        getComponentsById(view);
        // 设置监听事件
        setListeners();
    }

    private void initData(){
        token = GraduateDesignApplication.getToken();
        userInfo = GraduateDesignApplication.getUserInfo();
        activity = (HomeActivity) getActivity();
    }

    private void getComponentsById(View view){
        backButton = view.findViewById(R.id.back_btn_disk);
        backImageButton = view.findViewById(R.id.back_image_btn_disk);
        logoutButton = view.findViewById(R.id.logout);
    }

    private void setListeners(){
        backButton.setOnClickListener(this);
        backImageButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
    }

    // 为按钮元素设置对应的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn_disk:
            case R.id.back_image_btn_disk:
                goBackToMine();
                break;
            case R.id.logout:
                logout();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    // 返回到我的页面
    private void goBackToMine(){
        activity.getSupportFragmentManager().popBackStack();
    }

    private void logout(){
        // 登出
        token = null;
        userInfo = null;
        // 跳转到登录页面
        Intent intent = new Intent(activity, LoginActivity.class);
        ActivityJumpUtils.jumpActivity(activity, intent, 100L, true);
    }
}
