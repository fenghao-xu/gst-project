package com.ylzs.common.bo;

import java.io.Serializable;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 10:40 2020/3/10
 */
public class ResultObject implements Serializable {
    public static final ResultObject EMPTY = new ResultObject();

    public static ResultObject createErrorInstance(long errorCode, String msg) {
        ResultObject result = new ResultObject(errorCode, msg);
        return result;
    }

    public static ResultObject createSuccessInstance(String msg) {
        ResultObject result = new ResultObject(0, msg);
        return result;
    }

    public static ResultObject createInstance(Object object) {
        ResultObject result = new ResultObject();
        result.data = object;
        return result;
    }

    private Object data;

    private long errorCode = 0;

    private String errorMessage;

    public ResultObject() {
    }

    @Override
    public String toString() {
        return "ResultObject [data=" + data + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
    }

    public ResultObject(long errorCode, String msg) {
        this.errorCode = errorCode;
        this.errorMessage = msg;
    }

    public ResultObject(Object data) {
        this.data = data;
    }

    public long getCurrentTimeMs() {
        return System.currentTimeMillis();
    }

    public Object getData() {
        return data;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setErrorCode(long errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


}
