package com.example.gradwork_backend.vo;

public class ResultVo<T> {
    private Integer code;
    private String msg;
    private T data;

    private ResultVo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private ResultVo(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResultVo getFail(Integer code, String msg) {
        return new ResultVo(code, msg);
    }

    public static <T> ResultVo getFail(Integer code, String msg, T data) {
        return new ResultVo<>(code, msg, data);
    }

    public static ResultVo getSuccess(Integer code, String msg) {
        return new ResultVo(code, msg);
    }

    public static <T> ResultVo getSuccess(Integer code, String msg, T data) {
        return new ResultVo<>(code, msg, data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
