package com.example.gradwork_backend.enums;

public enum ResultEnums {
    SUCCESS(200, "成功"),
    ERROR_NO_RECORD(400, "无数据记录"),
    ERROR_UNKNOWN(500, "未知错误"),
    ENTITY_NOT_EXIST(20001, "用户不存在"),
    ENTITY_IS_EXIST(20002, "用户已存在"),
    ACCOUNT_ERROR(10004, "账号或密码错误");

    ResultEnums(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public Integer Code() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String Desc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
