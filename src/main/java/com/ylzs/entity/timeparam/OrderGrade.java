package com.ylzs.entity.timeparam;


import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * 订单等级数据
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-04-01 15:47:56
 */
@Data
public class OrderGrade extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单等级编码
	 */
	private String orderCode;
	/**
	 * 订单等级名称
	 */
	private String orderGradeName;
	/**
	 * 面料分值范围
	 */
	private String fabricScoreRange;
	/**
	 * 最小值
	 */
	private Integer minValue;
	/**
	 * 最大值
	 */
	private Integer maxValue;
	/**
	 * 
	 */
	private String remark;

}
