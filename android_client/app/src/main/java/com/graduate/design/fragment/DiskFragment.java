package com.graduate.design.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.graduate.design.activity.ShareActivity;
import com.graduate.design.activity.WifiReceiveActivity;
import com.graduate.design.adapter.fileItem.GetNodeFileItemAdapter;
import com.graduate.design.entity.GotNodeList;
import com.graduate.design.proto.Common;
import com.graduate.design.proto.FileUpload;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.EncryptionServiceImpl;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.ByteUtils;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class DiskFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private ListView fileList;
    private ImageButton backImageButton;
    private Button backButton;
    private Button searchButton;
    private ImageButton addFileOrDir;
    private ImageButton searchDeviceButton;
    private ImageButton wifiShareButton;
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
    private List<String> receiverName;

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
        receiverName = new ArrayList<>();
//        fileItemAdapter = new GetNodeFileItemAdapter(context, R.layout.item_file, nodeId);
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
        wifiShareButton = view.findViewById(R.id.share_with_wifi);
    }

    private void setListeners(){
        backImageButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        searchDeviceButton.setOnClickListener(this);
        addFileOrDir.setOnClickListener(this);
        wifiShareButton.setOnClickListener(this);
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
        Map<Long, GotNodeList> map = GraduateDesignApplication.getAllNodeList();
        if(map.containsKey(nodeId) && !map.get(nodeId).getUpdate())
            subNodes = FileUtils.putDirBeforeFile(map.get(nodeId).getNodeList());
        else {
            subNodes = FileUtils.putDirBeforeFile(userService.getDir(nodeId, token));
            map.put(nodeId, new GotNodeList(subNodes, false));
        }
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
            case R.id.share_with_wifi:
                popupShareWithWifi();
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
                .setTitlebarMainTitle(new FontBean("内存文件"))
                .setSelectFileTypes("txt", "jpg", "png")
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

                                        // 根据文件路径判断文件类型
                                        int pos = fileBean.getName().lastIndexOf('.');
                                        String fileType = fileBean.getName().substring(pos+1);
                                        String fileName = fileBean.getName().substring(0,pos);

                                        // 将文件类型添加到文件内容的头部，用作后续判断
                                        sb.append(fileType+"\n");

                                        byte[] buf = new byte[1024];
                                        int hasRead = 0;
                                        while((hasRead = fs.read(buf)) != -1){
                                            sb.append(new String(buf,0, hasRead, StandardCharsets.ISO_8859_1));
                                        }

                                        //long start = System.currentTimeMillis();
                                        // TODO 文件内容加密后上传
                                        // 随机生成文件密钥
                                        byte[] fileSecret = ByteUtils.getRandomBytes(32);
                                        // 文件密钥加密文件内容
                                        String encryptContent = FileUtils.bytes2Base64(encryptionService.encryptByAES256(sb.toString(), fileSecret));
                                        // 将key2作为密钥加密文件密钥
                                        byte[] key2 = GraduateDesignApplication.getKey2();
                                        String encryptSecretKey = FileUtils.bytes2Base64(encryptionService.encryptByAES256(fileSecret, key2));

                                        if(encryptContent == null) encryptContent = "";
                                        if(encryptSecretKey == null) encryptSecretKey = "";

                                        // 先从服务器拿到文件id
                                        Long fileId = userService.getNodeId(token);

                                        // 图片文件以文件名称作为搜索关键字
                                        List<String> words = new ArrayList<>();
                                        if("jpg".equals(fileType) || "png".equals(fileType)) {
                                            words.add(fileName);
                                        }
                                        else words = FileUtils.wordSegmentation(sb.toString().substring(fileType.length()+1));
                                        List<Common.indexToken> indexTokens = FileUtils.indexList(words, fileId);
                                        System.out.println("indexTokens: " + indexTokens.size());
                                        // 同步上传biIndex进行更新
                                        String biIndexString = FileUtils.bytes2Base64(GraduateDesignApplication.getBiIndex().writeObject());

                                        int res = userService.uploadFile(fileBean.getName(), nodeId, indexTokens,
                                                ByteString.copyFrom(encryptContent.getBytes(StandardCharsets.ISO_8859_1)), biIndexString, fileId, token,
                                                encryptSecretKey);
                                        GraduateDesignApplication.getAllNodeList().get(nodeId).setUpdate(true);
                                        if(res==1) {
                                            ToastUtils.showShortToastCenter("上传文件失败: " + fileBean.getName());
                                        }

                                        if(res==0) {
                                            ToastUtils.showShortToastCenter("上传文件成功: " + fileBean.getName());
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
                                GraduateDesignApplication.getAllNodeList().get(nodeId).setUpdate(true);
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
                int res = userService.createDir(dirName, nodeId, token);
                GraduateDesignApplication.getAllNodeList().get(nodeId).setUpdate(true);
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
                    //ToastUtils.showShortToastCenter("点击了Join Session");
                    gotoJoinSession();
                }
                else if(getString(R.string.host_session).contentEquals(item.getTitle())){
                    //ToastUtils.showShortToastCenter("点击了Host Session");
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

    private void popupShareWithWifi(){
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(context, wifiShareButton);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.wifi_share, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(getString(R.string.share_file).contentEquals(item.getTitle())){
                   // ToastUtils.showShortToastCenter("点击了share file");
                    gotoShareFile();
                }
                else if(getString(R.string.receive_file).contentEquals(item.getTitle())){
                  //  ToastUtils.showShortToastCenter("点击了receive file");
                    gotoReceive();
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void selectShareFile(String receiverName){
        Intent intent = new Intent(activity, ShareActivity.class);
        intent.putExtra("nodeId", GraduateDesignApplication.getUserInfo().getRootId());
        intent.putExtra("wifiShare", true);
        intent.putExtra("receiverName", receiverName);
        ActivityJumpUtils.jumpActivity(activity, intent, 100L, false);
    }

    private void gotoReceive(){
        Intent intent = new Intent(activity, WifiReceiveActivity.class);
        ActivityJumpUtils.jumpActivity(activity, intent, 100L, false);
    }

    private void gotoShareFile() {
        //自定义dialog显示布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.receiver_name_dialog, null);
        //自定义dialog显示风格
        dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(inflate);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        dialog.show();

        EditText editReceiverName = inflate.findViewById(R.id.receiver_name);
        Button dialogCancelButton = inflate.findViewById(R.id.receiver_dialog_cancel);
        Button dialogOKButton = inflate.findViewById(R.id.receiver_dialog_ok);

        // 点击了ok按钮
        dialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
                String name = editReceiverName.getText().toString();
                if(name.length()==0) return;
                selectShareFile(name);
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
            String[] fileContentSecret = userService.getNodeContent(clickedNode.getNodeId(), token);

            if(fileContentSecret==null) {
                ToastUtils.showShortToastCenter("读取文件出错");
                return;
            }

            // 现在的文件内容头部包含文件类型
            String fileContentWithType = fileContentSecret[0];
            String secret = fileContentSecret[1];
            if(fileContentWithType=="" && secret=="") {
                ToastUtils.showShortToastCenter("分享源文件已被删除");
                return;
            }

            // 解析文件内容
            int pos = fileContentWithType.indexOf('\n');
            String fileType = fileContentWithType.substring(0, pos);
            String fileContent = fileContentWithType.substring(pos+1);

            FileContentFragment fragment = new FileContentFragment();
            Bundle bundle = new Bundle();
            bundle.putString("fileName", clickedNode.getNodeName());
            bundle.putString("fileContent", fileContent);
            bundle.putString("fileType", fileType);
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
