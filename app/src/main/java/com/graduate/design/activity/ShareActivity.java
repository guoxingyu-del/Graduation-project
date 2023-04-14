package com.graduate.design.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.allenliu.classicbt.Connect;
import com.allenliu.classicbt.listener.TransferProgressListener;
import com.graduate.design.R;
import com.graduate.design.adapter.fileItem.ShareFileItemAdapter;
import com.graduate.design.entity.GotNodeList;
import com.graduate.design.proto.Common;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.sharettokenprotocol.ShareTokenGen;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private Button backButton;
    private ImageButton backImageButton;
    private ListView shareFileList;

    private Long nodeId;
    private List<Common.Node> subNodes;
    private ShareFileItemAdapter shareFileItemAdapter;
    private String token;
    private UserService userService;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

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
        shareFileItemAdapter = new ShareFileItemAdapter(context, R.layout.item_file_share);
        nodeId = getIntent().getLongExtra("nodeId", GraduateDesignApplication.getUserInfo().getRootId());
    }

    private void getComponentsById(){
        backButton = findViewById(R.id.back_btn);
        backImageButton = findViewById(R.id.back_image_btn);

        shareFileList = findViewById(R.id.show_share_files);
        shareFileList.setAdapter(shareFileItemAdapter);
    }

    private void setListeners(){
        backButton.setOnClickListener(this);
        backImageButton.setOnClickListener(this);

        shareFileList.setOnItemClickListener(this);
        shareFileList.setMultiChoiceModeListener(new MyMultiChoiceModeListener());
    }

    private void setNodeList(){
        shareFileItemAdapter.clear();
        Map<Long, GotNodeList> map = GraduateDesignApplication.getAllNodeList();
        if(map.containsKey(nodeId) && !map.get(nodeId).getUpdate())
            subNodes = map.get(nodeId).getNodeList();
        else {
            subNodes = FileUtils.putDirBeforeFile(userService.getNodeList(nodeId, token));
            map.put(nodeId, new GotNodeList(subNodes, false));
        }
        shareFileItemAdapter.addAllFileItem(subNodes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
            case R.id.back_image_btn:
                goBack();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void goBack(){
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.show_share_files:
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
            Intent intent = new Intent(ShareActivity.this, ShareActivity.class);
            intent.putExtra("nodeId", clickedNode.getNodeId());
            ActivityJumpUtils.jumpActivity(ShareActivity.this, intent, 100L, false);
        }
    }

    private class MyMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {
        List<Common.Node> selectedItem = new ArrayList<>();

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if(checked) selectedItem.add(subNodes.get(position));
            else selectedItem.remove(subNodes.get(position));
            shareFileItemAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.confirm_cancel, menu);
            shareFileItemAdapter.setIsCheckable(true);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.share:
                    // 分享
                    shareFile();
                    ToastUtils.showShortToastCenter("点击了分享按钮");
                    break;
                default:
                    break;
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            shareFileItemAdapter.setIsCheckable(false);
            selectedItem.clear();
        }

        private void shareFile(){
            Connect curConnect = GraduateDesignApplication.getCurConnect();
            if(curConnect == null) {
                ToastUtils.showShortToastCenter("分享之前需要连接蓝牙");
                return;
            }

            byte[] start = GraduateDesignApplication.getStart();
            byte[] end = GraduateDesignApplication.getEnd();

//            // TODO 批量分享，加密传输
//            for(Common.Node node : selectedItem){
//                if(node.getNodeType() == Common.NodeType.Dir) continue;
//                String content = userService.getNodeContent(node.getNodeId(), token);
//                String fileNameAndContent = getString(R.string.filename) + node.getNodeName() + "\n" + getString(R.string.fileContent) + content + "\n";
//                byte[] msg = fileNameAndContent.getBytes(StandardCharsets.UTF_8);
//                ByteBuffer fullMsg = ByteBuffer.allocate(msg.length + start.length + end.length);
//                fullMsg.put(start).put(msg).put(end);
            // TODO 批量分享，加密传输
            for(Common.Node node : selectedItem){
                if(node.getNodeType() == Common.NodeType.Dir) continue;
                String content = userService.getNodeContent(node.getNodeId(), token);
                String fileNameAndContent = getString(R.string.filename) + node.getNodeName() + "\n" + getString(R.string.fileContent) + content + "\n";
//                byte[] msg = fileNameAndContent.getBytes(StandardCharsets.UTF_8);
//                ByteBuffer fullMsg = ByteBuffer.allocate(msg.length + start.length + end.length);
//                fullMsg.put(start).put(msg).put(end);
                ShareTokenGen shareTokenGen = new ShareTokenGen();
                Common.ShareToken shareTokenRaw = shareTokenGen.genShareToken(String.valueOf(node.getNodeId()), " ", " ");
                String shareToken = getString(R.string.shareTokenL) + shareTokenRaw.getL() + "\n"
                        + getString(R.string.shareTokenJid) + shareTokenRaw.getJId() + "\n"
                        + getString(R.string.shareTokenKid) + shareTokenRaw.getKId() + "\n"
                        + getString(R.string.shareTokenFileId) + shareTokenRaw.getFileId() + "\n"
                        + fileNameAndContent;
                byte[] msg = shareToken.getBytes(StandardCharsets.UTF_8);
                ByteBuffer fullMsg = ByteBuffer.allocate(msg.length + start.length + end.length);
                fullMsg.put(start).put(msg).put(end);
                curConnect.write(fullMsg.array(), new TransferProgressListener() {
                    @Override
                    public void transfering(int progress) {

                    }

                    @Override
                    public void transferSuccess(byte[] bytes) {
                        ToastUtils.showShortToastCenter("传输成功");
                    }

                    @Override
                    public void transferFailed(Exception exception) {
                        ToastUtils.showShortToastCenter("传输失败" + exception.getLocalizedMessage());
                    }
                });
                break;
            }
        }
    }
}
