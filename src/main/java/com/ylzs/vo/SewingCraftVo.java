package com.ylzs.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ylzs.core.model.SuperEntityVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @className SewingCraftWarehouseVo
 * @Description 车缝工序VO
 * @Author sky
 * @create 2020-03-03 09:31:53
 */

@Data
public class SewingCraftVo extends SuperEntityVo {

    private static final long serialVersionUID = -1136286712826281894L;

    /**工艺品类编号*/
    private String craftCategoryCode;
    /**工艺品类名称*/
    private String craftCategoryName;
    /**工艺部件编码*/
    private String partCode;
    /**工艺部件名称*/
    private String partName;
    /**工序编号*/
    private String craftCode;
    /**工序名称*/
    private String craftName;
    /**工序描述*/
    private String description;
    /**标准时间*/
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal standardTime;
    /**标准单价*/
    @JsonSerialize(using=ToStringSerializer.class)
    private BigDecimal standardPrice;
    private String picUrl;
    /**机器类型关联代码*/
    private String machineCode;
    /**机器设备名称*/
    private String machineName;
    /**工序流**/
    private String craftFlowNum;
    /**工艺主框架code */
    private String craftMainFrameCode;
    /**工艺主框架名称 */
    private String craftMainFrameName;
    /**
     * 站位
     */
    private String station;
    /**
     * 站位设备
     */
    private String stationDevice;


}
