package com.ylzs.entity.partThesaurus;


import com.ylzs.core.model.SuperEntity;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 部件词库面料属性
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:01:38
 */
@Data
public class PartWarehouseFabric extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 部件关联代码（部件词库part_warehouse的random_code）
	 */
	private String partRandomCode;
	/**
	 * 序号
	 */
	private Integer orderNum;
	/**
	 * 面料处理类型
	 */
	private String fabricProcessType;
	/**
	 * 面料处理值(面料属性列表的值)
	 */
	private String fabricProcessValue;
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
