package com.ylzs.entity.materialCraft.resp;

import com.ylzs.entity.materialCraft.MaterialCraftRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺规则和部件
 * @Date 2020/3/10
 */
@Data
public class MaterialCraftRuleAndPartResp implements Serializable {

    private static final long serialVersionUID = 7955260621273597228L;

    /**
     * 设计部件和部件位置
     */
    private List<QueryMaterialCraftRulePartResp> ruleParts;

    /**
     * 材料工艺规则
     */
    private List<MaterialCraftRule> sewingCraftResp;
}
