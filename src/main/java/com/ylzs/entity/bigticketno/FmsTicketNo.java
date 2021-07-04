package com.ylzs.entity.bigticketno;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author xuwei
 * @create 2020-06-09 18:58
 * FMS下发过来的工单号
 */
@Data
public class FmsTicketNo {
    /**
     * 主键自增
     */
    @Id
    @Field(type = FieldType.Long)
    private Long id;
    /**
     * 生产工单号
     */
    private String productionTicketNo;

    /**
     * 款式编码
     */
    private String styleCode;

    /**
     * 款色式编码
     */
    private String styleCodeColor;
    /**
     * 款号类型02 基准款、03智库款、05定制款、06 智化生产款、08 普通大货款
     * 06 08 是大货，02 03 04是定制
     */
    private String styleType;

    /**
     * 服装品类编码
     */
    private String clothesCategoryCode;

    /**
     * 品牌
     */
    private String brand;
    /**
     * 工厂编码
     */
    private String factoryCode;
    /**
     * 生产组别
     */
    private String productionCategory;

     /** 订单行号
	 */
    private String orderLineId;
    /**
     * 生产组别名称
     */
    private String productionCategoryName;

    /**
     * 单位
     */
    private String unit;
    /**
     * 定制订单号
     */
    private String mtmOrder;
    /**
     * 件数
     */
    private String number;

    /**
     * 定制款号
     */
    private String customStyleCode;

    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDay;
    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDay;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 维护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String updateUserName;

    private String releaseUserName;

    private String allocationUser;//分配人

    /**
     * 适宜性编码
     */
    private String adaptCode;

}
