package com.graduate.design.adapter.fileItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.graduate.design.R;
import com.graduate.design.activity.ReceiveActivity;
import com.graduate.design.service.UserService;
import com.graduate.design.service.impl.UserServiceImpl;
import com.graduate.design.utils.ActivityJumpUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/*
* 在进行蓝牙分享过程中，接收方收到的文件列表
* 列表项由 文件类型、文件名、发送设备名、右侧”更多“按钮，四部分组成
* 列表项使用 R.layout.item_file 布局文件
* 不同于 BaseFileItemAdapter，该适配器的 list 属性由String[]数组构成，存放文件名和文件内容
* */

public class ReceiveFileItemAdapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    private List<String[]> list;
    private UserService userService;


    public ReceiveFileItemAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        list = new ArrayList<>();
        userService = new UserServiceImpl();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_file, null);
        }

        // TODO 获取接收的是文件夹还是文件
        ImageView imageView = convertView.findViewById(R.id.node_type);
        imageView.setImageResource(R.drawable.file);

        // 设置文件名称
        String[] node = list.get(position);
        TextView topName = convertView.findViewById(R.id.top_name);
        topName.setText(node[1]);

        // 设置发送者名称
        TextView subInfo = convertView.findViewById(R.id.sub_info);
        // TODO 获取发送设备名
        if ("0".equals(node[9]))
            subInfo.setText("from: " + node[2]);
        else subInfo.setText("create time: " + node[2]);

        // 设置"更多"按钮的点击事件
        ImageButton moreButton = convertView.findViewById(R.id.more_btn);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(context, moreButton);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.receive_file_item_adapter, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // 点击了接收按钮
                        if(context.getString(R.string.receive_file).contentEquals(item.getTitle())){
                            gotoReceive();
                            delete();
                        }
                        // 点击了删除文件按钮
                        else if(context.getString(R.string.delete_file).contentEquals(item.getTitle())){
                            ToastUtils.showShortToastCenter("点击了删除文件按钮");
                            delete();
                        }
                        return false;
                    }

                    // 接收文件
                    private void gotoReceive(){
                        Intent intent = new Intent(activity, ReceiveActivity.class);
                        intent.putExtra("nodeId", GraduateDesignApplication.getUserInfo().getRootId());
                        intent.putExtra("type", node[0]);
                        intent.putExtra("filename", node[1]);
                        intent.putExtra("from", node[2]);
                        intent.putExtra("fileSecret", node[3]);
                        intent.putExtra("shareTokenL", node[4]);
                        intent.putExtra("shareTokenJId", node[5]);
                        intent.putExtra("shareTokenKId", node[6]);
                        intent.putExtra("isShare", node[7]);
                        intent.putExtra("address", node[8]);
                        ActivityJumpUtils.jumpActivity(activity, intent, 100L, false);
                    }

                    // 从接收列表中删除该文件
                    private void delete(){
                        list.remove(node);
                        if ("1".equals(node[9])){
                            userService.deleteShareToken(node[10], GraduateDesignApplication.getToken());
                        }
                        notifyDataSetChanged();
                    }
                });
                popupMenu.show();
            }
        });

        return convertView;
    }

    public void addFileItem(String[] node){
        if(!list.contains(node)){
            list.add(node);
            notifyDataSetChanged();
        }
    }

    public void addAllFileItem(List<String[]> nodes){
        for(String[] node : nodes){
            addFileItem(node);
        }
    }

    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }

    public PopupMenu.OnMenuItemClickListener listener;

    public void setListener(PopupMenu.OnMenuItemClickListener listener) {
        this.listener = listener;
    }
}
