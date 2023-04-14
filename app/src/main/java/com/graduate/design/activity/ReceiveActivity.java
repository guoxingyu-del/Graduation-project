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

import java.util.List;

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
    private String filename;
    private String fileContent;
    private String shareTokenL;
    private String shareTokenJId;
    private String shareTokenKId;
    private String shareTokenFileId;
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

    private void initData(){
        token = GraduateDesignApplication.getToken();
        context = getApplicationContext();
        userService = new UserServiceImpl();
        encryptionService = new EncryptionServiceImpl();
        nodeId = getIntent().getLongExtra("nodeId", GraduateDesignApplication.getUserInfo().getRootId());
        filename = getIntent().getStringExtra("filename");
        fileContent = getIntent().getStringExtra("fileContent");
        shareTokenL = getIntent().getStringExtra("shareTokenL");
        shareTokenJId = getIntent().getStringExtra("shareTokenJId");
        shareTokenKId = getIntent().getStringExtra("shareTokenKId");
        shareTokenFileId = getIntent().getStringExtra("shareTokenFileId");
        fileItemAdapter = new ChooseDirFileItemAdapter(context, R.layout.item_file);
    }

    private void getComponentsById(){
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
            subNodes = FileUtils.putDirBeforeFile(userService.getNodeList(nodeId, token));
            map.put(nodeId, new GotNodeList(subNodes, false));
        }
        fileItemAdapter.addAllFileItem(subNodes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

//    private void receive(){
//        // 将文件内容和文件标题作为一个新的节点上传
//        // 利用文件名和用户密码生成文件密钥
//        byte[] fileSecret = GraduateDesignApplication.getKey2();
//        // 将加密结果转为Base64编码
//        String encryptContent = FileUtils.bytes2Base64(encryptionService.encryptByAES256(fileContent, fileSecret));
//        if(encryptContent == null) encryptContent = "";
//
//        // 先从服务器中拿到文件节点
//        Long fileId = userService.getNodeId(token);
//        List<FileUpload.indexToken> indexTokens = FileUtils.indexList(fileContent, fileId);
//        // 同步上传biIndex进行更新
//        String biIndexString = FileUtils.bytes2Base64(GraduateDesignApplication.getBiIndex().writeObject());
//
//        userService.uploadFile(filename, nodeId, indexTokens, ByteString.copyFromUtf8(encryptContent), biIndexString,
//                fileId, token);
//        ToastUtils.showShortToastCenter("保存成功");
//    }
private void receive() {
    // 将文件内容和文件标题作为一个新的节点上传
    // 利用文件名和用户密码生成文件密钥
    byte[] fileSecret = GraduateDesignApplication.getKey2();
    // 将加密结果转为Base64编码
    String encryptContent = FileUtils.bytes2Base64(encryptionService.encryptByAES256(fileContent, fileSecret));
    if (encryptContent == null) encryptContent = "";

    // 先从服务器中拿到文件节点
    Long fileId = userService.getNodeId(token);
//        List<FileUpload.indexToken> indexTokens = FileUtils.indexList(fileContent, fileId);
    // 同步上传biIndex进行更新



//        Log.d("ssssssssss", "go receive");
    Common.ShareToken shareToken = Common.ShareToken.newBuilder().setL(shareTokenL).setJId(shareTokenJId).setKId(shareTokenKId).setFileId(shareTokenFileId).build();
    List<FileUpload.indexToken> indexTokens = userService.shareTokenRegister(shareToken, token);
//        ToastUtils.showShortToastCenter("保存成功");
    String biIndexString = FileUtils.bytes2Base64(GraduateDesignApplication.getBiIndex().writeObject());
    userService.uploadFile(filename, nodeId, indexTokens, ByteString.copyFromUtf8(encryptContent), biIndexString,
            fileId, token);
    ToastUtils.showShortToastCenter("保存成功");
}
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
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
            intent.putExtra("filename", filename);
            intent.putExtra("fileContent", fileContent);
            intent.putExtra("shareTokenL", shareTokenL);
            intent.putExtra("shareTokenJId", shareTokenJId);
            intent.putExtra("shareTokenKId", shareTokenKId);
            intent.putExtra("shareTokenFileId", shareTokenFileId);
            ActivityJumpUtils.jumpActivity(ReceiveActivity.this, intent, 100L, false);
        }
    }

}
