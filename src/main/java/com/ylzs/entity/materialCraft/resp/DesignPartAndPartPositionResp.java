package com.ylzs.entity.materialCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 设计部件和部件位置返回数据
 * @Date 2020/3/10
 */
@Data
public class DesignPartAndPartPositionResp implements Serializable {
    private static final long serialVersionUID = 5437356053461101114L;

    /**
     * 特殊方案编号
     */
    private Integer specialPlanNumber;

    /**
     * 设计部件编码
     */
    private String designCode;

    /**
     * 设计部件名称
     */
    private String designName;

    /**
     * 服装品类名称
     */
    private String craftCategoryName;

    /**
     * 部件位置名称
     */
    private String partPositionName;

    /**
     * 部件位置编码
     */
    private String partPositionCode;
}
