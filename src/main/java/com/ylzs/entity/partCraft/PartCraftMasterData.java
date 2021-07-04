package com.ylzs.entity.partCraft;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 部件工艺主数据表
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Data
public class PartCraftMasterData extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 部件工艺编码
	 */
	private String partCraftMainCode;
	/**
	 * 部件工艺名称
	 */
	private String partCraftMainName;
	/**
	 * 工艺品类code
	 */
	private String craftCategoryCode;
	/**
	 * 结构部件，关联工艺部件表craft_part ,random_code字段
	 */
	private String craftPartCode;
	/**
	 * 部件类型，数据字典中获取
	 */
	private String partType;
	/**
	 * 部件工艺标准时间之和，单位分钟
	 */
	private BigDecimal standardTime;
	/**
	 * 部件工艺标准单价之和，单位元
	 */
	private BigDecimal standardPrice;
	/**
	 * 描述信息
	 */
	private String remark;

	/**
	 * 发布人
	 */
	private String releaseUser;

	/**
	 * 业务类型
	 */
	private String businessType;
	/**
	 * 发布时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date releaseTime;

}
