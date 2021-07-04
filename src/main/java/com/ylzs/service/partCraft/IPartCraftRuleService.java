package com.ylzs.service.partCraft;


import com.ylzs.entity.partCraft.PartCraftRule;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.partCraft.PartCraftRuleVo;

import java.util.List;
import java.util.Map;

/**
 * 部件工艺规则
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
public interface IPartCraftRuleService extends IOriginService<PartCraftRule> {

    List<PartCraftRule> getRulesList(Long partCraftMainRandomCode);
    List<PartCraftRule> getRulesList(Long partCraftMainRandomCode,Integer status);

    /**
     * 根据部件位置和服装品类编码查询部件工艺规则列表
     * @param partPositionCode
     * @param clothingCagegoryCode
     * @return
     */
    List<PartCraftRule> getPartPositionRuleAll(String partPositionCode, String clothingCagegoryCode);
    List<PartCraftRuleVo> getRulesMainVoList(Long partCraftMainRandomCode, Integer status);
    Map<Long,List<PartCraftRuleVo>> getPartCraftRuleGroupMainRandomCodeByList(List<Long> mainRandomCodes);
}

