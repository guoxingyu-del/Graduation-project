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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.proto.Common;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;
import com.graduate.design.view.ClearEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    private void getComponentsById(View view){
        cancelButton = view.findViewById(R.id.cancel_btn);
        searchText = view.findViewById(R.id.search_text);
        listView = view.findViewById(R.id.show_search_files);
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
                showSearchFileList(actionId, true);
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
        return false;
    }

    private void showSearchFileList(int actionId, Boolean search){
        if(actionId == EditorInfo.IME_ACTION_SEARCH){
            searchNodes = userService.searchFile(searchText.getText().toString(), token);
            // 展示搜索结果
            List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
            for (int j = 0; j < searchNodes.size(); j++) {
                Map<String, Object> item = new HashMap<>();
                Common.Node node = searchNodes.get(j);

                item.put("nodeType", R.drawable.file);
                item.put("topName", node.getNodeName());

                // 将时间转换成yyyy-MM-dd HH:MM:ss格式的24小时制
                Long updateTime = node.getUpdateTime();
                Date date = new Date();
                //格式里的时如果用hh表示用12小时制，HH表示用24小时制。MM必须是大写!
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date.setTime(updateTime*1000);//java里面应该是按毫秒
                item.put("subTime", sdf.format(date));
                listItem.add(item);
            }

            //创建一个simpleAdapter
            SimpleAdapter myAdapter = new SimpleAdapter(context,
                    listItem, R.layout.activity_file_item, new String[]{"nodeType", "topName", "subTime"},
                    new int[]{R.id.node_type, R.id.top_name, R.id.sub_time});

            listView.setAdapter(myAdapter);
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
