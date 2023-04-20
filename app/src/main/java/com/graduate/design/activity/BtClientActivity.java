package com.graduate.design.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.allenliu.classicbt.BleManager;
import com.allenliu.classicbt.Connect;
import com.allenliu.classicbt.listener.ConnectResultlistner;
import com.allenliu.classicbt.listener.PinResultListener;
import com.allenliu.classicbt.listener.ScanResultListener;
import com.allenliu.classicbt.listener.TransferProgressListener;
import com.allenliu.classicbt.scan.ScanConfig;
import com.graduate.design.R;
import com.graduate.design.adapter.DeviceItemAdapter;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.PermissionUtils;
import com.graduate.design.utils.ToastUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BtClientActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private ImageButton backImageButton;
    private Button backButton;
    private Button searchDeviceButton;
    private EditText sendMsg;
    private Button sendButton;
    private ListView listView;
    private Button disconnectButton;
    private Button shareButton;
    private Button transferButton;

    private DeviceItemAdapter deviceItemAdapter;
 //   private Connect currentConnect;
    // 一次只允许一个连接
    private Boolean isConnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_client);

        // 初始化页面
        InitViewUtils.initView(this);
        // 申请权限
        PermissionUtils.initPermission(this);
        // 初始化数据
        initData();
        // 拿到页面元素
        getComponentsById();
        // 设置监听事件
        setListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在此注销bleManager
        if(BleManager.getInstance()!=null)
            BleManager.getInstance().destory();
    }

    private void initData() {
        deviceItemAdapter = new DeviceItemAdapter(GraduateDesignApplication.getAppContext());
    }

    private void getComponentsById() {
        backImageButton = findViewById(R.id.back_image_btn);
        backButton = findViewById(R.id.back_btn);
        searchDeviceButton = findViewById(R.id.search_device_btn);
        sendButton = findViewById(R.id.send_btn);
        sendMsg = findViewById(R.id.send_msg);
        disconnectButton = findViewById(R.id.disconnect_btn);
        shareButton = findViewById(R.id.share_btn);
        transferButton = findViewById(R.id.transfer_btn);

        listView = findViewById(R.id.show_search_device);
        listView.setAdapter(deviceItemAdapter);
    }

    private void setListeners() {
        backImageButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        searchDeviceButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        transferButton.setOnClickListener(this);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_image_btn:
            case R.id.back_btn:
                gotoHomeActivity();
                break;
            case R.id.search_device_btn:
                searchDevice();
                break;
            case R.id.send_btn:
                send();
                break;
            case R.id.disconnect_btn:
                disconnect();
                break;
            case R.id.share_btn:
                selectShareFile(true);
                break;
            case R.id.transfer_btn:
                selectShareFile(false);
                break;
            default:
                ToastUtils.showShortToastCenter("错误的元素ID");
                break;
        }
    }

    private void gotoHomeActivity() {
        finish();
    }

    // 扫描设备
    private void searchDevice() {
        deviceItemAdapter.clearDevice();
        // 停止搜索
        BleManager.getInstance().stopSearch();
        BleManager.getInstance().scan(new ScanConfig(10000), new ScanResultListener() {
            @Override
            public void onDeviceFound(BluetoothDevice device) {
                deviceItemAdapter.addDevice(device);
            }

            @Override
            public void onFinish() {
                ToastUtils.showShortToastCenter("扫描结束");
            }

            @Override
            public void onError() {

            }
        });
    }

    private void send(){
        if(GraduateDesignApplication.getCurConnect() == null) {
            ToastUtils.showShortToastCenter("未连接蓝牙");
            return;
        }
        byte[] msg = sendMsg.getText().toString().getBytes(StandardCharsets.UTF_8);
        byte[] start = GraduateDesignApplication.getStart();
        byte[] end = GraduateDesignApplication.getEnd();
        byte[] lineBreak = "\n".getBytes(StandardCharsets.UTF_8);
        ByteBuffer fullMsg = ByteBuffer.allocate(msg.length + start.length + end.length + lineBreak.length);
        fullMsg.put(start).put(msg).put(lineBreak).put(end);

        GraduateDesignApplication.getCurConnect().write(fullMsg.array(), new TransferProgressListener() {
            @Override
            public void transfering(int progress) {
                ToastUtils.showShortToastCenter("正在传输数据：" + progress);
            }

            @Override
            public void transferSuccess(byte[] bytes) {
                ToastUtils.showShortToastCenter("传输成功");
            }

            @Override
            public void transferFailed(Exception exception) {
                ToastUtils.showShortToastCenter("传输失败：" + exception.getLocalizedMessage());
            }
        });
    }

    private void disconnect(){
        BleManager.getInstance().destory();
    }

    private void selectShareFile(Boolean isShare){
        Intent intent = new Intent(BtClientActivity.this, ShareActivity.class);
        intent.putExtra("nodeId", GraduateDesignApplication.getUserInfo().getRootId());
        intent.putExtra("isShare", isShare);
        ActivityJumpUtils.jumpActivity(BtClientActivity.this, intent, 100L, false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.show_search_device:
                connect(position);
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void connect(int position){
        if(isConnected) return;
        BluetoothDevice device = (BluetoothDevice) deviceItemAdapter.getItem(position);

        // 当前设备已经配对
        if(BleManager.getInstance().getPairedDevices().contains(device)){
            BleManager.getInstance().connect(device, new ConnectResultlistner() {
                @Override
                public void connectSuccess(Connect connect) {
                    ToastUtils.showShortToastCenter("连接成功");
                    GraduateDesignApplication.setCurConnect(connect);
                    isConnected = true;
                    disconnectButton.setEnabled(true);
                    shareButton.setEnabled(true);
                    sendButton.setEnabled(true);
                }

                @Override
                public void connectFailed(Exception e) {
                    ToastUtils.showShortToastCenter("连接失败" + e.getLocalizedMessage());
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void disconnected() {
                    ToastUtils.showShortToastCenter("连接已断开");
                    isConnected = false;
                    GraduateDesignApplication.setCurConnect(null);
                    disconnectButton.setEnabled(false);
                    shareButton.setEnabled(false);
                    sendButton.setEnabled(false);
                }
            });
        }

        // 进行配对
        else {
            BleManager.getInstance().pin(device, new PinResultListener() {
                @Override
                public void paired(BluetoothDevice device) {
                    connect(position);
                }
            });

        }
    }
}
