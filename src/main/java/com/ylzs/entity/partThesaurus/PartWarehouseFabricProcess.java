package com.ylzs.entity.partThesaurus;


import com.ylzs.core.model.SuperEntity;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 部件词库面料工艺
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:01:38
 */
@Data
public class PartWarehouseFabricProcess extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 部件关联代码(部件词库part_warehouse的random_code)
	 */
	private Long partRandomCode;
	/**
	 * 面料处理类型关联代码(部件词库面料属性的random_code)
	 */
	private Long partFabricRandomCode;
	/**
	 * 序号
	 */
	private Integer orderNum;
	/**
	 * 处理方式(-1减少 0替换 1增加)
	 */
	private Integer processType;
	/**
	 * 现工序代码(工序词库sewing_craft_warehouse的random_code)
	 */
	private String craftRandomCode;
	/**
	 * 原工序代码(工序词库sewing_craft_warehouse的random_code)
	 */
	private String oldCraftRandomCode;
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
