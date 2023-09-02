package com.graduate.design.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;

public class MineFragment extends Fragment implements View.OnClickListener {
    private TextView username;
    private TextView email;
    private Button changePasswordButton;
    private Button settingsButton;
    private Button aboutButton;
    private HomeActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_mine, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化数据
        initData();
        // 获取页面元素
        getComponentsById(view);
        // 为按钮元素设置点击事件
        setListeners();
        // 设置用户信息
        setUserInfo();
    }

    private void initData(){
        activity = (HomeActivity) getActivity();
    }

    private void getComponentsById(View view){
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        changePasswordButton = view.findViewById(R.id.change_password_btn);
        settingsButton = view.findViewById(R.id.settings_btn);
        aboutButton = view.findViewById(R.id.about_btn);
    }

    private void setListeners(){
        changePasswordButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
    }

    private void setUserInfo(){
        username.setText(GraduateDesignApplication.getUsername());
        email.setText(GraduateDesignApplication.getUserInfo().getEmail());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_password_btn:
                gotoChangePassword();
                break;
            case R.id.settings_btn:
                gotoSettings();
                break;
            case R.id.about_btn:
                gotoAbout();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }


    private void gotoChangePassword(){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, new ChangePasswordFragment())
                .addToBackStack(null)
                .commit();
    }

    private void gotoSettings(){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, new SettingsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void gotoAbout(){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, new AboutFragment())
                .addToBackStack(null)
                .commit();
    }
}
