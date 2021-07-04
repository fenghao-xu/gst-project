package com.ylzs.entity.custom;


import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 工序词库部件关系
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-04-02 14:53:33
 */
@Data
public class CustomStyleSewPosition extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 定制订单工艺主数据（系统自动生成）
	 */
	private Long customMainRandomCode;
	/**
	 * 缝边位置编码
	 */
	private String sewPositionCode;
	/**
	 * 缝边位置名称
	 */
	private String sewPositionName;

	/**
	 * 缝边位置长度
	 */
	private BigDecimal sewPositionValue;

}
