package com.graduate.design.adapter.fileItem;

import android.app.Activity;
import android.content.Context;
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
import com.graduate.design.fragment.BtServerFragment;
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
    private BtServerFragment fragment;


    public ReceiveFileItemAdapter(Context context, Activity activity, BtServerFragment fragment) {
        this.context = context;
        this.activity = activity;
        this.fragment = fragment;
        list = new ArrayList<>();
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
        topName.setText(node[0]);

        // 设置发送者名称
        TextView subInfo = convertView.findViewById(R.id.sub_info);
        // TODO 获取发送设备名
        subInfo.setText("设备名");

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
                        if("Receive".contentEquals(item.getTitle())){
                            fragment.gotoReceive(node);
                            delete();
                        }
                        // 点击了删除文件按钮
                        else if("Delete".contentEquals(item.getTitle())){
                            ToastUtils.showShortToastCenter("点击了删除文件按钮");
                            delete();
                        }
                        return false;
                    }

                    // 从接收列表中删除该文件
                    private void delete(){
                        list.remove(node);
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
