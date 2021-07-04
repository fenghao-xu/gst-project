package com.ylzs.vo.bigstylereport;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xuwei
 * @create 2020-08-20 9:48
 * 工种报表
 */
@Data
public class WorkTypeReport {
    private String craftCode;// 工序编号
    private String workTypeCode;// 工种代码
    private String workTypeName;// 工种名称
    private BigDecimal standardTime;// 标准时间
    private BigDecimal standardPrice;// 标准单价
    /**
     * 生产工单号
     */
    private String productionTicketNo;

    private String ctStyleCode;//大货款号

    /**
     * 工厂编码
     */
    private String factoryCode;
    /**
     * 工厂编码
     */
    private String factoryName;
    /**
     * 生产组别
     */
    private String productionCategory;

    /**
     * 生产组别名称
     */
    private String productionCategoryName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
