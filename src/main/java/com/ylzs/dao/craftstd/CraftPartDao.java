package com.ylzs.dao.craftstd;

import com.ylzs.entity.craftstd.CraftPart;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：工艺部件
 *
 * @author lyq
 * 2019-09-25 11:13
 */
public interface CraftPartDao {
    Integer addCraftPart(CraftPart craftPart);

    Integer deleteCraftPart(String craftPartCode);

    Integer updateCraftPart(CraftPart craftPart);

    List<CraftPart> getCraftPartByCode(@Param("craftPartCodes") String[] craftPartCodes);

    List<CraftPart> getCraftPartByPage(@Param("keywords") String keywords, @Param("craftCategoryId") Integer craftCategoryId, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate,@Param("craftCategoryCode")String craftCategoryCode);

    Long getStdCountByCraftPartCode(@Param("craftPartCode") String craftPartCode);

    List<CraftPart> getCraftPartByCategoryId(@Param("craftCategoryId") Integer craftCategoryId);

    CraftPart getCraftPartById(Integer id);

    List<String> getCraftPartCodeByPartIds(Integer[] craftPartIds);

    public List<CraftPart> getAllValidCraftPart();

    @MapKey("craftPartCode")
    public Map<String, CraftPart> getAllCraftPartToMap();

    public List<CraftPart> getCateGoryCodePartAll(@Param("codeArray") String codeArray);
}
