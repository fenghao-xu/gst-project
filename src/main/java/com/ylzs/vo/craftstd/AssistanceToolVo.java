package com.ylzs.vo.craftstd;

import lombok.Data;

import java.io.Serializable;

/**
 * 辅助工具Vo
 */
@Data
public class AssistanceToolVo  implements Serializable {

    private static final long serialVersionUID = -7883305905157529392L;
    /**
     * 辅助工具代码
     */
    private String toolCode;
    /**
     * 辅助工具名称
     */
    private String toolName;
}
