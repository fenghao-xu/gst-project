package com.ylzs.service.workplacecraft;

import com.ylzs.entity.workplacecraft.WorkplaceCraft;
import com.ylzs.vo.workplacecraft.WorkplaceCraftExportVo;

import java.util.Date;
import java.util.List;
public interface IWorkplaceCraftService {


    int deleteByPrimaryKey(Long randomCode);

    int insert(WorkplaceCraft record);

    int insertSelective(WorkplaceCraft record);

    WorkplaceCraft selectByPrimaryKey(Long randomCode);

    int updateByPrimaryKeySelective(WorkplaceCraft record);

    int updateByPrimaryKey(WorkplaceCraft record);

    int batchInsert(List<WorkplaceCraft> list);

    List<WorkplaceCraft> getByCondition(Long productionPartRandomCode,
                                        String keywords,
                                        String craftCategory,
                                        Long mainFrameRandomCode,
                                        String mainFrameCode,
                                        Integer craftFlowNum,
                                        Date createDateStart,
                                        Date createDateStop,
                                        Date updateDateStart,
                                        Date updateDateStop,
                                        String createUser,
                                        String updateUser,String craftCategoryList,Integer status);
    boolean isSawingCraftWorkplaceExists(String workplaceCraftCode);
    List<WorkplaceCraftExportVo> getWorkplaceCraftExportVos(List<WorkplaceCraft> workplaceCrafts);
    boolean isFlowNumExistsInMainFrame(Integer craftFlowNum,
                                       Long mainFrameRandomCode,
                                       Long excludeRandomCode);
    boolean isWorkplaceCraftNameExistsInMainFrame(String workplaceCraftName,
                                                  Long mainFrameRandomCode,
                                                  Long excludeRandomCode);
    int updateRelateWorkplaceCraft(String workplaceCraftCode);
    String getWorkplaceCraftInUsed(String workplaceCraftCode);


}
