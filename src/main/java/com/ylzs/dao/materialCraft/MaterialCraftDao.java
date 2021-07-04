package com.ylzs.dao.materialCraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.resp.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author weikang
 * @Description 材料工艺主数据
 * @Date 2020/3/5
 */
@Mapper
public interface MaterialCraftDao extends BaseDAO<MaterialCraft> {

    MaterialCraft selectByCodeAndStatus(Map<String, Object> map);

    List<MaterialCraftAndPropertyResp> selectCraftAndProperty(@Param("randomCodes")List<Long> randomCode);

    List<QueryMaterialCraftDataResp> selectMaterialCraftChecklist(Map<String, Object> map);

    int updatePublishStatus();

    List<Long> selectRandomCode();

    List<MaterialCraftHistoryVersionResp> selectHistoryByRandomCode(Long randomCode);

    MaterialCraft selectMaterialCraft(long parseLong);

    List<MaterialCraftAllDataResp> selectListByKindCode(String kindCode);

    int selectNameCount(@Param("randomCode") Long randomCode,
                        @Param("name") String combCraftName,
                        @Param("clothesCategoryCode") String clothesCategoryCode);

    List<FabricPropertyAndPropertyResp> selectPropertyAndCategoryCode(Integer status);

    String selectLatestVersion(String materialCraftCode);

    int updateNotActiveStatus(@Param("randomCodes")List<Long> list);

    List<Long> selectPublishRandomCode();

}
