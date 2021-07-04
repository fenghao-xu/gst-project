package com.ylzs.entity.partCombCraft.req;

import com.ylzs.entity.partCombCraft.PartCombCraft;
import com.ylzs.entity.partCombCraft.PartCombCraftRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 部件组合工艺请求数据
 * @Date 2020/3/12
 */
@Data
public class PartCombCraftReq implements Serializable {

    private static final long serialVersionUID = -1765238964719322247L;
    /**
     * 部件组合工艺
     */
    private PartCombCraft combCraft;

    /**
     * 部件组合工艺规则
     */
    private List<PartCombCraftRule> craftRuleList;

    /**searchDesignPartData
     * 部件组合工艺方案请求数据
     */
    private List<PartCombCraftProgramReq> partProgramReq;

    /**
     * 用户编码
     */
    private String userCode;
}
