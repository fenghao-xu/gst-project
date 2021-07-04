package com.ylzs.entity.partCombCraft.resp;

import com.ylzs.entity.partCombCraft.PartCombCraft;
import com.ylzs.entity.partCombCraft.PartCombCraftProgramDetail;
import com.ylzs.entity.partCombCraft.PartCombCraftRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author weikang
 * @Description 部件组合工艺返回数据
 * @Date 2020/3/13
 */
@Data
public class PartCombCraftDataResp implements Serializable {

    private static final long serialVersionUID = -4590622056761246229L;
    /**
     * 部件组合工艺主数据
     */
    private PartCombCraft partCombCraft;

    /**
     * 部件和位置返回数据
     */
    private Map<Integer, List<PartCombCraftProgramDetail>> detailResps;

    /**
     * 规则
     */
    private List<PartCombCraftRule> ruleList;

}
