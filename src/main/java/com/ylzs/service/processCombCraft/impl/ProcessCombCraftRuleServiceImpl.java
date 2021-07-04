package com.ylzs.service.processCombCraft.impl;

import com.ylzs.dao.processCombCraft.ProcessCombCraftRuleDao;
import com.ylzs.entity.processCombCraft.ProcessCombCraftRule;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.processCombCraft.IProcessCombCraftRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author weikang
 * @Description 工序组合工艺规则
 * @Date 2020/3/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessCombCraftRuleServiceImpl extends OriginServiceImpl<ProcessCombCraftRuleDao, ProcessCombCraftRule> implements IProcessCombCraftRuleService {
}
