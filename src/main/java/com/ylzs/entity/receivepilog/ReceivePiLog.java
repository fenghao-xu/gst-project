package com.ylzs.entity.receivepilog;

import lombok.Data;

import java.util.Date;

/**
 * @Author: watermelon.xzx
 * @Description:接收PI主数据log记录表
 * @Date: Created in 10:05 2020/3/7
 */
@Data
public class ReceivePiLog {
    /**
     *
     */
    private static final long serialVersionUID = -1340378164983116519L;

    /**
     * id
     */
    private Long id;

    /**
     * 接收类型
     */
    private String receiveType;

    /**
     * 发送数据
     */
    private String data;

    /**
     * 总记录数
     */
    private int count;

    /**
     * 返回状态码
     */
    private String returnStatus;

    /**
     * 返回描述
     */
    private String returnDescribe;

    /**
     * 失败数据
     */
    private String failData;

    /**
     * 失败记录数
     */
    private int failCount;

    /**
     * 开始连接时间
     */
    private Date startTime;

    /**
     * 结束连接时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
