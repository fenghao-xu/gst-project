package com.ylzs.service.partCraft.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.core.model.SuperEntity;
import com.ylzs.dao.partCraft.PartCraftRuleDao;
import com.ylzs.entity.partCraft.PartCraftRule;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.partCraft.IPartCraftRuleService;
import com.ylzs.vo.partCraft.PartCraftRuleVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service()
public class PartCraftRuleServiceImpl extends OriginServiceImpl<PartCraftRuleDao, PartCraftRule> implements IPartCraftRuleService {

    @Resource
    private PartCraftRuleDao ruleDao;

    @Override
    public List<PartCraftRule> getRulesList(Long partCraftMainRandomCode) {
        List<PartCraftRule> list = new ArrayList<>();
        try {
            list = ruleDao.getRulesList(partCraftMainRandomCode);
        } catch (Exception ex) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<PartCraftRule> getRulesList(Long partCraftMainCode, Integer status) {
        List<PartCraftRule> list = new ArrayList<>();
        try {
            QueryWrapper<PartCraftRule> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(PartCraftRule::getPartCraftMainRandomCode, partCraftMainCode)
                    .eq(SuperEntity::getStatus, status);
            list = ruleDao.selectList(queryWrapper);
        } catch (Exception ex) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<PartCraftRule> getPartPositionRuleAll(String partPositionCode, String clothingCagegoryCode) {
        List<PartCraftRule> list = new ArrayList<>();
        try {
            list = ruleDao.getPartPositionRuleAll(partPositionCode, clothingCagegoryCode);
        } catch (Exception ex) {
            ex.getMessage();
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<PartCraftRuleVo> getRulesMainVoList(Long partCraftMainRandomCode, Integer status) {
        List<PartCraftRuleVo> list = new ArrayList<>();
        try {
            list = ruleDao.getRulesMainVoList(partCraftMainRandomCode, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Map<Long, List<PartCraftRuleVo>> getPartCraftRuleGroupMainRandomCodeByList(List<Long> mainRandomCodes) {
        Map<Long, List<PartCraftRuleVo>> groupByMap = new HashMap<>();
        try {
            if (null != mainRandomCodes && mainRandomCodes.size() > 0) {
                List<PartCraftRuleVo> list = ruleDao.getMainRandomCodesRulesBatchList(mainRandomCodes);
                if (ObjectUtils.isNotEmptyList(list)) {
                    groupByMap = list.stream().collect(Collectors.groupingBy(PartCraftRuleVo::getPartCraftMainRandomCode));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupByMap;
    }
}