package com.ylzs.dao.timeparam;

import com.ylzs.entity.timeparam.SameLevelCraft;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-06-22 14:56
 */
public interface SameLevelCraftDao {
    public List<SameLevelCraft> getAllData();

    @MapKey("sameLevelCraftNumericalCode")
    public Map<String,SameLevelCraft> getAllDataToMap();

    @MapKey("sameLevelCraftNumericalCode")
    public Map<String,SameLevelCraft> getSameLevelCraftMap(@Param("codes") List<String> codes);

    public List<SameLevelCraft> getByParam(@Param("keywords") String keywords,@Param("craftCategoryCode") String craftCategoryCode,@Param("makeTypeCode") String makeTypeCode);

    public SameLevelCraft getDetails(@Param("id") Integer id);

    public String getMaxNumber(@Param("craftCategoryCode") String craftCategoryCode);

    public void updateSameLevelCraft(SameLevelCraft sameLevelCraft);

    public void addSameLevelCraft(SameLevelCraft sameLevelCraft);

    public void deleteSameLevelCraft(Long id);
}
