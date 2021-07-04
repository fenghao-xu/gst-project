package com.ylzs.service.materialCraft.impl;


import com.ylzs.dao.materialCraft.MaterialCraftRulePartDao;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftRulePart;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.materialCraft.IMaterialCraftRulePartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺规则部件
 * @Date 2020/3/5
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MaterialCraftRulePartServiceImpl extends OriginServiceImpl<MaterialCraftRulePartDao, MaterialCraftRulePart> implements IMaterialCraftRulePartService {

    @Resource
    private MaterialCraftRulePartDao rulePartDao;

    @Override
    public int updatePublishStatus(List<Long> list) {
        return rulePartDao.updatePublishStatus(list);
    }

    @Override
    public int updateNotActiveStatus(List<Long> list) {
        return rulePartDao.updateNotActiveStatus(list);
    }

    @Override
    public int selectCountByCraftRandomCodes(List<Long> randomCodes) {
        return rulePartDao.selectCountByCraftRandomCodes(randomCodes);
    }

}
