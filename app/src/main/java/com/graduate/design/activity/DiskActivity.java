package com.graduate.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.protobuf.ByteString;
import com.graduate.design.R;
import com.graduate.design.proto.Common;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.DialogUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.configs.PathSelectorConfig;
import com.molihuan.pathselector.entity.FileBean;
import com.molihuan.pathselector.fragment.BasePathSelectFragment;
import com.molihuan.pathselector.fragment.impl.PathSelectFragment;
import com.molihuan.pathselector.listener.CommonItemListener;
import com.molihuan.pathselector.utils.MConstants;
import com.molihuan.pathselector.utils.Mtools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// public class HomeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
public class DiskActivity extends AppCompatActivity {

    // 当前磁盘页面的父id，添加文件或文件夹时都使用该id
    private Long nodeId;
    private String token;
    private ImageButton gotoMineButton;
    private ListView fileList;
    private UserService userService;
    private ImageButton backImageButton;
    private Button backButton;
    private Button searchButton;
    private ImageButton addFileOrDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_color));

        // 获取当前token值
        token = GraduateDesignApplication.getToken();

        gotoMineButton = findViewById(R.id.goto_mine_btn);
        gotoMineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到我的页面
                Intent intent = new Intent(DiskActivity.this, MineActivity.class);
                ActivityJumpUtils.jumpActivity(DiskActivity.this, intent, 100L, false);
            }
        });

        // 是否显示返回按钮
        Boolean showBack = getIntent().getBooleanExtra("showBack", false);
        backImageButton = findViewById(R.id.back_image_btn_disk);
        backButton = findViewById(R.id.back_btn_disk);
        if(showBack){
            backImageButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
        }

        // 点击返回按钮，返回文件页面，这里把按钮分成了文字和图标按钮
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 是否显示搜索框
        Boolean showSearch = getIntent().getBooleanExtra("showSearch", true);
        searchButton = findViewById(R.id.search_btn);
        if(!showSearch){
            searchButton.setVisibility(View.GONE);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiskActivity.this, SearchActivity.class);
                ActivityJumpUtils.jumpActivity(DiskActivity.this, intent, 100L, false);
            }
        });

        // 上传文件或添加文件夹
        addFileOrDir = findViewById(R.id.add_file_or_dir);
        addFileOrDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(addFileOrDir);
            }
        });

        // 拿到文件列表
        nodeId = getIntent().getLongExtra("nodeId", GraduateDesignApplication.getUserInfo().getRootId());
        userService = new UserServiceImpl();

        fileList = findViewById(R.id.show_files);

        freshNodeList();
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

    // 展示添加文件夹或上传文件的菜单栏
    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.functions, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 点击了上传文件按钮
                if("Upload File".contentEquals(item.getTitle())){
                    showPhoneFileList();
                }
                // 点击了创建文件夹按钮
                else if("Create Dir".contentEquals(item.getTitle())){
                    DialogUtils.showDialog(DiskActivity.this, userService, nodeId, token);
                }
                return false;
            }
        });

        popupMenu.show();
    }

    // 展示上传文件列表
    private void showPhoneFileList(){
        //Constants.BUILD_ACTIVITY为ACTIVITY模式
        PathSelectFragment selector = PathSelector.build(this, MConstants.BUILD_ACTIVITY)
                .setRequestCode(635)
                .setMorePopupItemListeners(
                        new CommonItemListener("SelectAll") {
                            @Override
                            public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {

                                pathSelectFragment.selectAllFile(true);

                                return false;
                            }
                        },
                        new CommonItemListener("DeselectAll") {
                            @Override
                            public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                pathSelectFragment.selectAllFile(false);
                                return false;
                            }
                        }
                )
                .setHandleItemListeners(
                        new CommonItemListener("OK") {
                            @Override
                            public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                StringBuilder sb = new StringBuilder();
                                for (FileBean fileBean : selectedFiles) {
                                    FileInputStream fs;
                                    try {
                                        String path = fileBean.getPath();
                                        fs = new FileInputStream(path);
                                        byte[] buf = new byte[1024];
                                        int hasRead = 0;
                                        while((hasRead = fs.read(buf)) != -1){
                                            sb.append(new String(buf,0, hasRead));
                                        }
                                        // 文件内容加密后上传 TODO
                                        int res = userService.uploadFile(fileBean.getName(), nodeId, indexList(sb.toString()),
                                                ByteString.copyFromUtf8(sb.toString()), ByteString.copyFromUtf8("123"), token);

                                        if(res==1) {
                                            ToastUtils.showShortToastCenter("上传文件失败" + fileBean.getName());
                                        }

                                        if(res==0) {
                                            ToastUtils.showShortToastCenter("上传文件成功" + fileBean.getName());
                                        }

                                        sb.delete(0, sb.length());

                                        fs.close();
                                    } catch (FileNotFoundException e) {
                                        throw new RuntimeException(e);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                return false;
                            }
                        },
                        new CommonItemListener("cancel") {
                            @Override
                            public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                pathSelectFragment.openCloseMultipleMode(false);
                                return false;
                            }
                        }
                )
                .show();
    }

    private List<String> indexList(String content) {
        List<String> res = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        for(int i=0;i<content.length();i++){
            char temp = content.charAt(i);
            if((temp>='a' && temp<='z') || (temp>='A' && temp<='Z') || (temp>='0' && temp<='9')){
                word.append(temp);
            }
            else {
                if(word.length()>0){
                    res.add(word.toString());
                    word.delete(0, word.length());
                }
            }
        }
        if(word.length()>0) res.add(word.toString());
        return res;
    }


    private void freshNodeList(){
        List<Common.Node> subNodes = putDirBeforeFile(userService.getNodeList(nodeId, token));

        List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < subNodes.size(); i++) {
            Map<String, Object> item = new HashMap<>();
            Common.Node node = subNodes.get(i);

            item.put("nodeType", node.getNodeType() == Common.NodeType.Dir ?
                    R.drawable.folder : R.drawable.file);
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
        SimpleAdapter myAdapter = new SimpleAdapter(this,
                listItem, R.layout.activity_file_item, new String[]{"nodeType", "topName", "subTime"},
                new int[]{R.id.node_type, R.id.top_name, R.id.sub_time});

        fileList.setAdapter(myAdapter);

        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Common.Node clickedNode = subNodes.get(i);
                // 如果点击的是文件，则查看文件的详细内容
                if(clickedNode.getNodeType()== Common.NodeType.File){
                    // 拿取文件内容和对应密钥
                    String[] fileContentKey = userService.getNodeContent(clickedNode.getNodeId(), token);

                    if(fileContentKey==null) {
                        ToastUtils.showShortToastCenter("读取文件出错");
                        return;
                    }

                    // 解密 TODO
                    String fileContent = fileContentKey[0];

                    Intent intent = new Intent(DiskActivity.this, FileContentActivity.class);
                    intent.putExtra("fileName", clickedNode.getNodeName());
                    intent.putExtra("fileContent", fileContent);
                    ActivityJumpUtils.jumpActivity(DiskActivity.this, intent, 100L, false);
                }

                // 如果点击的是文件夹，则展开文件夹，未测试
                else {
                    Intent intent = new Intent(DiskActivity.this, DiskActivity.class);
                    intent.putExtra("nodeId", clickedNode.getNodeId());
                    intent.putExtra("showBack", true);
                    intent.putExtra("showSearch", false);
                    ActivityJumpUtils.jumpActivity(DiskActivity.this, intent, 100L, false);
                }
            }
        });
    }
}
