package com.example.schedulemanagement.base.entity;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/29
 *     desc   : 实体类基类
 * </pre>
 */

public class BaseResponse<T> {
    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
