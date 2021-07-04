package com.ylzs.entity.custom;


import com.baomidou.mybatisplus.annotation.TableField;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.util.List;

/**
 * 定制款部件信息
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 10:56:07
 */
@Data
public class CustomStylePart extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 设计部件编码
	 */
	private String designPartCode;

	/**
	 * 设计部件名称
	 */
	private String designPartName;
	/**
	 * 是不是虚拟部件
	 */
	private transient Boolean isVirtual;

	/**
	 * 部件位置代码
	 */
	private String positionCode;

	/**
	 * 部件位置名称
	 */
	private String positionName;
	/**
	 * 面料风格
	 */
	private String fabricStyle;
	/**
	 * 部件描述
	 */
	private String partDescript;
	/**
	 * 定制款主数据random_code
	 */
	private Long customMainRandomCode;
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 是否特殊
	 */
	@TableField(exist = false)
	private transient Boolean isSpecial;

	/**
	 * 条格类型
	 */
	private String patternSymmetry;

	/**
	 * 条格类型
	 */
	private String patternSymmetryName;
	@TableField(exist = false)
	private List<CustomStylePartCraft> partCraftList;
}
