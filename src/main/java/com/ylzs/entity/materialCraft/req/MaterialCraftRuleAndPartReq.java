package com.ylzs.entity.materialCraft.req;

import com.ylzs.entity.materialCraft.MaterialCraftRule;
import com.ylzs.entity.materialCraft.MaterialCraftRulePart;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺及部件请求数据
 * @Date 2020/3/6
 */
@Data
public class MaterialCraftRuleAndPartReq implements Serializable {
    private static final long serialVersionUID = -2038167324311041752L;

    /**
     * 材料工艺特殊规则对应部件
     */
    private List<MaterialCraftRulePart> craftRuleParts;

    /**
     * 材料工艺规则
     */
    private List<MaterialCraftRule> craftRules;
}
