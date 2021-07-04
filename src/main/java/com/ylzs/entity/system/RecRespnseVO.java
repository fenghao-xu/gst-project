package com.ylzs.entity.system;

/**
 * @Author: watermelon.xzx
 * @Description:PI接收记录错误信息
 * @Date: Created in 14:42 2020/3/10
 */
public class RecRespnseVO {
    private String errorCode;

    private String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
