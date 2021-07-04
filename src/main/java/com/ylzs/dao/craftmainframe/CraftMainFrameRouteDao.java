package com.ylzs.dao.craftmainframe;

import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface CraftMainFrameRouteDao {

    int deleteByPrimaryKey(Long randomCode);

    int insert(CraftMainFrameRoute record);

    int insertOrUpdate(CraftMainFrameRoute record);

    int insertOrUpdateSelective(CraftMainFrameRoute record);

    int insertSelective(CraftMainFrameRoute record);


    CraftMainFrameRoute selectByPrimaryKey(Long randomCode);



    int updateByPrimaryKeySelective(CraftMainFrameRoute record);

    int updateByPrimaryKey(CraftMainFrameRoute record);

    int updateBatch(List<CraftMainFrameRoute> list);

    int batchInsert(@Param("list") List<CraftMainFrameRoute> list);


    List<CraftMainFrameRoute> getByMainFrameRandomCode(Long mainFrameRandomCode);

    List<CraftMainFrameRoute> getByMainFrameCode(@Param("mainFrameCode") String mainFrameCode);

    List<CraftMainFrameRoute> getByCondition(@Param("mainFrameRandomCode") Long mainFrameRandomCode, @Param("keywords") String keywords,
                                             @Param("beginDate") Date beginDate,
                                             @Param("endDate") Date endDate);
}