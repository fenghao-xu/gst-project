package com.ylzs.entity.fabricProperty;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author weikang
 * @Description 材料属性实体类
 * @Date 2020/3/9
 */
@Data
public class FabricProperty extends SuperEntity {
    private static final long serialVersionUID = -8320306908247399550L;

    /**
     * 材料类型编码
     */
    private String materialCraftKindCode;

    /**
     * 材料属性编码
     */
    private String fabricPropertyCode;

    /**
     * 材料属性名称
     */
    private String fabricPropertyName;

    /**
     * 备注
     */
    private String remark;
}
