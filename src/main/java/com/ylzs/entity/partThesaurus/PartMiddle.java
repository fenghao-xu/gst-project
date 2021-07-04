package com.ylzs.entity.partThesaurus;


import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * 设计部件
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-26 17:08:11
 */
@Data
public class PartMiddle extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 部件中类名称
	 */
	private String partMiddleName;
	/**
	 * 部件中类代码
	 */
	private String partMiddleCode;
	/**
	 * 服装品类（关联服装品类字典表，对应所属服装26品类）
	 */
	private String clothingCategory;
	/**
	 * 备注
	 */
	private String remark;


}
