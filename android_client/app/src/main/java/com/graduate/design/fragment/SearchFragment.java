package com.graduate.design.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.protobuf.ByteString;
import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.adapter.fileItem.GetNodeFileItemAdapter;
import com.graduate.design.proto.Common;
import com.graduate.design.proto.SearchFile;
import com.graduate.design.proto.SendSearchToken;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.EncryptionServiceImpl;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;
import com.graduate.design.view.ClearEditText;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFragment extends Fragment implements View.OnClickListener,
        TextView.OnEditorActionListener, AdapterView.OnItemClickListener {
    private Button cancelButton;
    private ListView listView;
    private ClearEditText searchText;
    private UserService userService;
    private EncryptionService encryptionService;
    private String token;
    private List<Common.Node> searchNodes;
    private HomeActivity activity;
    private Context context;
    private GetNodeFileItemAdapter fileItemAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
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
        userService = new UserServiceImpl();
        encryptionService = new EncryptionServiceImpl();
        token = GraduateDesignApplication.getToken();
        activity = (HomeActivity) getActivity();
        context = getContext();
        if(fileItemAdapter==null)
            fileItemAdapter = new GetNodeFileItemAdapter(context, R.layout.item_file, -1L);
    }

    private void getComponentsById(View view){
        cancelButton = view.findViewById(R.id.cancel_btn);
        searchText = view.findViewById(R.id.search_text);

        listView = view.findViewById(R.id.show_search_files);
        listView.setAdapter(fileItemAdapter);
    }

    private void setListeners(){
        cancelButton.setOnClickListener(this);
        searchText.setOnEditorActionListener(this);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_btn:
                goBackDisk();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void goBackDisk(){
        activity.getSupportFragmentManager().popBackStack();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (v.getId()){
            case R.id.search_text:
                showSearchFileList(actionId);
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
        return false;
    }

    private void showSearchFileList(int actionId){
        if(actionId == EditorInfo.IME_ACTION_SEARCH){
            fileItemAdapter.clear();
            // 将搜索关键字用主密钥加密
            String keyword = searchText.getText().toString();
            List<Long> res = new ArrayList<>();
            // 分词
            List<String> words = FileUtils.wordSegmentation(keyword);
            for(String word : words) {
                // 构造搜索令牌Tw = (L, Jw)
                SendSearchToken.SearchToken searchToken = encryptionService.getSearchToken(word);
                // 搜索令牌为空，不存在相应文件
                if(searchToken==null) continue;
                // 根据搜索令牌拿到对应的Cw集合
                List<String> Cw = userService.sendSearchToken(searchToken, token);
                // 根据Cw获取节点id
                List<Long> idList = encryptionService.getNodeIdByCw(Cw, word);
                if(res.size()==0) res.addAll(idList);
                else res.retainAll(idList);
            }
            if(res.size()==0) return;
            // 获取查询到的节点
            searchNodes = userService.searchFile(res, token);
            fileItemAdapter.addAllFileItem(searchNodes);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.show_search_files:
                showFileContent(position);
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void showFileContent(int position){
        Common.Node clickedNode = searchNodes.get(position);
        // 拿取文件内容
        String[] fileContentSecret = userService.getNodeContent(clickedNode.getNodeId(), GraduateDesignApplication.getToken());
        if(fileContentSecret==null) {
            ToastUtils.showShortToastCenter("读文件出错");
            return;
        }

        String fileContent = fileContentSecret[0];
        String secret = fileContentSecret[1];

        if(fileContent=="" && secret=="") {
            ToastUtils.showShortToastCenter("分享源文件已被删除");
            return;
        }

        // 提取文件类型和文件内容
        int pos = fileContent.indexOf('\n');

        FileContentFragment fragment = new FileContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("fileType", fileContent.substring(0,pos));
        bundle.putString("fileName", clickedNode.getNodeName());
        bundle.putString("fileContent", fileContent.substring(pos+1));
        fragment.setArguments(bundle);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

}
