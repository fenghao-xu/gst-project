package com.ylzs.entity.partThesaurus;


import com.ylzs.core.model.SuperEntity;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 部件词库绣花、装饰位置
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:01:39
 */
@Data
public class PartWarehousePosition extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 序号
	 */
	private Integer orderNum;
	/**
	 * 部件关联代码（部件词库part_warehouse的random_code）
	 */
	private String partRandomCode;
	/**
	 * 部位代码关联代码(部件位置的random_code)
	 */
	private String positionRandomCode;
	/**
	 * 绣花方式
	 */
	private String embroideryStyleCode;
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
