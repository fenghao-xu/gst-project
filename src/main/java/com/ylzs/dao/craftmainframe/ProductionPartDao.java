package com.ylzs.dao.craftmainframe;

import com.ylzs.entity.craftmainframe.ProductionPart;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductionPartDao {

    int deleteByPrimaryKey(Long randomCode);

    int insert(ProductionPart record);

    int insertOrUpdate(ProductionPart record);

    int insertOrUpdateSelective(ProductionPart record);

    int insertSelective(ProductionPart record);


    ProductionPart selectByPrimaryKey(Long randomCode);


    int updateByPrimaryKeySelective(ProductionPart record);

    int updateByPrimaryKey(ProductionPart record);

    int updateBatch(List<ProductionPart> list);

    int batchInsert(@Param("list") List<ProductionPart> list);

    List<ProductionPart> getByMainFrameRandomCode(Long mainFrameRandomCode);

    @MapKey("productionPartCode")
    Map<String, ProductionPart> getMapByMainFrameCode(@Param("mainFrameCode") String mainFrameCode);

    List<ProductionPart> getByCondition(@Param("craftCategoryRandomCode") Long craftCategoryRandomCode, @Param("mainFrameRandomCode") Long mainFrameRandomCode,
                                        @Param("keywords") String keywords, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);
}