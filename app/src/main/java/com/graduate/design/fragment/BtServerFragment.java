package com.graduate.design.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.allenliu.classicbt.BleManager;
import com.allenliu.classicbt.Connect;
import com.allenliu.classicbt.listener.ConnectResultlistner;
import com.allenliu.classicbt.listener.PacketDefineListener;
import com.allenliu.classicbt.listener.TransferProgressListener;
import com.google.protobuf.ByteString;
import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.adapter.fileItem.ReceiveFileItemAdapter;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.PermissionUtils;
import com.graduate.design.utils.ToastUtils;

import java.nio.charset.StandardCharsets;

public class BtServerFragment extends Fragment implements View.OnClickListener {
    private ImageButton backImageButton;
    private Button backButton;
    private TextView serverConnectState;
    private TextView showReceiveInfo;
    private ListView listView;

    private String filename;
    private String fileContent;
    private UserService userService;
    private ReceiveFileItemAdapter fileItemAdapter;
    private HomeActivity activity;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bt_server, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化数据
        initData();
        // 动态申请权限
        PermissionUtils.initPermission(activity);
        // 获取页面元素
        getComponentById(view);
        // 在分享界面中将底部导航栏隐藏
        hideBottomNavBar();
        // 设置监听事件
        setListeners();
        // 注册为服务器，接收数据
        registerServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在此注销bleManager
        if(BleManager.getInstance()!=null)
            BleManager.getInstance().destory();
    }

    private void initData(){
        userService = new UserServiceImpl();
        activity = (HomeActivity) getActivity();
        context = getContext();
        fileItemAdapter = new ReceiveFileItemAdapter(context, activity, this);
    }

    private void getComponentById(View view){
        backImageButton = view.findViewById(R.id.back_image_btn);
        backButton = view.findViewById(R.id.back_btn);

        serverConnectState = view.findViewById(R.id.server_connect_state);
        showReceiveInfo = view.findViewById(R.id.show_receive_info);

        listView = view.findViewById(R.id.show_receive_files);
        listView.setAdapter(fileItemAdapter);
    }

    private void hideBottomNavBar(){
        activity.findViewById(R.id.rg_tab).setVisibility(View.GONE);
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
                showReceiveInfo.setText(res);
                int filenameIndex = res.indexOf("filename:");
                int fileContentIndex = res.indexOf("fileContent:");
                int endIndex = res.indexOf("结束");
                if(filenameIndex == -1 || fileContentIndex == -1) return;

                // 提取出文件名和文件内容
                // 除去文件名中的换行符
                filename = FileUtils.removeLineBreak(res.substring(filenameIndex + "filename:".length(), fileContentIndex));
                fileContent = res.substring(fileContentIndex + "fileContent:".length(), endIndex);

                String[] fileInfo = new String[]{filename, fileContent};
                fileItemAdapter.addFileItem(fileInfo);
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
        // 在返回主页面之前将底部导航栏重新显示
        activity.findViewById(R.id.rg_tab).setVisibility(View.VISIBLE);
        activity.getSupportFragmentManager().popBackStack();
    }

    public void gotoReceive(String[] node){
        ShareFragment fragment = new ShareFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("nodeId", GraduateDesignApplication.getUserInfo().getRootId());
        bundle.putString("filename", node[0]);
        bundle.putString("fileContent", node[1]);
        fragment.setArguments(bundle);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
