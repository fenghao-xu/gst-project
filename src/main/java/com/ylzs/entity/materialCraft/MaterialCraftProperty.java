package com.ylzs.entity.materialCraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author weikang
 * @Description 材料工艺对应属性值实体
 * @Date 2020/3/5
 */
@Data
public class MaterialCraftProperty extends SuperEntity {
    private static final long serialVersionUID = -4591406692332245192L;

    /**
     * 材料工艺主数据material_craft的random_code
     */
    private Long materialCraftRandomCode;

    /**
     * 材料属性值编码
     */
    private String fabricPropertyDataCode;

    /**
     * 材料属性编码
     */
    private String fabricPropertyCode;

    /**
     * 材料属性名称
     */
    private String fabricPropertyName;

    /**
     * 材料属性值
     */
    private String fabricPropertyData;

    /**
     * 下标位置
     */
    private Integer propertyIndex;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1040.未生效 1090 删除
     */
    private Integer status;
}
