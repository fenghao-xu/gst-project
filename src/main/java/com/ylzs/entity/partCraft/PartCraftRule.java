package com.ylzs.entity.partCraft;


import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * 部件工艺规则
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Data
public class PartCraftRule extends SuperEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 原工序,工序词库sewing_craft_warehouse的random_code
	 */
	private String sourceCraftCode;
	/**
	 * 原有工序描述
	 */
	private String sourceCraftName;
	/**
	 * 现有工序代码,工序词库sewing_craft_warehouse的random_code
	 */
	private String actionCraftCode;
	/**
	 * 现有工序描述
	 */
	private String actionCraftName;
	/**
	 * 处理方式 -1减少 0替换 1增加
	 */
	private Integer processType;
	/**
	 * 部件工艺主数据编码
	 */
	private Long partCraftMainRandomCode;
	/**
	 * 描述信息
	 */
	private String remark;

	private Integer partCraftMainStatus;

	private Integer processingSortNum;

}
