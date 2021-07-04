package com.ylzs.dao.processCombCraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.partCombCraft.resp.UserResp;
import com.ylzs.entity.processCombCraft.ProcessCombCraft;
import com.ylzs.entity.processCombCraft.req.QueryProcessCombCraftReq;
import com.ylzs.entity.processCombCraft.resp.ProcessCombCraftAllDataResp;
import com.ylzs.entity.processCombCraft.resp.ProcessCombCraftProgramResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author weikang
 * @Description 工序组合工艺
 * @Date 2020/3/14
 */
@Mapper
public interface ProcessCombCraftDao extends BaseDAO<ProcessCombCraft> {

    List<String> selectListByCategoryCode(Map<String, Object> map);

    List<ProcessCombCraft> selectProcessCombCraftChecklist(QueryProcessCombCraftReq craftReq);

    List<UserResp> selectProcessCreateUser();

    List<ProcessCombCraftAllDataResp> selectProcessListByCategoryCode(String clothingCategoryCode);

    int selectNameCount(@Param("randomCode") Long randomCode, @Param("name") String combCraftName);

    ProcessCombCraft selectProcessCombCraft(Long randomCode);

}
