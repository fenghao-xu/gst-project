package com.ylzs.dao.partCombCraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.partCombCraft.PartCombCraftProgramDetail;
import com.ylzs.entity.partCombCraft.resp.PartCombCraftProgramDetailResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author weikang
 * @Description 部件组合工艺
 * @Date 2020/3/12
 */
@Mapper
public interface PartCombCraftProgramDetailDao extends BaseDAO<PartCombCraftProgramDetail> {

    List<String> selectListByCategoryCode(Map<String,Object> map);

    List<PartCombCraftProgramDetailResp> selectListByCraftRandomCode(String randomCode);

    List<PartCombCraftProgramDetail> selectProgramListByRandomCodes(@Param("randomCodes") List<Long> randomCodes);

}
