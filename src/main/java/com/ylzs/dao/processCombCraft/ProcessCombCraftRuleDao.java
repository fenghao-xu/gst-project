package com.ylzs.dao.processCombCraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.processCombCraft.ProcessCombCraftRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author weikang
 * @Description 工序组合工艺规则
 * @Date 2020/3/14
 */
@Mapper
public interface ProcessCombCraftRuleDao extends BaseDAO<ProcessCombCraftRule> {

    List<ProcessCombCraftRule> selectRuleListByCraftRandomCode(long parseLong);

    List<ProcessCombCraftRule> selectRuleListByRandomCodes(@Param("randomCodes")List<Long> randomCodes);
}
