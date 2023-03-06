package com.graduate.design.entity;

import java.util.Date;

public class FileInfo {
    private String nodeType;
    private Long nodeId;
    private String nodeName;
    private Date createTime;
    private Date updateTime;

    public FileInfo(String nodeType, Long nodeId, String nodeName, Date createTime, Date updateTime) {
        this.nodeType = nodeType;
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
