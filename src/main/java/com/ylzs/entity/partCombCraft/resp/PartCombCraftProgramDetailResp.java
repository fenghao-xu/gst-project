package com.ylzs.entity.partCombCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 部件和位置返回数据
 * @Date 2020/3/12
 */
@Data
public class PartCombCraftProgramDetailResp implements Serializable {

    private static final long serialVersionUID = -5469561341945627931L;

    /**
     * 部件编码和位置编码
     */
    private String designCodeAndPositionCode;

    /**
     * 设计部件和名称组合
     */
    private String designCodeAndName;

    /**
     * 部件编码和名称组合
     */
    private String partPositionCodeAndName;

    /**
     * 方案编码
     */
    private Integer partNumber;

    /**
     * 位置下标
     */
    private Integer partDetailIndex;
}
