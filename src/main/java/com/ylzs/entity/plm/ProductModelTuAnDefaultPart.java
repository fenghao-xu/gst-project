package com.ylzs.entity.plm;

import lombok.Data;

/**
 *
 */
@Data
public class ProductModelTuAnDefaultPart {
    /**
     * 部件编码
     */
    private String componentCode;
    /**
     * 是否是默认部件
     */
    private Boolean isDefaultComponent;
    /**
     * 图案类型
     */
    private Integer tuAnType;
    /**
     * 部位编码
     */
    private String position;
    /**
     * 面料代码
     */
    private String materialCode;
    /**
     * 能否定制
     */
    private Boolean canCustomize;
    /**
     * 有否取消
     */
    private Boolean canCancel;

}
