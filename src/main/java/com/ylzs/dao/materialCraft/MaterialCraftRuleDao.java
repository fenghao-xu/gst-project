package com.ylzs.dao.materialCraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺规则
 * @Date 2020/3/5
 */
@Mapper
public interface MaterialCraftRuleDao extends BaseDAO<MaterialCraftRule> {

    int updatePublishStatus(@Param("randomCodes")List<Long> list);

    List<MaterialCraftRule> selectRuleListByCraftRandomCode(long parseLong);

    int updateNotActiveStatus(@Param("randomCodes")List<Long> list);

}
