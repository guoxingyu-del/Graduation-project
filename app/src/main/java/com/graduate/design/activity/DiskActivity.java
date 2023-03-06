package com.graduate.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.graduate.design.R;
import com.graduate.design.proto.Common;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.GraduateDesignApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// public class HomeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
public class DiskActivity extends AppCompatActivity {

    private ImageButton gotoMineButton;
    private ListView fileList;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_color));

        gotoMineButton = findViewById(R.id.goto_mine_btn);
        gotoMineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到我的页面
                Intent intent = new Intent(DiskActivity.this, MineActivity.class);
                ActivityJumpUtils.jumpActivity(DiskActivity.this, intent, 100L, true);
            }
        });

        userService = new UserServiceImpl();
        List<Common.Node> subNodes = putDirBeforeFile(userService.getNode(GraduateDesignApplication.getUserInfo().getRootId(),
                GraduateDesignApplication.getToken()));

        List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < subNodes.size(); i++) {
            Map<String, Object> item = new HashMap<>();
            Common.Node node = subNodes.get(i);

            item.put("nodeType", node.getNodeType() == Common.NodeType.Dir ?
                    R.drawable.folder : R.drawable.file);
            item.put("topName", node.getNodeName());
            item.put("subTime", node.getUpdateTime());
            listItem.add(item);
        }

        //创建一个simpleAdapter
        SimpleAdapter myAdapter = new SimpleAdapter(this,
                listItem, R.layout.activity_file_item, new String[]{"nodeType", "topName", "subTime"},
                new int[]{R.id.node_type, R.id.top_name, R.id.sub_time});

        fileList = findViewById(R.id.show_files);

        fileList.setAdapter(myAdapter);

        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Common.Node clickedNode = subNodes.get(i);
                // 如果点击的是文件，则查看文件的详细内容
                if(clickedNode.getNodeType()== Common.NodeType.File){
                    Intent intent = new Intent(DiskActivity.this, FileContentActivity.class);
                    ActivityJumpUtils.jumpActivity(DiskActivity.this, intent, 100L, false);
                }
            }
        });
    }

    // 把文件夹放到文件前面
    private List<Common.Node> putDirBeforeFile(List<Common.Node> subNodes){
        List<Common.Node> dirs = new ArrayList<>();
        List<Common.Node> files = new ArrayList<>();
        List<Common.Node> res = new ArrayList<>();

        for(int i=0;i<subNodes.size();i++){
            Common.Node node = subNodes.get(i);
            if(node.getNodeType()== Common.NodeType.File)
                files.add(node);
            else dirs.add(node);
        }

        res.addAll(dirs);
        res.addAll(files);
        return res;
    }
}
