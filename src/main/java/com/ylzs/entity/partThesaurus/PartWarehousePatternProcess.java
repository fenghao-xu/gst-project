package com.ylzs.entity.partThesaurus;


import com.ylzs.core.model.SuperEntity;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 部件词库绣花/装饰工艺
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:01:39
 */
@Data
public class PartWarehousePatternProcess extends SuperEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 序号
	 */
	private Integer orderNum;
	/**
	 * PAD显示
	 */
	private Integer isPadShow;
	/**
	 * 部件关联代码(部件词库part_warehouse的random_code)
	 */
	private String partRandomCode;
	/**
	 * 部件位置关联代码(部件词库绣花、装饰位置part_warehouse_position的random_code)
	 */
	private String partPositonRandomCode;
	/**
	 * 处理方式(-1减少 0替换 1增加)
	 */
	private Integer processType;
	/**
	 * 现工序关联代码(工序词库sewing_craft_warehouse的random_code)
	 */
	private String craftRandomCode;
	/**
	 * 原工序关联代码(工序词库sewing_craft_warehouse的random_code)
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
