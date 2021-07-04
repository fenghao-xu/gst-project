package com.ylzs.service.thinkstyle;

import com.ylzs.entity.thinkstyle.ThinkStyleProcessRule;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.thinkstyle.ThinkStyleProcessRuleVo;

import java.util.List;

/**
 * @description: 智库款工艺处理规则
 * @author: lyq
 * @date: 2020-03-05 17:38
 */
public interface IThinkStyleProcessRuleService extends IOriginService<ThinkStyleProcessRule> {
    /**
     * @param partRandomCode 智库款部件关联代码
     * @return 处理规则列表
     */
    List<ThinkStyleProcessRuleVo> getProcessRuleVos(Long partRandomCode);
}
