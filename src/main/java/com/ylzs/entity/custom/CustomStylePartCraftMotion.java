package com.ylzs.entity.custom;


import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 车缝工序词库动作
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-04-01 17:10:46
 */
@Data
public class CustomStylePartCraftMotion extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 序号（顺序号）
	 */
	private Integer orderNum;
	/**
	 * 动作代码
	 */
	private String motionCode;
	/**
	 * 动作名称
	 */
	private String motionName;
	/**
	 * 频率
	 */
	private BigDecimal frequency;
	/**
	 * 动作描述
	 */
	private String description;
	/**
	 * 转速
	 */
	private Integer speed;
	/**
	 * 机器时间（单位TMU）
	 */
	private Integer machineTime;
	/**
	 * 人工时间（单位TMU）
	 */
	private Integer manualTime;
	/**
	 * 关联定制款部件工序表random_code
	 */
	private Long partCraftRandomCode;
	/**
	 * 审核时间
	 */
	private Date auditDate;

}
