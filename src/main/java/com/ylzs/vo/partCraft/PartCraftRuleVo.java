package com.ylzs.vo.partCraft;

import com.ylzs.core.model.SuperEntityVo;
import lombok.Data;

/**
 * @className PartCraftRuleVo
 * @Description
 * @Author sky
 * @create 2020-03-04 20:17:48
 */
@Data
public class PartCraftRuleVo extends SuperEntityVo {


    private static final long serialVersionUID = 597069848025839481L;
    /**
     * 原工序,工序词库sewing_craft_warehouse的random_code
     */
    private String sourceCraftCode;
    /**
     * 原有工序描述
     */
    private String sourceCraftName;
    /**
     * 现有工序代码,工序词库sewing_craft_warehouse的random_code
     */
    private String actionCraftCode;
    /**
     * 现有工序描述
     */
    private String actionCraftName;
    /**
     * 处理方式 -1减少 0替换 1增加
     */
    private Integer processType;
    /**
     * 部件工艺主数据编码
     */
    private Long partCraftMainRandomCode;

    private Integer processingSortNum;
}

