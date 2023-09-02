package com.graduate.design.adapter.fileItem;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.protobuf.ByteString;
import com.graduate.design.R;
import com.graduate.design.proto.Common;
import com.graduate.design.proto.FileUpload;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.EncryptionServiceImpl;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ByteUtils;
import com.graduate.design.utils.DateTimeUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.StringUtils;
import com.graduate.design.utils.ToastUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/*
 * 继承自 BaseFileItemAdapter
 * 在登录成功时，展示当前用户文件列表
 * 列表项由 文件类型、文件名、文件更新日期、右侧“更多”按钮，四部分组成
 * 列表项使用 R.layout.item_file 布局文件
 * */

public class GetNodeFileItemAdapter extends BaseFileItemAdapter {
    private final Long parentId;
    private final byte[] fileSecret;
    private final UserService userService;
    private final EncryptionService encryptionService;
    private final String token;

    public GetNodeFileItemAdapter(Context context, int layoutId, Long nodeId) {
        super(context, layoutId);
        this.parentId = nodeId;
        fileSecret = GraduateDesignApplication.getKey2();
        userService = new UserServiceImpl();
        encryptionService = new EncryptionServiceImpl();
        token = GraduateDesignApplication.getToken();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        // 设置"更多"按钮的点击事件
        ImageButton moreButton = convertView.findViewById(R.id.more_btn);
        // 搜索节点不支持删除
        if(parentId==-1) {
            moreButton.setVisibility(View.INVISIBLE);
            return convertView;
        }

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(context, moreButton);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.get_node_file_item_adapter, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // 点击了删除文件按钮
                        if (context.getString(R.string.delete_file).contentEquals(item.getTitle())) {
                            ToastUtils.showShortToastCenter("点击了删除文件按钮");
                            delete();
                        }
                        return false;
                    }

                    // TODO 删除文件逻辑
                    public void delete() {
                        Common.Node fileBean = list.get(position);
                        // 获取所有要删除的文件节点id
                        List<Long> deleteIdList = new ArrayList<>();
                        // 文件夹节点队列
                        Queue<Common.Node> dirQueue = new LinkedList<>();
                        if (fileBean.getNodeType() == Common.NodeType.Dir) {
                            dirQueue.add(fileBean);
                        }
                        deleteIdList.add(fileBean.getNodeId());
                        while (dirQueue.size() > 0) {
                            Common.Node curNode = dirQueue.poll();
                            List<Common.Node> subNodes = userService.getDir(curNode.getNodeId(), token);
                            for (Common.Node node : subNodes) {
                                deleteIdList.add(node.getNodeId());
                                if (node.getNodeType() == Common.NodeType.Dir) dirQueue.add(node);
                            }
                        }

                        int res = userService.deleteFileOrDir(deleteIdList, token);

                        if (res == 0) {
                            ToastUtils.showShortToastCenter("删除文件成功");
                            clear();
                            addAllFileItem(userService.getDir(parentId, token));
                            GraduateDesignApplication.getAllNodeList().get(parentId).setUpdate(true);
                        }
                        else {
                            ToastUtils.showShortToastCenter("删除文件失败");
                        }
                    }
                });
                popupMenu.show();
            }
        });
        return convertView;
    }
}
