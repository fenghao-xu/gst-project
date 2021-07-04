package com.ylzs.service.custom;


import com.ylzs.entity.custom.CustomStyleRule;
import com.ylzs.service.IOriginService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定制款工艺路径规则表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:01:58
 */
public interface ICustomStyleRuleService extends IOriginService<CustomStyleRule> {

    public List<CustomStyleRule> getStyleMainRandomRuleList(Long mainRandomCode);
    int deleteCustomStyleRule(Long mainRandomCode);
    int deleteBatchCustomStyleRule(List<Long> randomCodeList);
}

