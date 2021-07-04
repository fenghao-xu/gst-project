package com.ylzs.dao.partCombCraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.partCombCraft.PartCombCraft;
import com.ylzs.entity.partCombCraft.req.QueryPartCombCraftReq;
import com.ylzs.entity.partCombCraft.resp.PartCombCraftAllDataResp;
import com.ylzs.entity.partCombCraft.resp.UserResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author weikang
 * @Description 部件组合工艺
 * @Date 2020/3/12
 */
@Mapper
public interface PartCombCraftDao extends BaseDAO<PartCombCraft> {

    List<PartCombCraft> selectPartCombCraftChecklist(QueryPartCombCraftReq craftReq);

    List<UserResp> selectPartCreateUser();

    List<PartCombCraftAllDataResp> selectListByCategoryCode(@Param("clothingCategoryCode") String clothingCategoryCode, @Param("codeArray")String[] codeArray);

    int selectNameCount(@Param("randomCode") Long randomCode,@Param("name") String combCraftName);

    PartCombCraft selectPartCombCraft(Long randomCode);

}
