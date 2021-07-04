package com.ylzs.service.thinkstyle.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.dao.thinkstyle.ThinkStyleProcessRuleDao;
import com.ylzs.entity.thinkstyle.ThinkStyleProcessRule;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.thinkstyle.IThinkStyleProcessRuleService;
import com.ylzs.vo.thinkstyle.ThinkStyleProcessRuleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：lyq
 * @description：智库款工艺处理规则
 * @date ：2020-03-05 18:50
 */

@Service
public class ThinkStyleProcessRuleService extends OriginServiceImpl<ThinkStyleProcessRuleDao, ThinkStyleProcessRule> implements IThinkStyleProcessRuleService {
    @Resource
    private ThinkStyleProcessRuleDao thinkStyleProcessRuleDao;

    @Override
    public List<ThinkStyleProcessRuleVo> getProcessRuleVos(Long partRandomCode) {
        QueryWrapper<ThinkStyleProcessRule> ruleQueryWrapper = new QueryWrapper<>();
        ruleQueryWrapper.lambda().eq(ThinkStyleProcessRule::getPartRandomCode, partRandomCode).orderByAsc(ThinkStyleProcessRule::getProcessingSortNum);
        List<ThinkStyleProcessRule> thinkStyleProcessRules = thinkStyleProcessRuleDao.selectList(ruleQueryWrapper);
        if(thinkStyleProcessRules == null) {
            return null;
        }
        List<ThinkStyleProcessRuleVo> thinkStyleProcessRuleVos = thinkStyleProcessRules.stream().map(this::getThinkStyleProcessRuleVo).collect(Collectors.toList());
        return thinkStyleProcessRuleVos;
    }

    private ThinkStyleProcessRuleVo getThinkStyleProcessRuleVo(ThinkStyleProcessRule obj) {
        ThinkStyleProcessRuleVo result = new ThinkStyleProcessRuleVo();
        try {
            BeanUtils.copyProperties(obj, result);
            if(obj.getProcessingSortNum() == null) obj.setProcessingSortNum(0);

            if (obj.getProcessType() != null) {
                if (obj.getProcessType().equals(-1)) {
                    result.setProcessTypeName("删除");
                } else if (obj.getProcessType().equals(0)) {
                    result.setProcessTypeName("替换");
                } else if (obj.getProcessType().equals(1)) {
                    result.setProcessTypeName("增加");
                }
            }

        } catch (Exception e) {

        }
        return result;
    }


}
