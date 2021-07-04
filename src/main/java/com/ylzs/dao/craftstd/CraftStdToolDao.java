package com.ylzs.dao.craftstd;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.craftstd.CraftStdTool;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CraftStdToolDao extends BaseDAO<CraftStdTool> {
    public List<CraftStdTool> getCraftStdToolByCraftStdId(Long craftStdId);
    List<CraftStdTool> getCraftStdTool(@Param("craftStdIds") List<Long> craftStdIds);
}