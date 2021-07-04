package com.ylzs.dao.fabricProperty;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.fabricProperty.FabricProperty;
import com.ylzs.entity.materialCraft.resp.MaterialCraftPropertyResp;

import java.util.List;

/**
 * @author weikang
 * @Description
 * @Date 2020/3/9
 */
public interface FabricPropertyDao extends BaseDAO<FabricProperty> {

    List<MaterialCraftPropertyResp> selectFabricPropertyList(String kindCode);

}
