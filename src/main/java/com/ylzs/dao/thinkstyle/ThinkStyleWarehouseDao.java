package com.ylzs.dao.thinkstyle;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.thinkstyle.ThinkStyleWarehouse;
import com.ylzs.vo.thinkstyle.CraftCategoryVo;
import com.ylzs.vo.thinkstyle.ThinkStylePublishCraftVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @description: 智库款工艺
 * @author: lyq
 * @date: 2020-03-05 18:38
 */

@Mapper
public interface ThinkStyleWarehouseDao extends BaseDAO<ThinkStyleWarehouse> {
    List<ThinkStyleWarehouse> selectAllThinkStyle(
            @Param("keywords") String keywords,
            @Param("craftCategoryCodes") String[] craftCategoryCodes,
            @Param("clothesCategoryCodes") String[] clothesCategoryCodes,
            @Param("updateDateStart") Date updateDateStart,
            @Param("updateDateStop") Date updateDateStop,
            @Param("status") Integer status,
            @Param("isInvalid") Boolean isInvalid);

    int updateStandardTime(@Param("styleRandomCode") Long styleRandomCode,
                           @Param("clothingCategoryCode") String clothingCategoryCode);



    List<CraftCategoryVo> getCraftCategoryVos();
    List<ThinkStylePublishCraftVo> getThinkStylePublishCraftVos(@Param("styleRandomCode") Long styleRandomCode,
                                                                @Param("clothingCategoryCode") String clothingCategoryCode);
}
