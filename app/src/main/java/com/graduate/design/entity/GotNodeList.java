package com.graduate.design.entity;

import com.graduate.design.proto.Common;

import java.util.ArrayList;
import java.util.List;

public class GotNodeList {
    private List<Common.Node> nodeList;
    // 文件列表是否更新
    private Boolean isUpdate;

    public GotNodeList(List<Common.Node> nodeList, Boolean isUpdate) {
        this.nodeList = nodeList;
        this.isUpdate = isUpdate;
    }

    public GotNodeList() {
        nodeList = new ArrayList<>();
        isUpdate = true;
    }

    public List<Common.Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Common.Node> nodeList) {
        this.nodeList = nodeList;
    }

    public Boolean getUpdate() {
        return isUpdate;
    }

    public void setUpdate(Boolean update) {
        isUpdate = update;
    }
}
