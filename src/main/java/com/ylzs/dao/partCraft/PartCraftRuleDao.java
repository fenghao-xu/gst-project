package com.ylzs.dao.partCraft;


import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.partCraft.PartCraftRule;
import com.ylzs.vo.partCraft.PartCraftRuleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部件工艺规则
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Mapper
public interface PartCraftRuleDao extends BaseDAO<PartCraftRule> {

    @Select("SELECT * FROM part_craft_rule WHERE part_craft_main_random_code = #{partCraftMainRandomCode}")
    List<PartCraftRule> getRulesList(Long partCraftMainRandomCode);


    List<PartCraftRule> getPartPositionRuleAll(@Param("partPositionCode")String partPositionCode,@Param("clothingCagegoryCode")String clothingCagegoryCode);

    @Select("SELECT * FROM part_craft_rule WHERE part_craft_main_random_code = #{partCraftMainRandomCode} AND status = #{status}")
    List<PartCraftRule> getRulesMainList(Long partCraftMainRandomCode,Integer status);
    @Select("SELECT * FROM part_craft_rule WHERE part_craft_main_random_code = #{partCraftMainRandomCode} AND status = #{status}")
    List<PartCraftRuleVo> getRulesMainVoList(Long partCraftMainRandomCode, Integer status);
    List<PartCraftRuleVo> getMainRandomCodesRulesBatchList(@Param("mainRandomCodes") List<Long> mainRandomCodes);

}
