package com.graduate.design.adapter.fileItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.allenliu.classicbt.Connect;
import com.graduate.design.R;
import com.graduate.design.proto.Common;
import com.graduate.design.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

/*
 * 继承自 BaseFileItemAdapter
 * 在进行蓝牙分享过程中，发送方选择要分享的文件时，展示出来的文件列表
 * 列表项由 文件类型、文件名、文件更新日期、右侧复选框，四部分组成
 * 列表项使用 R.layout.item_file_share 布局文件
 * */

public class ShareFileItemAdapter extends BaseFileItemAdapter {
    private Boolean isCheckable = false;

    public ShareFileItemAdapter(Context context, int layoutId){
        super(context, layoutId);
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
        convertView = super.getView(position, convertView, parent);

        CheckBox checkBox = convertView.findViewById(R.id.selected_check_box);

        // 当前可选框是否可见
        if(isCheckable){
            checkBox.setVisibility(View.VISIBLE);
        }
        else checkBox.setVisibility(View.INVISIBLE);
        // 判断当前项是否已被选中
        checkBox.setChecked(((ListView)parent).isItemChecked(position));

        return convertView;
    }

    public void setIsCheckable(Boolean isCheckable){
        this.isCheckable = isCheckable;
        notifyDataSetChanged();
    }
}
