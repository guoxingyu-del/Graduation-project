package com.graduate.design.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.protobuf.ByteString;
import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.adapter.fileItem.ChooseDirFileItemAdapter;
import com.graduate.design.proto.Common;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;

import java.util.List;

public class ReceiveFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private Button backButton;
    private ImageButton backImageButton;
    private ListView listView;
    private Button receiveButton;

    private String token;
    private Context context;
    private UserService userService;
    private Long nodeId;
    private String filename;
    private String fileContent;
    private ChooseDirFileItemAdapter fileItemAdapter;
    private List<Common.Node> subNodes;
    private HomeActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_receive, container, false);
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
        // 设置当前节点下的文件列表
        setNodeList();
    }

    private void initData(){
        token = GraduateDesignApplication.getToken();
        activity = (HomeActivity) getActivity();
        context = getContext();
        userService = new UserServiceImpl();
        // 拿到当前节点id
        if(getArguments()==null){
            ToastUtils.showShortToastCenter("保存信息不完善");
            goBack();
        }
        else{
            nodeId = getArguments().getLong("nodeId");
            filename = getArguments().getString("filename");
            fileContent = getArguments().getString("fileContent");
        }
        fileItemAdapter = new ChooseDirFileItemAdapter(context, R.layout.item_file);
    }

    private void getComponentsById(View view){
        backButton = view.findViewById(R.id.back_btn);
        backImageButton = view.findViewById(R.id.back_image_btn);
        receiveButton = view.findViewById(R.id.receive_btn);

        listView = view.findViewById(R.id.show_files);
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
        subNodes = FileUtils.putDirBeforeFile(userService.getNodeList(nodeId, token));
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
        activity.getSupportFragmentManager().popBackStack();
    }

    private void receive(){
        // 将文件内容和文件标题作为一个新的节点上传
        userService.uploadFile(filename, nodeId, FileUtils.indexList(fileContent), ByteString.copyFromUtf8(fileContent),
                ByteString.copyFromUtf8("123"), token);
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
            ShareFragment fragment = new ShareFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("nodeId", clickedNode.getNodeId());
            bundle.putString("filename", filename);
            bundle.putString("fileContent", fileContent);
            fragment.setArguments(bundle);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
