package com.ylzs.entity.materialCraft.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺规则及部件list
 * @Date 2020/3/6
 */
@Data
public class MaterialCraftRuleAndPartListReq implements Serializable {

    private static final long serialVersionUID = -4175630862271280915L;

    private List<MaterialCraftRuleAndPartReq> ruleAndPartReqList;
}
