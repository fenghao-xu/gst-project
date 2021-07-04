package com.ylzs.entity.materialCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description
 * @Date 2020/5/4
 */
@Data
public class FabricPropertyAndPropertyResp implements Serializable {
    private static final long serialVersionUID = 4729896416083539187L;


    private Long randomCode;

    /**
     * 材料属性值编码
     */
    private String fabricPropertyDataCodes;

    /**
     * 材料属性编码
     */
    private String fabricPropertyCodes;

    /**
     * 服装品类编码
     */
    private String clothingCategoryCode;

    /**
     * 材料类型编码
     */
    private String materialCraftKindCode;
}
