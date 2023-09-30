package com.graduate.design.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.protobuf.ByteString;
import com.graduate.design.R;
import com.graduate.design.adapter.fileItem.ChooseDirFileItemAdapter;
import com.graduate.design.adapter.fileItem.GetNodeFileItemAdapter;
import com.graduate.design.adapter.fileItem.ReceiveFileItemAdapter;
import com.graduate.design.entity.GotNodeList;
import com.graduate.design.proto.Common;
import com.graduate.design.proto.FileUpload;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.EncryptionServiceImpl;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReceiveActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private Button backButton;
    private ImageButton backImageButton;
    private ListView listView;
    private Button receiveButton;

    private String token;
    private Context context;
    private UserService userService;
    private EncryptionService encryptionService;
    private Long nodeId;
    private String type;
    private String filename;
    private String from;
    private String secretKey;
    private String shareTokenL;
    private String shareTokenJId;
    private String shareTokenKId;
    private String isShare;
    private String address;
    private ChooseDirFileItemAdapter fileItemAdapter;
    private List<Common.Node> subNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        // 初始化视图
        InitViewUtils.initView(this);
        // 初始化数据
        initData();
        // 拿到页面元素
        getComponentsById();
        // 设置监听事件
        setListeners();
        // 设置当前节点下的文件列表
        setNodeList();
    }

    private void initData() {
        token = GraduateDesignApplication.getToken();
        context = getApplicationContext();
        userService = new UserServiceImpl();
        encryptionService = new EncryptionServiceImpl();

        nodeId = getIntent().getLongExtra("nodeId", GraduateDesignApplication.getUserInfo().getRootId());
        type = getIntent().getStringExtra("type");
        filename = getIntent().getStringExtra("filename");
        from = getIntent().getStringExtra("from");
        secretKey = getIntent().getStringExtra("fileSecret");
        shareTokenL = getIntent().getStringExtra("shareTokenL");
        shareTokenJId = getIntent().getStringExtra("shareTokenJId");
        shareTokenKId = getIntent().getStringExtra("shareTokenKId");
        isShare = getIntent().getStringExtra("isShare");
        address = getIntent().getStringExtra("address");

        fileItemAdapter = new ChooseDirFileItemAdapter(context, R.layout.item_file);
    }

    private void getComponentsById() {
        backButton = findViewById(R.id.back_btn);
        backImageButton = findViewById(R.id.back_image_btn);
        receiveButton = findViewById(R.id.receive_btn);

        listView = findViewById(R.id.show_files);
        listView.setAdapter(fileItemAdapter);
    }

    private void setListeners(){
        backButton.setOnClickListener(this);
        backImageButton.setOnClickListener(this);
        receiveButton.setOnClickListener(this);

        listView.setOnItemClickListener(this);
    }

    private void setNodeList(){
        fileItemAdapter.clear();
        Map<Long, GotNodeList> map = GraduateDesignApplication.getAllNodeList();
        if(map.containsKey(nodeId) && !map.get(nodeId).getUpdate())
            subNodes = FileUtils.putDirBeforeFile(map.get(nodeId).getNodeList());
        else {
            subNodes = FileUtils.putDirBeforeFile(userService.getDir(nodeId, token));
            map.put(nodeId, new GotNodeList(subNodes, false));
        }
        fileItemAdapter.addAllFileItem(subNodes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
            case R.id.back_image_btn:
                goBack();
                break;
            case R.id.receive_btn:
                receive();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void goBack(){
        finish();
    }

    private void receive() {
        List<String> res = userService.firstShare(shareTokenL, shareTokenJId, token);
        // 为接收文件新建一个id
        Long curNodeId = userService.getNodeId(token);
        List<Common.indexToken> indexTokenList = new ArrayList<>();
        for(String cid : res) {
            byte[] cidBytes = FileUtils.Base64ToBytes(cid);
            byte[] wordBytes = encryptionService.decryptByAES256(cidBytes, FileUtils.Base64ToBytes(shareTokenKId));
            String word = new String(wordBytes, StandardCharsets.ISO_8859_1);
            indexTokenList.add(encryptionService.uploadIndex(curNodeId, word));
        }
        byte[] secretKeyBytes = FileUtils.Base64ToBytes(secretKey);
        byte[] key2 = GraduateDesignApplication.getKey2();
        byte[] encryptKey = encryptionService.encryptByAES256(secretKeyBytes, key2);
        Boolean isShareBoolean = false;
        if("1".equals(isShare)) {
            isShareBoolean = true;
        }
        int ret = userService.secondShare(filename, nodeId, FileUtils.bytes2Base64(GraduateDesignApplication.getBiIndex().writeObject()),
                curNodeId, isShareBoolean, Long.parseLong(address), FileUtils.bytes2Base64(encryptKey), indexTokenList, token);

        if(ret==0) {
            ToastUtils.showShortToastCenter("接收成功");
            GraduateDesignApplication.getAllNodeList().get(nodeId).setUpdate(true);
            setNodeList();
        }
        else ToastUtils.showShortToastCenter("接收失败");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.show_files:
                getFileContentOrNextDir(position);
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void getFileContentOrNextDir(int position){
        Common.Node clickedNode = subNodes.get(position);
        // 如果点击的是文件夹，进入下一层
        if(clickedNode.getNodeType()== Common.NodeType.Dir){
            Intent intent = new Intent(ReceiveActivity.this, ReceiveActivity.class);
            intent.putExtra("nodeId", clickedNode.getNodeId());
            intent.putExtra("type", type);
            intent.putExtra("filename", filename);
            intent.putExtra("from", from);
            intent.putExtra("fileSecret", secretKey);
            intent.putExtra("shareTokenL", shareTokenL);
            intent.putExtra("shareTokenJId", shareTokenJId);
            intent.putExtra("shareTokenKId", shareTokenKId);
            intent.putExtra("isShare", isShare);
            intent.putExtra("address", address);
            ActivityJumpUtils.jumpActivity(ReceiveActivity.this, intent, 100L, false);
        }
    }

}
