package com.ylzs.dao.craftstd;

import com.ylzs.entity.craftstd.CategoryRelation;
import com.ylzs.entity.craftstd.CraftCategory;
import com.ylzs.vo.clothes.ClothesVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-23 15:37
 */
public interface CraftCategoryDao {
    @MapKey("craftCategoryCode")
    public Map<String, CraftCategory> getForMap();

    public Integer addCraftCategory(CraftCategory craftCategory);

    public Integer deleteCraftCategory(String craftCategoryCode);

    public Integer updateCraftCategory(CraftCategory craftCategory);

    public List<CraftCategory> getAllValidCraftCategory();

    public List<CraftCategory> getCraftCategoryByPage(@Param("keywords") String keywords, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    public List<CraftCategory> getCraftCategoryByCode(@Param("codes") String[] codes);

    public Long getStdCountByCode(@Param("code") String code);

    public Integer addCraftCategories(@Param("craftCategories") List<CraftCategory> craftCategories);

    public Integer getMaxSeqNum();

    public CraftCategory getCraftCategoryById(Integer id);

    public Integer addCategoryRelation(CategoryRelation categoryRelation);

    public Integer deleteCategoryRelation(@Param("craftCategoryId") Integer craftCategoryId,
                                          @Param("clothesCategoryId") Integer clothesCategoryId);

    public Integer deleteCategoryRelationById(@Param("id") Integer id);

    public Integer deleteCategoryRelationByCraftCategoryId(@Param("craftCategoryId") Integer craftCategoryId);

    List<CategoryRelation> getCategoryRelationByCraftCategoryId(@Param("craftCategoryId") Integer craftCategoryId);

    List<CraftCategory> getCraftCategoryAndPart();

    List<CraftCategory> getCraftCategoryCodeAndName();


    public List<CraftCategory> getCraftCategoryClothesCategory();

    public List<String> getCraftCategoryCodeByPartIds(Integer[] craftPartIds);

    List<String> getCraftCategoryCodeByIds(Integer[] craftPartIds);

    boolean isCraftStdNameExists(@Param("craftStdName") String craftStdName, @Param("craftStdId") Long craftStdId);

    public List<CraftCategory> getCraftCategoryByClothesCategoryCode(String clothesCategoryCode);

    public List<ClothesVo> getClothesAll(@Param("idArray") Integer[] idArray);

    List<Map<String, String>> getCraftCategoryAndClothesCategory();

    String getCraftCategoryCode(@Param("clotheCategoryCode") String clotheCategoryCode);

    List<CraftCategory> getAll();

}
