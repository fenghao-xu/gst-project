package com.ylzs.service.workplacecraft.impl;

import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.StringUtils;
import com.ylzs.dao.workplacecraft.WorkplaceCraftDao;
import com.ylzs.entity.workplacecraft.WorkplaceCraft;
import com.ylzs.service.workplacecraft.IWorkplaceCraftService;
import com.ylzs.vo.workplacecraft.WorkplaceCraftExportVo;
import org.apache.shiro.dao.DataAccessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class WorkplaceCraftService implements IWorkplaceCraftService {

    @Resource
    private WorkplaceCraftDao workplaceCraftDao;

    @Override
    public int deleteByPrimaryKey(Long randomCode) {
        return workplaceCraftDao.deleteByPrimaryKey(randomCode);
    }

    @Override
    public int insert(WorkplaceCraft record) throws DataAccessException {
        return workplaceCraftDao.insert(record);
    }

    @Override
    public int insertSelective(WorkplaceCraft record) throws DataAccessException {
        return workplaceCraftDao.insertSelective(record);
    }

    @Override
    public WorkplaceCraft selectByPrimaryKey(Long randomCode) {
        return workplaceCraftDao.selectByPrimaryKey(randomCode);
    }

    @Override
    public int updateByPrimaryKeySelective(WorkplaceCraft record) throws DataAccessException {
        return workplaceCraftDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(WorkplaceCraft record) throws DataAccessException {
        return workplaceCraftDao.updateByPrimaryKey(record);
    }

    @Override
    public int batchInsert(List<WorkplaceCraft> list) throws DataAccessException {
        return workplaceCraftDao.batchInsert(list);
    }

    @Override
    public List<WorkplaceCraft> getByCondition(Long productionPartRandomCode, String keywords, String craftCategory, Long mainFrameRandomCode, String mainFrameCode, Integer craftFlowNum, Date createDateStart, Date createDateStop, Date updateDateStart, Date updateDateStop, String createUser, String updateUser, String craftCategoryListStr, Integer status) {
        List<String> craftCategoryList = null;
        if (StringUtils.isNotEmpty(craftCategoryListStr)) {
            String str[] = craftCategoryListStr.split(",");
            craftCategoryList = new ArrayList<>();
            craftCategoryList.addAll(Arrays.asList(str));
        }
        return workplaceCraftDao.getByCondition(productionPartRandomCode, keywords, craftCategory, mainFrameRandomCode, mainFrameCode, craftFlowNum, createDateStart, createDateStop, updateDateStart, updateDateStop, createUser, updateUser, craftCategoryList, status);
    }

    @Override
    public boolean isSawingCraftWorkplaceExists(String workplaceCraftCode) {
        return workplaceCraftDao.isSawingCraftWorkplaceExists(workplaceCraftCode);
    }

    @Override
    public List<WorkplaceCraftExportVo> getWorkplaceCraftExportVos(List<WorkplaceCraft> workplaceCrafts) {
        List<WorkplaceCraftExportVo> workplaceCraftExportVos = new ArrayList<>();
        int i = 1;
        for (WorkplaceCraft itm : workplaceCrafts) {
            WorkplaceCraftExportVo vo = getWorkplaceCraftExportVo(itm);
            vo.setLineNo(i++);
            workplaceCraftExportVos.add(vo);
        }
        return workplaceCraftExportVos;
    }

    @Override
    public boolean isFlowNumExistsInMainFrame(Integer craftFlowNum, Long mainFrameRandomCode, Long excludeRandomCode) {
        return workplaceCraftDao.isFlowNumExistsInMainFrame(craftFlowNum, mainFrameRandomCode, excludeRandomCode);
    }

    @Override
    public boolean isWorkplaceCraftNameExistsInMainFrame(String workplaceCraftName, Long mainFrameRandomCode, Long excludeRandomCode) {
        return workplaceCraftDao.isWorkplaceCraftNameExistsInMainFrame(workplaceCraftName, mainFrameRandomCode, excludeRandomCode);
    }

    @Override
    public int updateRelateWorkplaceCraft(String workplaceCraftCode) {
        return workplaceCraftDao.updateRelateWorkplaceCraft(workplaceCraftCode);
    }

    @Override
    public String getWorkplaceCraftInUsed(String workplaceCraftCode) {
        return workplaceCraftDao.getWorkplaceCraftInUsed(workplaceCraftCode);
    }


    public WorkplaceCraftExportVo getWorkplaceCraftExportVo(WorkplaceCraft workplaceCraft) {
        WorkplaceCraftExportVo result = new WorkplaceCraftExportVo();
        try {
            BeanUtils.copyProperties(workplaceCraft, result);
            if (workplaceCraft.getStatus() == null || workplaceCraft.getStatus().equals(BusinessConstants.Status.DRAFT_STATUS)) {
                result.setStatusName("草稿");
            } else if (workplaceCraft.getStatus().equals(BusinessConstants.Status.AUDITED_STATUS)) {
                result.setStatusName("已审核");
            } else if (workplaceCraft.getStatus().equals(BusinessConstants.Status.PUBLISHED_STATUS)) {
                result.setStatusName("已发布");
            } else if (workplaceCraft.getStatus().equals(BusinessConstants.Status.IN_VALID)) {
                result.setStatusName("已删除");
            } else if (workplaceCraft.getStatus().equals(BusinessConstants.Status.INVALID_STATUS)) {
                result.setStatusName("已失效");
            } else {
                result.setStatusName("未知");
            }
        } catch (Exception e) {

        }
        return result;
    }

}
