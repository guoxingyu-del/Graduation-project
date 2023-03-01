package com.graduate.design.entity;

import com.graduate.design.proto.UserLogin;

public class UserInfo {

    private Long rootId;
    private String username;
    private String email;

    public UserInfo(Long rootId, String username, String email) {
        this.rootId = rootId;
        this.username = username;
        this.email = email;
    }

    public Long getRootId() {
        return rootId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
