package com.ylzs.dao.workplacecraft;

import com.ylzs.entity.workplacecraft.WorkplaceCraft;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.dao.DataAccessException;

import java.util.Date;
import java.util.List;

@Mapper
public interface WorkplaceCraftDao {
    int deleteByPrimaryKey(Long randomCode) throws DataAccessException;

    int insert(WorkplaceCraft record) throws DataAccessException;

    int insertSelective(WorkplaceCraft record) throws DataAccessException;

    WorkplaceCraft selectByPrimaryKey(Long randomCode);

    int updateByPrimaryKeySelective(WorkplaceCraft record) throws DataAccessException;

    int updateByPrimaryKey(WorkplaceCraft record) throws DataAccessException;

    int batchInsert(@Param("list") List<WorkplaceCraft> list) throws DataAccessException;

    List<WorkplaceCraft> getByCondition(@Param("productionPartRandomCode") Long productionPartRandomCode,
                                        @Param("keywords") String keywords,
                                        @Param("craftCategory") String craftCategory,
                                        @Param("mainFrameRandomCode") Long mainFrameRandomCode,
                                        @Param("mainFrameCode") String mainFrameCode,
                                        @Param("craftFlowNum") Integer craftFlowNum,
                                        @Param("createDateStart") Date createDateStart,
                                        @Param("createDateStop") Date createDateStop,
                                        @Param("updateDateStart") Date updateDateStart,
                                        @Param("updateDateStop") Date updateDateStop,
                                        @Param("createUser") String createUser,
                                        @Param("updateUser") String updateUser,
                                        @Param("craftCategoryList") List<String> craftCategoryList, @Param("status") Integer status);

    boolean isSawingCraftWorkplaceExists(@Param("workplaceCraftCode") String workplaceCraftCode);

    boolean isFlowNumExistsInMainFrame(@Param("craftFlowNum") Integer craftFlowNum,
                                       @Param("mainFrameRandomCode") Long mainFrameRandomCode,
                                       @Param("excludeRandomCode") Long excludeRandomCode);

    boolean isWorkplaceCraftNameExistsInMainFrame(@Param("workplaceCraftName") String workplaceCraftName,
                                                  @Param("mainFrameRandomCode") Long mainFrameRandomCode,
                                                  @Param("excludeRandomCode") Long excludeRandomCode);


    int updateRelateWorkplaceCraft(@Param("workplaceCraftCode") String workplaceCraftCode);

    String getWorkplaceCraftInUsed(@Param("workplaceCraftCode") String workplaceCraftCode);
}