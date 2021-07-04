package com.ylzs.service.partCombCraft.impl;

import com.ylzs.dao.partCombCraft.PartCombCraftRuleDao;
import com.ylzs.entity.partCombCraft.PartCombCraftRule;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.partCombCraft.IPartCombCraftRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author weikang
 * @Description 部件组合工艺规则
 * @Date 2020/3/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PartCombCraftRuleServiceImpl extends OriginServiceImpl<PartCombCraftRuleDao, PartCombCraftRule> implements IPartCombCraftRuleService {
}
