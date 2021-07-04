package com.ylzs.entity.custom;


import com.baomidou.mybatisplus.annotation.TableField;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * 定制款工艺路径规则表
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 10:56:07
 */
@Data
public class CustomStyleRule extends SuperEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 过程编码
	 */
	private Integer processNo;
	/**
	 * 设计部件编码描述
	 */
	private String designPartDesc;
	/**
	 * 原工序编码
	 */
	private String sourceCraftCode;
	/**
	 * 原工序名称
	 */
	private String sourceCraftName;
	/**
	 * 处理类型 -1减少 0替换 1增加
	 */
	private Integer processType;
	/**
	 * 执行工序编码
	 */
	private String actionCraftCode;
	/**
	 * 执行工序名称
	 */
	private String actionCraftName;
	/**
	 * 定制款主数据random_code
	 */
	private Long customMainRandomCode;
	/**
	 * 备注
	 */
	private String remark;

	@TableField(exist = false)
	private transient Integer nember;
	/**
	 * 是否执行
	 */
	@TableField(exist = false)
	private Boolean whetherToExecute = true;

	private Integer processingSortNum;
}
