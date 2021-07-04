package com.ylzs.dao.materialCraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.materialCraft.MaterialCraftRulePart;
import com.ylzs.entity.materialCraft.resp.QueryMaterialCraftRulePartResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺特殊规则对应部件
 * @Date 2020/3/5
 */
@Mapper
public interface MaterialCraftRulePartDao extends BaseDAO<MaterialCraftRulePart> {

    int updatePublishStatus(@Param("randomCodes")List<Long> list);

    int selectCountByCraftRandomCodes(@Param("randomCodes") List<Long> randomCode);

    List<QueryMaterialCraftRulePartResp> selectListByCraftRandomCode(Long randomCode);

    int updateNotActiveStatus(@Param("randomCodes")List<Long> list);

}
