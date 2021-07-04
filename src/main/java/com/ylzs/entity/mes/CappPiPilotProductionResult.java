package com.ylzs.entity.mes;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "capp_pi_pilot_production_result")
public class CappPiPilotProductionResult {
    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 款号
     */
    @TableField(value = "style_code")
    private String styleCode;

    /**
     * 工艺路线最后一次拉取时间
     */
    @TableField(value = "last_check_route_time")
    private Date lastCheckRouteTime;

    /**
     * 试产确认时间
     */
    @TableField(value = "confirm_time")
    private Date confirmTime;

    /**
     * 试产反馈信息
     */
    @TableField(value = "feedback_msg")
    private String feedbackMsg;

    /**
     * 反馈图片（多个用逗号分隔）
     */
    @TableField(value = "feedback_url")
    private String feedbackUrl;

    /**
     * 试产作业人（多个用逗号分隔）
     */
    @TableField(value = "worker")
    private String worker;

    /**
     * 数据接收时间
     */
    @TableField(value = "receive_time")
    private Date receiveTime;
}