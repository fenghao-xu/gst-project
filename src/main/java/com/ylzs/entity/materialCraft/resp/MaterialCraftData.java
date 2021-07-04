package com.ylzs.entity.materialCraft.resp;

import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author weikang
 * @Description 材料工艺查看数据
 * @Date 2020/3/10
 */
@Data
public class MaterialCraftData implements Serializable {
    private static final long serialVersionUID = 4215543947401639063L;

    /**
     * 材料工艺主数据
     */
    private MaterialCraft materialCraft;

    /**
     * 材料工艺对应属性值
     */
    private List<MaterialCraftProperty> propertyResps;

    /**
     * 以方案编号为key,材料工艺规则和设计部件为value
     */
    private Map<Integer,MaterialCraftRuleAndPartResp> map;

    /**
     * 材料工艺历史版本数据
     */
    private List<MaterialCraftHistoryVersionResp> historyVersionResps;
}
