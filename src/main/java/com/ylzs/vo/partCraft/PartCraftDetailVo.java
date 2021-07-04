package com.ylzs.vo.partCraft;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ylzs.core.model.SuperEntityVo;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @className PartCraftMasterDetailVo
 * @Description
 * @Author sky
 * @create 2020-03-05 11:43:49
 */
@Data
public class PartCraftDetailVo extends SuperEntityVo {

    private static final long serialVersionUID = 803839943491255753L;

    /**
     * 工序代码
     */
    private String craftCode;
    /**
     * 工序代码
     */
    private String craftName;


    private Boolean pad;
    /**
     * 工序描述
     */
    private String craftRemark;
    /**
     * 工序流
     */
    private String craftFlowNum;
    /**
     * 机器设备
     */
    private String machineCode;
    private String machineName;
    /**
     * 部件工艺标准时间之和，单位分钟
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal standardTime;
    /**
     * 部件工艺标准单价之和，单位元
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal standardPrice;
    /**
     * 部件工艺主数据编码
     */
    private Long partCraftMainRandomCode;

    private transient List<SewingCraftAction> motionList;

    /**
     * 主框架编码
     */
    private String craftMainFrameCode;
    private String craftMainFrameName;

    private String craftPartName;// 工艺部件名称

    private String craftGradeCode;// 工序等级
}
