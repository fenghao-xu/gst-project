package com.ylzs.dao.partCraft;


import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.partCraft.PartCraftPosition;
import com.ylzs.vo.partCraft.PartCraftPositionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部件工艺位置
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Mapper
public interface PartCraftPositionDao extends BaseDAO<PartCraftPosition> {

    @Select("SELECT * FROM part_craft_position WHERE part_craft_main_random_code = #{partCraftMainRandomCode}")
    List<PartCraftPosition> getPartCraftPositionMainList(Long partCraftMainRandomCode);

    @Select("SELECT * FROM part_craft_position WHERE part_craft_main_random_code = #{partCraftMainRandomCode} AND status = #{status}")
    List<PartCraftPosition> getPartCraftPositionList(Long partCraftMainRandomCode, Integer status);

    List<PartCraftPosition> getPartCraftPositionRandomList(@Param("randomCodes") List<Long> randomCodes);

    @Select("SELECT * FROM part_craft_position WHERE part_craft_main_random_code = #{partCraftMainRandomCode} AND status = #{status}")
    List<PartCraftPositionVo> getPartCraftPositionVoList(Long partCraftMainRandomCode, Integer status);

    List<PartCraftPositionVo> getRandomCodePartCraftPositionBatchList(@Param("mainRandomCodes") List<Long> mainRandomCodes);

    public Integer getNumberByPartCraftMainRandomCode(@Param("partCraftMainRandomCode") Long partCraftMainRandomCode);
}
