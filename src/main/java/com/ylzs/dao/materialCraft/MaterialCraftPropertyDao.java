package com.ylzs.dao.materialCraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftProperty;
import com.ylzs.entity.materialCraft.resp.MaterialCraftPropertyResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺对应属性值
 * @Date 2020/3/5
 */
@Mapper
public interface MaterialCraftPropertyDao extends BaseDAO<MaterialCraftProperty> {

    int updatePublishStatus(@Param("randomCodes")List<Long> list);

    int updateNotActiveStatus(@Param("randomCodes")List<Long> list);

}
