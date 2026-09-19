package com.mes.common;


import com.mes.entity.MaintenanceRecord;

import java.util.List;

public class Result<T> {
    private int code;      // 状态码，成功是200
    private String message;   // 提示信息
    private T data;        // 数据，类型不确定所以用T

    // 构造器私有——不让外面 new
    private Result() {}

    // 成功 + 带数据：Result.ok(某个对象)
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "ok";
        r.data = data;
        return r;
    }

    // 成功 + 不带数据：Result.ok()
    public static <T> Result<T> ok() {
        return ok(null);
    }

    // 失败 + 默认500错误码
    public static <T> Result<T> fail(String message) {
        Result<T> r = new Result<>();
        r.code = 500;
        r.message = message;
        return r;
    }

    // 失败 + 自定义错误码
    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}