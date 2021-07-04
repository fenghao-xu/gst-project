package com.ylzs.common.util;

import com.ylzs.common.constant.MessageConstant;

/**
 * 说明：
 *
 * @author Administrator
 * 2019-10-11 9:56
 */
public class Result<T> {
    /**
     * 代码 1 - 成功，0 - 失败  其他待定
     */
    private final int code;

    /**
     * 消息描述
     */
    private final String msg;

    /**
     * 数据总条数
     */
    private final Integer total;

    /**
     * 数据体
     */
    private final T data;



    private Result(int code, String msg, Long total, T data) {
        super();
        this.code = code;
        this.msg = msg;
        if(total != null) {
            this.total = total.intValue();
        } else {
            this.total = null;
        }

        this.data = data;
    }
    public static Result fail(int code){
        return Result.build(code,MessageConstant.messageMap.get(code));
    }

    public static <T> Result<T> ok() {
        return Result.build(1, null);
    }

    public static <T> Result<T> ok(T data) {
        return Result.build(1, null, null, data);
    }
    public static <T> Result<T> ok(int code,T data) {
        return Result.build(code, null, null, data);
    }

    public static <T> Result<T> ok(T data, Long total) {
        return Result.build(1, null, total, data);
    }

    public static <T> Result<T> build(int code, String msg) {
        return Result.build(code, msg, null, null);
    }

    public static <T> Result<T> build(int code, String msg, Long total, T data) {
        return new Result<>(code, msg, total, data);
    }

    public static <T> Result<T> build(int code, String msg, T data) {
        return Result.build(code, msg, null, data);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public Integer getTotal() { return total; }

//    @Override
//    public String toString() {
//        return "Result [code=" + code + ", msg=" + msg + ", data=" + data + "]";
//    }
}
