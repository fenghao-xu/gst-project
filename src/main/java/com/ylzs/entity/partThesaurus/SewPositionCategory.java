package com.ylzs.entity.partThesaurus;


import com.ylzs.core.model.SuperEntity;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 缝边位置与品类关系表
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:36:52
 */
@Data
public class SewPositionCategory extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 根据工艺品类表code带出相应代码
	 */
	private String styleType;
	/**
	 * 缝边代码
	 */
	private String sewCode;
	/**
	 * 缝边位置
	 */
	private String sewName;


}
