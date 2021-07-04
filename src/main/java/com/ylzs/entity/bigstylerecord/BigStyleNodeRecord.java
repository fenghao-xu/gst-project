package com.ylzs.entity.bigstylerecord;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName(value = "big_style_node_record")
public class BigStyleNodeRecord extends SuperEntity {
    /**
     * 款式编码
     */
    @TableField(value = "style_code")
    private String styleCode;

    @TableField(value = "style_desc")
    private String styleDesc;

    /**
     * 服装品类编码
     */
    @TableField(value = "clothes_category_code")
    private String clothesCategoryCode;

    /**
     * 服装品类名称
     */
    @TableField(value = "clothes_category_name")
    private String clothesCategoryName;

    /**
     * 样衣第一次接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @TableField(value = "sample_receive_time1")
    private Date sampleReceiveTime1;

    /**
     * 预估缝制工时（小时）
     */
    @TableField(value = "estimate_sewing_time")
    private BigDecimal estimateSewingTime;

    /**
     * 发送FMS时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @TableField(value = "send_fms_time")
    private Date sendFmsTime;

    /**
     * 样衣第二次接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @TableField(value = "sample_receive_time2")
    private Date sampleReceiveTime2;

    /**
     * 生产级别代码
     */
    @TableField(value = "product_group_code")
    private String productGroupCode;

    /**
     * 生产组别名称
     */
    @TableField(value = "product_group_name")
    private String productGroupName;

    /**
     * 分配人员编码
     */
    @TableField(value = "assign_to_user_code")
    private String assignToUserCode;

    /**
     * 分配人员名称
     */
    @TableField(value = "assign_to_user_name")
    private String assignToUserName;

    /**
     * 预排产开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date scheduleTime;

    /**
     * 预排产交货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;


    /**
     * 分科开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date branchBeginTime;

    /**
     * 分科结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date branchEndTime;

    /**
     * 分科花费（小时）
     */

    private String branchSpend;

    /**
     * 样衣停留时间
     */
    private String sampleStaySpend;


    /**
     * 系统接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @TableField(value = "receive_time")
    private Date receiveTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    @TableField(value = "release_user")
    private String releaseUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "release_time")
    private Date releaseTime;

    /**
     * 款式图片url
     */
    @TableField(value = "style_image_url")
    private String styleImageUrl;

}