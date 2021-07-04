package com.ylzs.entity.fabricProperty.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 查询材料属性值请求参数
 * @Date 2020/3/21
 */
@Data
public class FabricPropertyDataReq implements Serializable {
    private static final long serialVersionUID = 881282969745413178L;

    /**
     * 材料属性编码
     */
    private String fabricPropertyCode;

    /**
     * 属性值
     */
    private String propertyValue;

    /**
     * 材料类型编码
     */
    private String kindCode;

    private Integer page;

    private Integer rows;

}
