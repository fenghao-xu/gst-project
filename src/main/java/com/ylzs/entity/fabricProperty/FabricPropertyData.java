package com.ylzs.entity.fabricProperty;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author weikang
 * @Description  材料属性值实体类
 * @Date 2020/3/18
 */
@Data
public class FabricPropertyData extends SuperEntity {
    private static final long serialVersionUID = -5776819934522675564L;

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
     * 属性值代码
     */
    private String propertyValueCode;

    /**
     * 属性值
     */
    private String propertyValue;

}
