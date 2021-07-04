package com.ylzs.entity.partCraft;


import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * 部件工艺位置
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Data
public class PartCraftPosition extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 部件位置编码,关联part_position部件位置表
	 */
	private String partPositionCode;
	/**
	 * 部件位置名称,根据部件位置编码自动带出
	 */
	private String partPositionName;
	/**
	 * 服装品类
	 */
	//private String clothingCategory;
	/**
	 * 部件工艺主数据编码
	 */
	private Long partCraftMainRandomCode;
	/**
	 * 描述信息
	 */
	private String remark;
	/**
	 * 服装品类代码
	 */
	private String clothingCategoryCode;
	/**
	 * 服装品类名称
	 */
	private String clothingCategoryName;

}
