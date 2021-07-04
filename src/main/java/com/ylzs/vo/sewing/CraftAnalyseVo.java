package com.ylzs.vo.sewing;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "CraftAnalyseVo",description = "工序报表返回值")
public class CraftAnalyseVo implements Serializable {
    private static final long serialVersionUID = 114232802765165451L;
    /**
     * 行号
     */
    @ApiModelProperty(value = "行号")
    private Integer rowNo;
    /**
     * 是否是新增
     */
    @ApiModelProperty(value = "是否是新增 true是 false否")
    private Boolean isNew;
    /**
     * 工序代码
     */
    @ApiModelProperty(value = "工序编码")
    private String craftCode;
    /**
     * 工序描述
     */
    @ApiModelProperty(value = "工序描述")
    private String craftDescript;
    /**
     * 机器名称
     */
    @ApiModelProperty(value = "机器名称")
    private String machineName;

    /**
     * 是否有图片
     */
    @ApiModelProperty(value = "是否有图片 true是 false否")
    private Boolean isPic;

    /**
     * 是否有视频
     */
    @ApiModelProperty(value = "是否有视频 true是 false否")
    private Boolean isVideo;

    /**
     * 标准时间
     */
    @ApiModelProperty(value = "标准时间")
    private BigDecimal standardTime;

    /**
     * 标准单价
     */
    @ApiModelProperty(value = "标准单价")
    private BigDecimal standardPrice;

    /**
     * 工序来源
     */
    @ApiModelProperty(value = "工序来源")
    private String craftSource;

    /**
     * 外部代码
     */
    @ApiModelProperty(value = "部件工艺/智库款工艺/款式工艺路线编码")
    private String outCode;
    /**
     * 外部名称
     */
    @ApiModelProperty(value = "部件工艺/智库款/款式名称")
    private String outName;

    /**
     * 外部描述
     */
    @ApiModelProperty(value = "部件工艺/智库款/款式描述")
    private String outDescript;

    /**
     * 定制订单号
     */
    @ApiModelProperty(value = "定制订单号")
    private String customOrderNo;
    /**
     * 工单号
     */
    @ApiModelProperty(value = "工单号")
    private String productionTicketNo;


    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String statusName;

    /**
     * 工艺品类名称
     */
    @ApiModelProperty(value = "工艺品类")
    private String craftCategoryName;

    /**
     * 结构部件名称
     */
    @ApiModelProperty(value = "结构部件")
    private String craftPartName;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 图片URL
     */
    @ApiModelProperty(value = "图片URL 多个逗号分隔")
    private String picUrls;

    /**
     * 视频URL
     */
    @ApiModelProperty(value = "视频URL 多个逗号分隔")
    private String videoUrls;

    /**
     * 图片视频url
     */
    @ApiModelProperty(value = "图片视频URL 数组格式")
    private String[] pictures;



}