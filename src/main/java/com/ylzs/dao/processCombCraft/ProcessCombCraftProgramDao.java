package com.ylzs.dao.processCombCraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.processCombCraft.ProcessCombCraftProgram;
import com.ylzs.entity.processCombCraft.resp.QueryProcessCombCraftProgramResp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author weikang
 * @Description 工序组合工艺方案
 * @Date 2020/3/14
 */
@Mapper
public interface ProcessCombCraftProgramDao extends BaseDAO<ProcessCombCraftProgram> {

    List<QueryProcessCombCraftProgramResp> selectProgramListByCraftRandomCode(long parseLong);

    List<ProcessCombCraftProgram> selectProgramListByRandomCodes(List<Long> randomCodes);

}
