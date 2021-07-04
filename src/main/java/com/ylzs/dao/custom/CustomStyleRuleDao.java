package com.ylzs.dao.custom;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.custom.CustomStyleRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定制款工艺路径规则表
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:01:58
 */
@Mapper
public interface CustomStyleRuleDao extends BaseDAO<CustomStyleRule> {

    public List<CustomStyleRule> getStyleMainRandomRuleList(Long mainRandomCode);

    int deleteCustomStyleRule(Long mainRandomCode);

    int deleteBatchCustomStyleRule(@Param("arrays") List<Long> randomCodeList);
}
