package com.ylzs.vo.partCraft;

import lombok.Data;

/**
 * @author xuwei
 * @create 2020-07-22 17:18
 */
@Data
public class DesignPartVO {
    /**
     * 设计部件编码,关联设计部件craft_part_code字段
     */
    private String designCode;
    /**
     * 设计部件名称,根据设计部件编码自动带出
     */
    private String designName;

    /**
     * 部件工艺主数据编码
     */
    private Long partCraftMainRandomCode;

    private Integer status;

}
