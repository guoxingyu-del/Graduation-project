package com.graduate.design.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.EncryptionServiceImpl;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ByteUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;

import java.nio.charset.StandardCharsets;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {
    private Button backButton;
    private ImageButton backImageButton;
    private Button confirmButton;
    private EditText oldPasswordEdit;
    private EditText newPasswordEdit;

    private String token;
    private HomeActivity activity;
    private UserService userService;
    private EncryptionService encryptionService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_change_password, container, false);
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
        activity = (HomeActivity) getActivity();

        userService = new UserServiceImpl();
        encryptionService = new EncryptionServiceImpl();
    }

    private void getComponentsById(View view){
        backButton = view.findViewById(R.id.back_btn_disk);
        backImageButton = view.findViewById(R.id.back_image_btn_disk);
        confirmButton = view.findViewById(R.id.change_pwd_confirm_button);
        oldPasswordEdit = view.findViewById(R.id.old_password);
        newPasswordEdit = view.findViewById(R.id.new_password);
    }

    private void setListeners(){
        backButton.setOnClickListener(this);
        backImageButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
    }

    // 为按钮元素设置对应的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn_disk:
            case R.id.back_image_btn_disk:
                goBackToMine();
                break;
            case R.id.change_pwd_confirm_button:
                confirmChangePassword();
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

    private void confirmChangePassword(){
        // 修改密码
        String oldPassword = oldPasswordEdit.getText().toString();
        String newPassword = newPasswordEdit.getText().toString();
        if(oldPassword.length() ==0 || newPassword.length()==0) {
            ToastUtils.showShortToastCenter("项目不能为空");
            return;
        }

        // 利用旧密码和新密码分别生成hashId
        byte[] usernameBytes = GraduateDesignApplication.getUsername().getBytes(StandardCharsets.ISO_8859_1);
        byte[] oldBytes = oldPassword.getBytes(StandardCharsets.ISO_8859_1);
        byte[] newBytes = newPassword.getBytes(StandardCharsets.ISO_8859_1);

        byte[] oldHashId = encryptionService.SHA512(ByteUtils.mergeBytes(usernameBytes, oldBytes));
        byte[] newHashId = encryptionService.SHA512(ByteUtils.mergeBytes(usernameBytes, newBytes));

        // 利用新密码加密key1和key2上传
        byte[] pwd2SHA256 = encryptionService.SHA256(newBytes);
        byte[] encryptKey1 = encryptionService.encryptByAES256(GraduateDesignApplication.getKey1(), pwd2SHA256);
        byte[] encryptKey2 = encryptionService.encryptByAES256(GraduateDesignApplication.getKey2(), pwd2SHA256);

        int res = userService.changePassword(FileUtils.bytes2Base64(oldHashId), FileUtils.bytes2Base64(newHashId),
                FileUtils.bytes2Base64(encryptKey1), FileUtils.bytes2Base64(encryptKey2), token);

        if(res==0) ToastUtils.showShortToastCenter("修改成功");
        else ToastUtils.showShortToastCenter("修改失败，旧密码错误");

        oldPasswordEdit.setText("");
        newPasswordEdit.setText("");
    }
}
