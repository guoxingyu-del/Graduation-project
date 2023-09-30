package com.graduate.design.adapter.fileItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.graduate.design.R;
import com.graduate.design.proto.Common;
import com.graduate.design.utils.DateTimeUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
* 可复用的文件列表适配器，目前子类有 GetNodeFileItemAdapter、ChooseDirFileItemAdapter 和 ShareFileItemAdapter
* 该适配器的 list 属性由文件节点组成
* */

public class BaseFileItemAdapter extends BaseAdapter {
    protected Context context;
    protected List<Common.Node> list;
    private int layoutId;

    public BaseFileItemAdapter(Context context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
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
            convertView = LayoutInflater.from(context).inflate(layoutId, null);
        }

        ImageView imageView = convertView.findViewById(R.id.node_type);
        TextView topName = convertView.findViewById(R.id.top_name);
        TextView subInfo = convertView.findViewById(R.id.sub_info);

        Common.Node node = list.get(position);

        int nodeType = node.getNodeType() == Common.NodeType.File ? R.drawable.file : R.drawable.folder;
        imageView.setImageResource(nodeType);
        topName.setText(node.getNodeName());
        subInfo.setText(DateTimeUtils.timerToString(node.getUpdateTime()*1000));

        return convertView;
    }

    public void addFileItem(Common.Node node){
        if(!list.contains(node)){
            list.add(node);
            notifyDataSetChanged();
        }
    }

    public void addAllFileItem(List<Common.Node> nodes){
        if(nodes == null) return;
        for(Common.Node node : nodes){
            addFileItem(node);
        }
    }

    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }
}
