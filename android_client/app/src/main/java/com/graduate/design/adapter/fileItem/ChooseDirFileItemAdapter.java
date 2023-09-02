package com.graduate.design.adapter.fileItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.graduate.design.R;
import com.graduate.design.utils.DateTimeUtils;

/*
 * 继承自 BaseFileItemAdapter
 * 在选择文件夹存储收到的文件时，展示当前用户文件列表
 * 列表项由 文件类型、文件名、文件更新日期，三部分组成
 * 列表项使用 R.layout.item_file 布局文件，但将右侧“更多”按钮隐藏
 * */

public class ChooseDirFileItemAdapter extends BaseFileItemAdapter {
    public ChooseDirFileItemAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        // 将"更多"按钮隐藏
        ImageButton moreButton = convertView.findViewById(R.id.more_btn);
        moreButton.setVisibility(View.GONE);

        return convertView;
    }
}
