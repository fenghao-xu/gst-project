package com.ylzs.vo.craftstd;

import lombok.Data;

import java.io.Serializable;

/**
 * 工艺建标辅助工具Vo
 */
@Data
public class CraftStdToolVo  implements Serializable {

    private static final long serialVersionUID = -8438198378484505001L;
    /**
     * 工序标准id
     */
    private Long craftStdId;

    /**
     * 工具代码
     */
    private String toolCode;

    /**
     * 工具名称
     */
    private String toolName;

    /**
     * 工具类型代码
     */
    private String toolTypeCode;
    /**
     * 工具类型名称
     */
    private String toolTypeName;

}
