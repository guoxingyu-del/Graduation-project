package com.graduate.design.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.allenliu.classicbt.BleManager;
import com.allenliu.classicbt.Connect;
import com.allenliu.classicbt.listener.ConnectResultlistner;
import com.allenliu.classicbt.listener.PacketDefineListener;
import com.allenliu.classicbt.listener.TransferProgressListener;
import com.google.protobuf.ByteString;
import com.graduate.design.R;
import com.graduate.design.adapter.fileItem.ReceiveFileItemAdapter;
import com.graduate.design.proto.GetShareTokens;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.EncryptionServiceImpl;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.DateTimeUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.PermissionUtils;
import com.graduate.design.utils.ToastUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WifiReceiveActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backImageButton;
    private Button backButton;
    private ListView listView;

    private UserService userService;
    private ReceiveFileItemAdapter fileItemAdapter;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_receive);

        // 初始化页面
        InitViewUtils.initView(this);
        // 初始化数据
        initData();
        // 获取页面元素
        getComponentById();
        // 设置监听事件
        setListeners();
        // 设置接收文件列表
        setNodeList();
    }

    private void initData(){
        token = GraduateDesignApplication.getToken();
        userService = new UserServiceImpl();
        fileItemAdapter = new ReceiveFileItemAdapter(getApplicationContext(), WifiReceiveActivity.this);
    }

    private void getComponentById(){
        backImageButton = findViewById(R.id.back_image_btn);
        backButton = findViewById(R.id.back_btn);

        listView = findViewById(R.id.wifi_receive_files);
        listView.setAdapter(fileItemAdapter);
    }

    private void setListeners(){
        backImageButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_image_btn:
            case R.id.back_btn:
                gotoHomeActivity();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的元素ID");
                break;
        }
    }

    private void gotoHomeActivity() {
        finish();
    }

    private void setNodeList() {
        fileItemAdapter.clear();
        List<GetShareTokens.ShareMesssage> allShareToken = userService.getAllShareToken(GraduateDesignApplication.getUsername(), token);
        if(allShareToken==null) return;
        for(GetShareTokens.ShareMesssage shareMesssage : allShareToken){
            String createTime = DateTimeUtils.timerToString(shareMesssage.getCreateTime()*1000);
            fileItemAdapter.addFileItem(new String[]{"file", shareMesssage.getFileName(), createTime,
            shareMesssage.getSecretKey(), shareMesssage.getShareToken().getL(), shareMesssage.getShareToken().getJId(),
                    shareMesssage.getShareToken().getKId(), "1", shareMesssage.getShareToken().getFileId(), "1",
                    shareMesssage.getShareTokenId()});
        }
    }
}

