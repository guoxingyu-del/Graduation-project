package com.graduate.design.adapter.fileItem;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.graduate.design.R;
import com.graduate.design.proto.Common;
import com.graduate.design.utils.DateTimeUtils;
import com.graduate.design.utils.ToastUtils;

/*
 * 继承自 BaseFileItemAdapter
 * 在登录成功时，展示当前用户文件列表
 * 列表项由 文件类型、文件名、文件更新日期、右侧“更多”按钮，四部分组成
 * 列表项使用 R.layout.item_file 布局文件
 * */

public class GetNodeFileItemAdapter extends BaseFileItemAdapter {

    public GetNodeFileItemAdapter(Context context, int layoutId) {
        super(context, layoutId);
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
                        if("Delete".contentEquals(item.getTitle())){
                            ToastUtils.showShortToastCenter("点击了删除文件按钮");
                            delete();
                        }
                        return false;
                    }

                    // TODO 删除文件逻辑
                    public void delete(){

                    }
                });

                popupMenu.show();
            }
        });

        return convertView;
    }
}
