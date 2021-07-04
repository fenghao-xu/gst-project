package com.ylzs.entity.fabricProperty.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description
 * @Date 2020/3/18
 */
@Data
public class FabricPropertyDataResp implements Serializable {
    private static final long serialVersionUID = -4900455799583218389L;

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
