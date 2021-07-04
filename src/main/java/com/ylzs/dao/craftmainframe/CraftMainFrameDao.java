package com.ylzs.dao.craftmainframe;

import com.ylzs.entity.craftmainframe.CraftMainFrame;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface CraftMainFrameDao {

    int deleteByPrimaryKey(Long randomCode);

    int insert(CraftMainFrame record);

    int insertSelective(CraftMainFrame record);

    CraftMainFrame selectByPrimaryKey(Long randomCode);

    int updateByPrimaryKeySelective(CraftMainFrame record);

    int updateByPrimaryKey(CraftMainFrame record);

    int batchInsert(@Param("list") List<CraftMainFrame> list);

    CraftMainFrame selectByCode(String code);

    List<CraftMainFrame> getByCondition(@Param("keywords") String keywords, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    boolean isDefaultMainFrameExists(@Param("craftCategoryRandomCode") Long craftCategoryRandomCode, @Param("excludeMainFrameRandomCode") Long excludeMainFrameRandomCode);

    @MapKey("mainFrameCode")
    public Map<String,CraftMainFrame> getAllMainFrameToMap();

    CraftMainFrame selectMainFrameByClothesCategoryCode(@Param("clothesCategoryCode") String clothesCategoryCode);

    List<CraftMainFrame> selectMainFrameListByClothesCategoryCode(@Param("clothesCategoryCode") String clothesCategoryCode);

    List<CraftMainFrame>getByCraftCategoryAndType(@Param("craftCategoryCode") String craftCategoryCode,@Param("frameType") String frameType);

    void updateRelateMainFrameInfo(@Param("mainFrameCode") String mainFrameCode);
}