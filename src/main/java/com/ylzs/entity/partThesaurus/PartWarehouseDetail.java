package com.ylzs.entity.partThesaurus;


import com.ylzs.core.model.SuperEntity;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 部件词库明细
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:01:39
 */
@Data
public class PartWarehouseDetail extends SuperEntity {
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
	 * 工序关联代码(工序词库sewing_craft_warehouse的random_code)
	 */
	private String craftRandomCode;
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
