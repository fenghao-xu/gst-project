package com.ylzs.service.craftstd;

import com.ylzs.entity.craftstd.CraftCategory;
import com.ylzs.entity.staticdata.CraftGrade;
import com.ylzs.vo.CraftCateGoryVo;
import com.ylzs.vo.clothes.ClothesVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：工艺品类服务接口
 *
 * @author lyq
 * 2019-09-23 14:44
 */
public interface ICraftCategoryService {
    public Integer addCraftCategory(CraftCategory craftCategory);

    public Integer addCraftCategories(List<CraftCategory> craftCategories);

    public Integer deleteCraftCategory(String craftCategoryCode, String userCode);

    public Integer updateCraftCategory(CraftCategory craftCategory);

    public CraftCategory getCraftCategoryById(Integer id);

    public List<CraftCategory> getCraftCategoryByCode(String[] craftCategoryCodes);

    public List<CraftCategory> getCraftCategoryByPage(String keywords, Date beginDate, Date endDate);

    public Integer getMaxSeqNum();

    public List<CraftCategory> getCraftCategoryAndPart();

    List<CraftCategory> getCraftCategoryCodeAndName();

    public List<CraftCateGoryVo> getCraftCategoryApartVos();

    public List<CraftCategory> getAllValidCraftCategory();

    public List<CraftCategory> getCraftCategoryClothesCategory();

    public List<ClothesVo> getClothesAll(Integer [] ids);

    List<Map<String, String>> getCraftCategoryAndClothesCategory();

    /**
     * 根据服装品类编码查询对应工艺品类编码
     * @param clotheCategoryCode
     * @return
     */
    String getCraftCategoryCode(String clotheCategoryCode);

    List<CraftCategory> getAll();

}
