package com.ylzs.dao.partCraft;


import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.partCraft.PartCraftDesignParts;
import com.ylzs.entity.thinkstyle.ThinkStyleCraft;
import com.ylzs.entity.thinkstyle.ThinkStylePart;
import com.ylzs.entity.thinkstyle.ThinkStyleProcessRule;
import com.ylzs.vo.partCraft.DesignPartVO;
import com.ylzs.vo.partCraft.PartCraftDesignPartsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
//import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.List;

/**
 * 部件工艺设计部件
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Mapper
public interface PartCraftDesignPartsDao extends BaseDAO<PartCraftDesignParts> {

    @Select("SELECT * FROM part_craft_design_parts WHERE part_craft_main_random_code = #{partCraftMainCode}")
    List<PartCraftDesignParts> getDesignPartsMainList(Long partCraftMainCode);

    @Select("SELECT * FROM part_craft_design_parts WHERE part_craft_main_random_code = #{partCraftMainCode} AND status = #{status}")
    List<PartCraftDesignParts> getDesignPartsList(Long partCraftMainCode, Integer status);

    List<PartCraftDesignParts> getDesignPartsRandomList(@Param("randomCodes") List<Long> randomCodes);

    @Select("SELECT * FROM part_craft_design_parts WHERE part_craft_main_random_code = #{partCraftMainCode} AND status = #{status}")
    List<PartCraftDesignPartsVo> getDesignPartsVoList(Long partCraftMainCode, Integer status);

    List<PartCraftDesignPartsVo> getDesignPartsVoBatchList(@Param("mainRandomCodes") List<Long> mainRandomCodes);

    List<DesignPartVO> getDataByDesignCode(@Param("designCode") String designCode);

    public Integer getCountByDesignPartAndPosition(@Param("designCode") String designCode, @Param("positionCode") String positionCode, @Param("clothingCategory") String clothingCategory);

    public List<ThinkStyleCraft> getPartCraftByDesignPartAndPosition(@Param("designCode") String designCode, @Param("positionCode") String positionCode, @Param("clothingCategory") String clothingCategory);

    public List<ThinkStyleProcessRule> getPartCraftRulesByDesignPartAndPosition(@Param("designCode") String designCode, @Param("positionCode") String positionCode, @Param("clothingCategory") String clothingCategory);

    public List<ThinkStylePart>getPartPriceAndTImeByDesignPartAndPosition(@Param("designCode") String designCode, @Param("positionCode") String positionCode, @Param("clothingCategory") String clothingCategory);
}
