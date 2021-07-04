package com.ylzs.service.materialCraft.impl;


import com.ylzs.dao.materialCraft.MaterialCraftRuleDao;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftRule;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.materialCraft.IMaterialCraftRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺规则
 * @Date 2020/3/5
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MaterialCraftRuleServiceImpl extends OriginServiceImpl<MaterialCraftRuleDao, MaterialCraftRule> implements IMaterialCraftRuleService {

    @Resource
    private MaterialCraftRuleDao ruleDao;

    @Override
    public int updatePublishStatus(List<Long> list) {
        return ruleDao.updatePublishStatus(list);
    }

    @Override
    public int updateNotActiveStatus(List<Long> list) {
        return ruleDao.updateNotActiveStatus(list);
    }
}
