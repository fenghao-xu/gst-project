package com.ylzs.entity.pi;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "capp_pi_sendpilog")
public class CappPiSendpilog {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发送类型
     */
    @TableField(value = "send_type")
    private String sendType;

    /**
     * 目标URL
     */
    @TableField(value = "dest_url")
    private String destUrl;

    /**
     * 发送的数据
     */
    @TableField(value = "data")
    private String data;

    /**
     * 返回的数据
     */
    @TableField(value = "return_msg")
    private String returnMsg;

    /**
     * 异常数据
     */
    @TableField(value = "fail_msg")
    private String failMsg;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;
}