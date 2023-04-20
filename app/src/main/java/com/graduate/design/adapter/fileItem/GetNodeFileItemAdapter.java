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
import com.graduate.design.delete.DeleteProtocol;
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

                        /*// 删除文件的重要逻辑就是仿照添加文件
                        // 单个文件和文件夹分开讨论 文件夹需要将下面的文件全部删除，防止搜索的时候产生问题
                        // 文件名也得修改一下
                        Queue<Common.Node> queueNode = new LinkedList<>();
                        Queue<Long> queueParentId = new LinkedList<>();
                        queueNode.add(fileBean);
                        queueParentId.add(parentId);
                        int res = 0;
                        // 将文件夹和文件全部删除
                        while (!queueNode.isEmpty()) {
                            Common.Node node = queueNode.poll();
                            if (node.getNodeType() == Common.NodeType.File) {
                                String nodeName = StringUtils.getRandomName(random.nextInt(10) + 1,".txt");
//                                res += deleteFile(node.getNodeName(), queueParentId.poll(), node.getNodeId());
                                res += deleteFile(nodeName, queueParentId.poll(), node.getNodeId());
                            }
                            if (node.getNodeType() == Common.NodeType.Dir) {
                                // 这里还需要将其下面的所有结点找出来
                                List<Common.Node> nodeList = userService.getDir(node.getNodeId(), token);
                                for (Common.Node n : nodeList) {
                                    queueNode.add(n);
                                    queueParentId.add(node.getNodeId());
                                }
                                String nodeName = StringUtils.getRandomName(random.nextInt(10) + 1,"");
                                res += deleteDir(nodeName, queueParentId.poll(), node.getNodeId());
                            }
                        }

                        if (res == 0) {
                            ToastUtils.showShortToastCenter("删除文件成功" + fileBean.getNodeName());
                            GraduateDesignApplication.getAllNodeList().get(parentId).setUpdate(true);
                        } else {
                            ToastUtils.showShortToastCenter("删除文件失败" + fileBean.getNodeName());
                        }
                    }

                    public int deleteFile(String nodeName, Long parentId, Long nodeId) {
                        Long fileId = userService.getNodeId(token);
                        // 随机生成文件内容，这部分可以将文件随机生成的方式再修改一下
                        // 这个文件内容有问题，应该为a-z这种
//                        String fileContent = new String(ByteUtils.getRandomBytes(new Random().nextInt(128) + 6));
                        String fileContent = StringUtils.getRandomCharSet(random.nextInt(128) + 1);
                        String encryptContent = FileUtils.bytes2Base64(encryptionService.encryptByAES256(fileContent, fileSecret));
                        if (encryptContent == null) encryptContent = "";
                        List<Common.indexToken> indexTokens = FileUtils.indexList(fileContent, fileId);
                        String biIndexString = FileUtils.bytes2Base64(GraduateDesignApplication.getBiIndex().writeObject());
                        String fileSecret = "123";
                        // 文件名也得生成一个
                        return userService.uploadFile(nodeName, parentId, indexTokens,
                                ByteString.copyFrom(encryptContent.getBytes(StandardCharsets.UTF_8)), biIndexString, fileId, token,
                                fileSecret);
                    }

                    public int deleteDir(String nodeName, Long parentId, Long nodeId) {
                        Long dirId = userService.getNodeId(token);
                        return userService.createDir(nodeName, parentId, token);
                    }
                });*/
                    }
                });
                popupMenu.show();
            }
        });
        return convertView;
    }
}
