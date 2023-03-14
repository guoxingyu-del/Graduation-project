package com.graduate.design.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.protobuf.ByteString;
import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.proto.Common;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.DialogUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.entity.FileBean;
import com.molihuan.pathselector.fragment.BasePathSelectFragment;
import com.molihuan.pathselector.fragment.impl.PathSelectFragment;
import com.molihuan.pathselector.listener.CommonItemListener;
import com.molihuan.pathselector.utils.MConstants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiskFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private ListView fileList;
    private ImageButton backImageButton;
    private Button backButton;
    private Button searchButton;
    private ImageButton addFileOrDir;
    private UserService userService;
    // 当前磁盘页面的id，添加文件或文件夹时都使用该id
    private Long nodeId;
    private String token;
    private List<Common.Node> subNodes;
    private HomeActivity activity;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_disk, container, false);
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
        // 设置是否显示返回按钮和搜索框
        setShowBackButtonAndSearchText();
        // 设置当前节点下的文件列表
        setNodeList();
    }

    private void initData(){
        token = GraduateDesignApplication.getToken();
        activity = (HomeActivity) getActivity();
        context = getContext();
        userService = new UserServiceImpl();
        // 拿到当前节点id
        if(getArguments()==null) nodeId = GraduateDesignApplication.getUserInfo().getRootId();
        else nodeId = getArguments().getLong("nodeId");
    }

    private void getComponentsById(View view){
        backImageButton = view.findViewById(R.id.back_image_btn_disk);
        backButton = view.findViewById(R.id.back_btn_disk);
        searchButton = view.findViewById(R.id.search_btn);
        addFileOrDir = view.findViewById(R.id.add_file_or_dir);
        fileList = view.findViewById(R.id.show_files);
    }

    private void setListeners(){
        backImageButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        addFileOrDir.setOnClickListener(this);
        fileList.setOnItemClickListener(this);
    }

    private void setShowBackButtonAndSearchText(){
        // 不在根节点，显示后退按钮，并隐藏搜索按钮
        if(nodeId != GraduateDesignApplication.getUserInfo().getRootId()){
            backImageButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.GONE);
        }
        else {
            backImageButton.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            searchButton.setVisibility(View.VISIBLE);
        }
    }

    private void setNodeList(){
        subNodes = putDirBeforeFile(userService.getNodeList(nodeId, token));

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
        SimpleAdapter myAdapter = new SimpleAdapter(context,
                listItem, R.layout.activity_file_item, new String[]{"nodeType", "topName", "subTime"},
                new int[]{R.id.node_type, R.id.top_name, R.id.sub_time});

        fileList.setAdapter(myAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn_disk:
            case R.id.back_image_btn_disk:
                goBackParent();
                break;
            case R.id.search_btn:
                gotoSearch();
                break;
            case R.id.add_file_or_dir:
                popupAddFileOrDir();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void goBackParent(){
        activity.getSupportFragmentManager().popBackStack();
    }

    private void gotoSearch(){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, new SearchFragment())
                .addToBackStack(null)
                .commit();
    }

    // 展示添加文件夹或上传文件的菜单栏
    private void popupAddFileOrDir(){
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(context, addFileOrDir);
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
                    DialogUtils.showDialog(context, userService, nodeId, token);
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
                                        // TODO 文件内容加密后上传
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
        // 如果点击的是文件，则查看文件的详细内容
        if(clickedNode.getNodeType()== Common.NodeType.File){
            // 拿取文件内容和对应密钥
            String[] fileContentKey = userService.getNodeContent(clickedNode.getNodeId(), token);

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

        // 如果点击的是文件夹，则展开文件夹
        else {
            DiskFragment fragment = new DiskFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("nodeId", clickedNode.getNodeId());
            fragment.setArguments(bundle);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
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
