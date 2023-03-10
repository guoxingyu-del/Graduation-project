package com.graduate.design.common;

public enum Const {
    CURRENT_USER(0, "current_user"),
    MSG(1, "msg"),
    LOG_FILE_NAME(2, "graduate_design_log.txt"),
    ERROR(3, "error");

    private Integer code;
    private String desc;

    Const(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }



    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
}
