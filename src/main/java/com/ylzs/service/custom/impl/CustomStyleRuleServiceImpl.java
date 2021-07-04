package com.ylzs.service.custom.impl;

import com.ylzs.dao.custom.CustomStyleRuleDao;
import com.ylzs.entity.custom.CustomStyleRule;
import com.ylzs.service.custom.ICustomStyleRuleService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service()
public class CustomStyleRuleServiceImpl extends OriginServiceImpl<CustomStyleRuleDao, CustomStyleRule> implements ICustomStyleRuleService {

    @Autowired
    private CustomStyleRuleDao ruleDao;

    @Override
    public List<CustomStyleRule> getStyleMainRandomRuleList(Long mainRandomCode) {
        List<CustomStyleRule> list = new ArrayList<>();
        try {
            list = ruleDao.getStyleMainRandomRuleList(mainRandomCode);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int deleteCustomStyleRule(Long mainRandomCode) {
        return ruleDao.deleteCustomStyleRule(mainRandomCode);
    }

    @Override
    public int deleteBatchCustomStyleRule(List<Long> randomCodeList) {
        return ruleDao.deleteBatchCustomStyleRule(randomCodeList);
    }
}