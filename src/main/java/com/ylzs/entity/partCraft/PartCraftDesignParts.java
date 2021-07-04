package com.ylzs.entity.partCraft;


import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * 部件工艺设计部件
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Data
public class PartCraftDesignParts extends SuperEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 设计部件编码,关联设计部件craft_part_code字段
	 */
	private String designCode;
	/**
	 * 设计部件名称,根据设计部件编码自动带出
	 */
	private String designName;
	/**
	 * 图案方式
	 */
	private String patternMode;

	private String patternType;//图案类型 PI对应patternTechnology

	private String patternTechnology;//图案工艺
	/**
	 * 部件工艺主数据编码
	 */
	private Long partCraftMainRandomCode;
	/**
	 * 描述信息
	 */
	private String remark;

}
