package com.graduate.design.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.protobuf.ByteString;
import com.graduate.design.R;
import com.graduate.design.activity.BtClientActivity;
import com.graduate.design.activity.BtServerActivity;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.adapter.fileItem.GetNodeFileItemAdapter;
import com.graduate.design.delete.DeleteProtocol;
import com.graduate.design.proto.Common;
import com.graduate.design.proto.FileUpload;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.EncryptionServiceImpl;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.entity.FileBean;
import com.molihuan.pathselector.entity.FontBean;
import com.molihuan.pathselector.fragment.BasePathSelectFragment;
import com.molihuan.pathselector.fragment.impl.PathSelectFragment;
import com.molihuan.pathselector.listener.CommonItemListener;
import com.molihuan.pathselector.listener.FileItemListener;
import com.molihuan.pathselector.utils.MConstants;
import com.molihuan.pathselector.utils.Mtools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class DiskFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private ListView fileList;
    private ImageButton backImageButton;
    private Button backButton;
    private Button searchButton;
    private ImageButton addFileOrDir;
    private ImageButton searchDeviceButton;
    private UserService userService;
    private EncryptionService encryptionService;
    // 当前磁盘页面的id，添加文件或文件夹时都使用该id
    private Long nodeId;
    private String token;
    private List<Common.Node> subNodes;
    private GetNodeFileItemAdapter fileItemAdapter;
    private HomeActivity activity;
    private Context context;
    private Dialog dialog;

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
        encryptionService = new EncryptionServiceImpl();
        // 拿到当前节点id
        if(getArguments()==null) nodeId = GraduateDesignApplication.getUserInfo().getRootId();
        else nodeId = getArguments().getLong("nodeId");
        fileItemAdapter = new GetNodeFileItemAdapter(context, R.layout.item_file, nodeId);
    }

    private void getComponentsById(View view){
        backImageButton = view.findViewById(R.id.back_image_btn_disk);
        backButton = view.findViewById(R.id.back_btn_disk);
        searchButton = view.findViewById(R.id.search_btn);
        addFileOrDir = view.findViewById(R.id.add_file_or_dir);
        fileList = view.findViewById(R.id.show_files);
        fileList.setAdapter(fileItemAdapter);
        searchDeviceButton = view.findViewById(R.id.search_device_btn);
    }

    private void setListeners(){
        backImageButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        searchDeviceButton.setOnClickListener(this);
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
        fileItemAdapter.clear();
        subNodes = FileUtils.putDirBeforeFile(userService.getNodeList(nodeId, token));
        fileItemAdapter.addAllFileItem(subNodes);
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
            case R.id.search_device_btn:
                popupJoinOrHostSession();
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
                if(getString(R.string.upload_file).contentEquals(item.getTitle())){
                    showPhoneFileList();
                }
                // 点击了创建文件夹按钮
                else if(getString(R.string.create_dir).contentEquals(item.getTitle())){
                    // DialogUtils.showDialog(context, userService, nodeId, token);
                    showDialog();
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
                .setTitlebarBG(getResources().getColor(R.color.common_orange))
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
                                        // 将key2作为密钥加密文件内容
                                        byte[] fileSecret = GraduateDesignApplication.getKey2();
                                        // 将加密结果转为Base64编码
                                        String encryptContent = FileUtils.bytes2Base64(encryptionService.encryptByAES256(sb.toString(), fileSecret));
                                        if(encryptContent == null) encryptContent = "";

                                        // 先从服务器拿到文件id
                                        Long fileId = userService.getNodeId(token);

                                        List<FileUpload.indexToken> indexTokens = FileUtils.indexList(sb.toString(), fileId);
                                        // 同步上传biIndex进行更新
                                        String biIndexString = FileUtils.bytes2Base64(GraduateDesignApplication.getBiIndex().writeObject());

                                        // 这个nodeId的形式的改，得改成密文对的形式，初次之外，其中share中的uploadFile也需要进行修改
                                        // 还需要上传username
                                        int res = userService.uploadFile(fileBean.getName(), nodeId, indexTokens,
                                                ByteString.copyFrom(encryptContent.getBytes(StandardCharsets.UTF_8)), biIndexString, fileId, token,
                                                DeleteProtocol.idOpPairCipherGen(GraduateDesignApplication.getKey1(),
                                                        String.valueOf(fileId), "add"), GraduateDesignApplication.getUsername());

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
                                // 更新文件列表
                                setNodeList();
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



    private void showDialog() {
        //自定义dialog显示布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog, null);
        //自定义dialog显示风格
        dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(inflate);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        dialog.show();

        EditText createDirName = inflate.findViewById(R.id.create_dir_name);
        Button dialogCancelButton = inflate.findViewById(R.id.dialog_cancel);
        Button dialogOKButton = inflate.findViewById(R.id.dialog_ok);

        // 点击了ok按钮
        dialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
                String dirName = createDirName.getText().toString();
                // 创建文件夹也同样需要和创建文件一样，修改传递上去的东西，id_op密文对，然username
                // 同时这个dir的id也只能够在本地进行生成了
                Long dirId = userService.getNodeId(token);
                // 将相关信息保存进去
                int res = userService.createDir(dirName, nodeId, token, dirId,
                        DeleteProtocol.idOpPairCipherGen(GraduateDesignApplication.getKey1(),
                                String.valueOf(dirId), "add"), GraduateDesignApplication.getUsername());
                if(res==0) ToastUtils.showShortToastCenter("添加文件夹成功");
                else ToastUtils.showShortToastCenter("添加文件夹失败");
                // 更新文件列表
                setNodeList();
            }
        });
        // 点击了取消按钮
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
            }
        });
    }

    private void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    private void popupJoinOrHostSession(){
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(context, searchDeviceButton);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.share, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(getString(R.string.join_session).contentEquals(item.getTitle())){
                    ToastUtils.showShortToastCenter("点击了Join Session");
                    gotoJoinSession();
                }
                else if(getString(R.string.host_session).contentEquals(item.getTitle())){
                    ToastUtils.showShortToastCenter("点击了Host Session");
                    gotoHostSession();
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void gotoJoinSession(){
        Intent intent = new Intent(activity, BtClientActivity.class);
        ActivityJumpUtils.jumpActivity(activity, intent, 100L, false);
    }

    private void gotoHostSession(){
        Intent intent = new Intent(activity, BtServerActivity.class);
        ActivityJumpUtils.jumpActivity(activity, intent, 100L, false);
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
            String fileContent = userService.getNodeContent(clickedNode.getNodeId(), token);

            if(fileContent==null) {
                ToastUtils.showShortToastCenter("读取文件出错");
                return;
            }

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
}
