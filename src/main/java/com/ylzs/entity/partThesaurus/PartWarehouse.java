package com.ylzs.entity.partThesaurus;


import com.ylzs.core.model.SuperEntity;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 部件词库实体类
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:01:39
 */
@Data
public class PartWarehouse extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 服装品类(字典的code)
	 */
	private String clothesCategoryCode;
	/**
	 * 设计部件(设计部件的random_code)
	 */
	private String partRandomCode;
	/**
	 * 部件属性(字典的code)
	 */
	private String partPropertyCode;
	/**
	 * 部件代码
	 */
	private String partCode;
	/**
	 * 部件名称
	 */
	private String partName;
	/**
	 * 部件描述
	 */
	private String description;
	/**
	 * 生产部件(生产部件的random_code)
	 */
	private String producePartRandomCode;
	/**
	 * 共享部件(部件词库part_warehouse的random_code)
	 */
	private String sharePartRandomCode;
	/**
	 * 图案类型
	 */
	private String patternCode;
	/**
	 * 标准时间(单位TMU)
	 */
	private BigDecimal standardTime;
	/**
	 * 标准单价(单位元)
	 */
	private BigDecimal standardPrice;
	/**
	 * 部件图url
	 */
	private String imageUrl;
	/**
	 * 线稿图url
	 */
	private String sketchPictureUrl;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 审核时间
	 */
	private Date auditTime;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date updateTime;

}
