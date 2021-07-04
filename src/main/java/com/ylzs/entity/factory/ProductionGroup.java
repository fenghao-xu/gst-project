package com.ylzs.entity.factory;


import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * 生产组别
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-05-05 09:13:31
 */
@Data
public class ProductionGroup extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 工厂代码
	 */
	private String factoryCode;
	/**
	 * 工厂名称
	 */
	private String factoryName;
	/**
	 * 适应性编码
	 */
	private String adaptCode;
	/**
	 * 工艺主框架代码
	 */
	private Long mainFrameRandomCode;
	/**
	 * 工艺主框架代码
	 */
	private String mainFrameCode;
	/**
	 * 工艺主框架名称
	 */
	private String mainFrameName;
	/**
	 * 描述
	 */
	private String remark;

	/**
	 * 生产组别代码
	 */
	private String productionGroupCode;
	/**
	 * '生产组别名称
	 */
	private String productionGroupName;

}
