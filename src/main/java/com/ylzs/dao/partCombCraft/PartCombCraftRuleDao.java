package com.ylzs.dao.partCombCraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.partCombCraft.PartCombCraftRule;
import com.ylzs.entity.partCombCraft.resp.PartCombCraftRuleResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author weikang
 * @Description 部件组合工艺规则
 * @Date 2020/3/12
 */
@Mapper
public interface PartCombCraftRuleDao extends BaseDAO<PartCombCraftRule> {

    List<PartCombCraftRuleResp> selectSourceListByRandomCode(long parseLong);

    List<PartCombCraftRuleResp> selectActionListByRandomCode(long parseLong);

    List<PartCombCraftRule> selectRuleListByRandomCodes(@Param("randomCodes")List<Long> randomCodes);

    List<PartCombCraftRule> selectRuleListByCraftRandomCode(long parseLong);

}
