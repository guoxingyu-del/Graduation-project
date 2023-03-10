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
import com.graduate.design.utils.ToastUtils;
import com.graduate.design.view.ClearEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private Button cancelButton;
    private ListView listView;

    private ClearEditText searchText;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_color));

        // 取消搜索按钮
        cancelButton = findViewById(R.id.cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userService = new UserServiceImpl();

        searchText = findViewById(R.id.search_text);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    List<Common.Node> searchNodes = userService.searchFile(searchText.getText().toString(), GraduateDesignApplication.getToken());

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

                    listView = findViewById(R.id.show_search_files);

                    listView.setAdapter(myAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Common.Node clickedNode = searchNodes.get(i);
                            // 拿取文件内容和对应密钥
                            String[] fileContentKey = userService.getNodeContent(clickedNode.getNodeId(), GraduateDesignApplication.getToken());

                            if(fileContentKey==null) {
                                ToastUtils.showShortToastCenter("读取文件出错");
                                return;
                            }

                            // 解密 TODO
                            String fileContent = fileContentKey[0];

                            Intent intent = new Intent(SearchActivity.this, FileContentActivity.class);
                            intent.putExtra("fileName", clickedNode.getNodeName());
                            intent.putExtra("fileContent", fileContent);
                            ActivityJumpUtils.jumpActivity(SearchActivity.this, intent, 100L, false);
                        }
                    });
                }
                return false;
            }
        });
    }
}
