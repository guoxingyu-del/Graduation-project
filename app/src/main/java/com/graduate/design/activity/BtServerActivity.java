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
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.PermissionUtils;
import com.graduate.design.utils.ToastUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BtServerActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backImageButton;
    private Button backButton;
    private TextView serverConnectState;
    private TextView showReceiveInfo;
    private ListView listView;

    private String filename;
    private String fileContent;
    private String shareTokenL;
    private String shareTokenJId;
    private String shareTokenKId;
    private String shareTokenFileId;
    private UserService userService;
    private ReceiveFileItemAdapter fileItemAdapter;
    // 当前连接
 //   private Connect currentConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_server);

        // 初始化页面
        InitViewUtils.initView(this);
        // 动态申请权限
        PermissionUtils.initPermission(this);
        // 初始化数据
        initData();
        // 获取页面元素
        getComponentById();
        // 设置监听事件
        setListeners();
        // 注册为服务器，接收数据
        registerServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在此注销bleManager
        if(BleManager.getInstance()!=null)
            BleManager.getInstance().destory();
    }

    private void initData(){
        userService = new UserServiceImpl();
        fileItemAdapter = new ReceiveFileItemAdapter(getApplicationContext(), BtServerActivity.this);
    }

    private void getComponentById(){
        backImageButton = findViewById(R.id.back_image_btn);
        backButton = findViewById(R.id.back_btn);

        serverConnectState = findViewById(R.id.server_connect_state);
        showReceiveInfo = findViewById(R.id.show_receive_info);

        listView = findViewById(R.id.show_receive_files);
        listView.setAdapter(fileItemAdapter);
    }

    private void setListeners(){
        backImageButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    private void registerServer(){
        // 设置可被发现时间为300s
        BleManager.getInstance().enableDiscoverable(300);
        // 注册为服务器
        BleManager.getInstance().registerServerConnection(new ConnectResultlistner() {
            @Override
            public void connectSuccess(Connect connect) {
                ToastUtils.showShortToastCenter("蓝牙已连接");
                serverConnectState.setText("已连接");
                GraduateDesignApplication.setCurConnect(connect);
                // 开始接收数据
                receiveMsg();
            }

            @Override
            public void connectFailed(Exception e) {
                ToastUtils.showShortToastCenter("蓝牙连接失败");
            }

            @Override
            public void disconnected() {
                ToastUtils.showShortToastCenter("蓝牙已断开连接");
                serverConnectState.setText("未连接");
                BleManager.getInstance().destory();
                GraduateDesignApplication.setCurConnect(null);
                // 重新变为可发现状态
                registerServer();
            }
        });
    }

    private void receiveMsg(){
        if(GraduateDesignApplication.getCurConnect() == null){
            ToastUtils.showShortToastCenter("没有蓝牙连接");
            return;
        }
        GraduateDesignApplication.getCurConnect().setReadPacketVerifyListener(new PacketDefineListener() {
            @Override
            public byte[] getPacketStart() {
                return GraduateDesignApplication.getStart();
            }

            @Override
            public byte[] getPacketEnd() {
                return GraduateDesignApplication.getEnd();
            }
        });
        GraduateDesignApplication.getCurConnect().read(new TransferProgressListener() {
            @Override
            public void transfering(int progress) {
                ToastUtils.showShortToastCenter("正在传输：" + progress + "%");
            }

            @Override
            public void transferSuccess(byte[] bytes) {
                ToastUtils.showShortToastCenter("传输数据成功");

                String res = ByteString.copyFrom(bytes).toString(StandardCharsets.UTF_8);

                // 取出消息整体内容
                int startIndex = res.indexOf(getString(R.string.startMsg));
                int endIndex = res.indexOf(getString(R.string.endMsg));
                String resWithoutStartEnd = res.substring(startIndex + getString(R.string.startMsg).length(), endIndex);

                // 将消息分割出文件名和文件内容
                int filenameIndex = resWithoutStartEnd.indexOf(getString(R.string.filename));
                int fileContentIndex = resWithoutStartEnd.indexOf(getString(R.string.fileContent));
                int LIndex = resWithoutStartEnd.indexOf(getString(R.string.shareTokenL));
                int JIdIndex = resWithoutStartEnd.indexOf(getString(R.string.shareTokenJid));
                int KIdIndex = resWithoutStartEnd.indexOf(getString(R.string.shareTokenKid));
                int FileIdIndex = resWithoutStartEnd.indexOf(getString(R.string.shareTokenFileId));
                // 测试消息，在页面展示
                if(filenameIndex == -1 || fileContentIndex == -1) {
                    showReceiveInfo.setText(resWithoutStartEnd);
                    return;
                }
                if (LIndex == -1 || JIdIndex == -1 || KIdIndex == -1 || FileIdIndex == -1) {
                    showReceiveInfo.setText(resWithoutStartEnd);
                    return;
                }
                // 提取出文件名和文件内容
                // 除去文件名中的换行符
                shareTokenL = FileUtils.removeLineBreak(resWithoutStartEnd.substring(LIndex + getString(R.string.shareTokenL).length(), JIdIndex));
                shareTokenJId = FileUtils.removeLineBreak(resWithoutStartEnd.substring(JIdIndex + getString(R.string.shareTokenJid).length(), KIdIndex));
                shareTokenKId = FileUtils.removeLineBreak(resWithoutStartEnd.substring(KIdIndex + getString(R.string.shareTokenKid).length(), FileIdIndex));
                shareTokenFileId = FileUtils.removeLineBreak(resWithoutStartEnd.substring(FileIdIndex + getString(R.string.shareTokenFileId).length(), filenameIndex));
                filename = FileUtils.removeLineBreak(resWithoutStartEnd.substring(filenameIndex + getString(R.string.filename).length(), fileContentIndex));
                fileContent = resWithoutStartEnd.substring(fileContentIndex + getString(R.string.fileContent).length());
//                String[] fileInfo = new String[]{filename, fileContent};
                String[] shareToken = new String[]{filename, fileContent, shareTokenL, shareTokenJId, shareTokenKId, shareTokenFileId};
//                fileItemAdapter.addFileItem(fileInfo);
                fileItemAdapter.addFileItem(shareToken);
            }

            @Override
            public void transferFailed(Exception exception) {
                ToastUtils.showShortToastCenter("传输数据失败：" + exception.getLocalizedMessage());
            }
        });
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
}
