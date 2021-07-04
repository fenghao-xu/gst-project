package com.ylzs.entity.bigstylecraft;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.core.model.SuperEntity;
import com.ylzs.entity.bigstylerecord.TimePriceOperationLog;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-19 14:59
 * 大货款式分析 主表
 */
@TableName("big_style_analyse_master")
@Data
public class BigStyleAnalyseMaster extends SuperEntity {
    private Long styleRandomCode;

    /**
     * 生产工单号
     */
    private String productionTicketNo;

    /**
     * 件数
     */
    private transient String number;

    /**
     * 工厂编码
     */
    private String factoryCode;
    /**
     * 生产组别
     */
    private String productionCategory;

    /**
     * 生产组别名称
     */
    private String productionCategoryName;

    @TableField("craft_category_code")
    private String craftCategoryCode;//工艺品类

    @TableField("big_style_analyse_code")
    private String bigStyleAnalyseCode;

    @TableField("craft_category_name")
    private String craftCategoryName;//工艺名称

    @TableField("clothes_category_name")
    private String clothesCategoryName;//服装品类

    @TableField("clothes_category_code")
    private String clothesCategoryCode;//工服装品类

    @TableField("style_code")
    private String styleCode;//款式编码

    @TableField("style_name")
    private String styleName;//款式 名称

    @TableField("style_desc")
    private String styleDesc;//款式 描述

    @TableField("for_sales_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date forSalesTime;//上市日期

    @TableField("main_frame_name")
    private String mainFrameName;//工艺主框架名称

    @TableField("main_frame_code")
    private String mainFrameCode;//工艺主框架编码

    @TableField("sub_brand")
    private String subBrand;//子品牌


    @TableField("fabric_fraction")
    private Integer fabricFraction;//面料分值

    @TableField("packing_method_coat")
    private String packingMethodCoat;//包装方式

    @TableField("standard_time")
    private BigDecimal standardTime;//标准时间

    @TableField("standard_price")
    private BigDecimal standardPrice;//标准单价

    @TableField("ct_style_code")
    private String ctStyleCode;//大货款号

    /**
     * 是否按照行号排序
     */
    private Boolean sortedByNumber;

    /**
     * 是否校验工序流
     */
    private Boolean isCheckCraftCode;

    /**
     * 适宜性编码
     */
    private String adaptCode;

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("release_time")
    private Date releaseTime;
    /**
     * 人
     */
    @TableField("release_user")
    private String releaseUser;

    private transient String releaseUserName;

    private transient String createUserName;
    private transient String updateUserName;

    /**
     * 状态的名称，例如1000，就是草稿
     */
    private transient String statusName;

    private transient List<JSONObject> pictures;

    private transient List<BigStyleAnalysePartCraft> partCraftList;

    private transient List<BigStyleAnalyseSkc> skc;

    private transient List<JSONObject> workTypeList;

    private transient List<TimePriceOperationLog> timePriceOperationLogList;

}
