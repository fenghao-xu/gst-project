package com.ylzs.vo.craftstd;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 辅助工具类型Vo
 */
@Data
public class AssistanceToolAndTypeVo   implements Serializable {

    private static final long serialVersionUID = 8002412681726989455L;
    /**
     * 辅助类型代码
     */
    private String toolTypeCode;
    /**
     * 辅助类型名称
     */
    private String toolTypeName;
    /**
     * 辅助工具列表
     */
    private List<AssistanceToolVo> childrens;
}
