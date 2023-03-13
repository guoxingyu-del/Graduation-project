package com.graduate.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.graduate.design.R;
import com.graduate.design.proto.Common;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;
import com.graduate.design.view.ClearEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener,
        TextView.OnEditorActionListener, AdapterView.OnItemClickListener {
    private Button cancelButton;
    private ListView listView;
    private ClearEditText searchText;
    private UserService userService;
    private String token;
    private List<Common.Node> searchNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 初始化页面
        InitViewUtils.initView(this);
        // 初始化数据
        initData();
        // 拿到页面元素
        getComponentsById();
        // 设置监听事件
        setListeners();
    }

    private void initData(){
        userService = new UserServiceImpl();
        token = GraduateDesignApplication.getToken();
    }

    private void getComponentsById(){
        cancelButton = findViewById(R.id.cancel_btn);
        searchText = findViewById(R.id.search_text);
        listView = findViewById(R.id.show_search_files);
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
        finish();
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
            SimpleAdapter myAdapter = new SimpleAdapter(SearchActivity.this,
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

        Intent intent = new Intent(SearchActivity.this, FileContentActivity.class);
        intent.putExtra("fileName", clickedNode.getNodeName());
        intent.putExtra("fileContent", fileContent);
        ActivityJumpUtils.jumpActivity(SearchActivity.this, intent, 100L, false);
    }
}
