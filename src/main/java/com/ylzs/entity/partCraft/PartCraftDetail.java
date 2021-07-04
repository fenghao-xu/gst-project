package com.ylzs.entity.partCraft;


import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 部件工艺明细
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Data
public class PartCraftDetail extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 工序代码
	 */
	private String craftCode;
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
	//机器设备名称
	private String machineName;
	/**
	 * 部件工艺标准时间之和，单位分钟
	 */
	private BigDecimal standardTime;
	/**
	 * 部件工艺标准单价之和，单位元
	 */
	private BigDecimal standardPrice;
	/**
	 * 部件工艺主数据编码
	 */
	private Long partCraftMainRandomCode;
	/**
	 * 描述信息
	 */
	private String remark;

	/**
	 * 主框架编码
	 */
	private String craftMainFrameCode;

}
