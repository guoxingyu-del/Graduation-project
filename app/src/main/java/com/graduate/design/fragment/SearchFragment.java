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

import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.adapter.fileItem.GetNodeFileItemAdapter;
import com.graduate.design.proto.Common;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;
import com.graduate.design.view.ClearEditText;

import java.util.List;

public class SearchFragment extends Fragment implements View.OnClickListener,
        TextView.OnEditorActionListener, AdapterView.OnItemClickListener {
    private Button cancelButton;
    private ListView listView;
    private ClearEditText searchText;
    private UserService userService;
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
        token = GraduateDesignApplication.getToken();
        activity = (HomeActivity) getActivity();
        context = getContext();
        fileItemAdapter = new GetNodeFileItemAdapter(context, R.layout.item_file);
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
            searchNodes = userService.searchFile(searchText.getText().toString(), token);
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
        // 拿取文件内容和对应密钥
        String[] fileContentKey = userService.getNodeContent(clickedNode.getNodeId(), GraduateDesignApplication.getToken());

        if(fileContentKey==null) {
            ToastUtils.showShortToastCenter("读取文件出错");
            return;
        }

        // TODO 解密
        String fileContent = fileContentKey[0];

        FileContentFragment fragment = new FileContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("fileName", clickedNode.getNodeName());
        bundle.putString("fileContent", fileContent);
        fragment.setArguments(bundle);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

}
