package com.ylzs.vo.bigstylereport;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xuwei
 * @create 2020-05-29 9:19
 */
@Data
public class CraftVO {

    private String craftCode;// 工序编号
    private String craftName;// 工序名称
    private String craftPartName;// 工艺部件名称
    private String craftRemark;// 工序描述
    private String workTypeCode;// 工种代码
    private String workTypeName;// 工种名称
    private BigDecimal standardTime;// 标准时间
    private BigDecimal standardPrice;// 标准单价
    private String machineName;// 机器名称
    /**
     * 工序流
     */
    private  Integer craftFlowNum;
    /**
     * 工序等级
     */
    private String craftGradeCode;
    /**
     * 订单等级
     */
    private  Integer orderGrade;
    /**
     * 工票号
     */
    private String workOrderNo;

    /**
     * 生产部件编码
     */
    private String productionPartCode;


    /**
     * 后续工序编码
     */
    private transient String nextCraftCode;

    /**
     * 后续工序出现次数
     */
    private transient Integer nextCraftCodeCount;
}
