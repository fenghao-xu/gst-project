package com.ylzs.entity.materialCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 查看页面返回属性数据
 * @Date 2020/3/16
 */
@Data
public class MaterialCraftPropertyResp implements Serializable {
    private static final long serialVersionUID = 3501275950641753560L;

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

}
