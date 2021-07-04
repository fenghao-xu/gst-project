package com.ylzs.entity.custom;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 定制款工艺路线主数据表
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-31 14:06:00
 */
@Data
public class CustomStyleCraftCourse extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 款式编码
	 */
	private String styleCode;
	/**
	 * 智库款名称
	 */
	private String thinkStyleName;
	/**
	 * 订单编号
	 */
	private String orderNo;
	/**
	 * 订单行号
	 */
	private String orderLineId;
	/**
	 * 款式描述
	 */
	private String styleDescript;
	/**
	 * 客户名称
	 */
	private String customerName;
	/**
	 * 面料分值
	 */
	private Integer fabricsScore;
	/**
	 * 标准时间
	 */
	private BigDecimal standerTime;
	/**
	 * 总单价
	 */
	private BigDecimal totalPrice;
	/**
	 * 款式图片
	 */
	private String stylePicture;
	/**
	 * 品牌
	 */
	private String brand;
	/**
	 * 工厂编码
	 */
	private String factoryCode;
	/**
	 * 工厂名称
	 */
	private String factoryName;
	/**
	 * 生产组别
	 */
	private String productionCategory;
	/**
	 * 工艺主框架编码
	 */
	private String mainFrameCode;
	/**
	 * 工艺主框架名
	 */
	private String mainFrameName;
	/**
	 * 生产工单号
	 */
	private String productionTicketNo;
	/**
	 * 主面料编码
	 */
	private String mainMaterialCode;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 发布人
	 */
	private String releaseUser;
	/**
	 * 发布时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date releaseTime;
	/**
	 * 失效时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expirationTime;
	/**
	 * 发布版本号
	 */
	@TableField("release_version")
	private String releaseVersion;
	/**
	 * 服装品类编码
	 */
	private String clothesCategoryCode;
	/**
	 * 服装品类名称
	 */
	private String clothesCategoryName;

	/**
	 * 工艺品类编码
	 */
	private String craftCategoryCode;

	/**
	 * 布面倒顺
	 */
	private String fabricDirection;

	/**
	 * 生效时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date effectiveTime;


	/**
	 * 定制订单号+订单行号
	 */
	@TableField(exist = false)
	private String customOrderNo;
	/**
	 * 工艺品类名称
	 */
	private String craftCategoryName;

	@TableField(exist = false)
	private List<CustomStylePart> customPartList;
	@TableField(exist = false)
	private List<CustomStyleRule> styleRuleList;
	@TableField(exist = false)
	private List<CustomStyleSewPosition> sewPositionList;
}
