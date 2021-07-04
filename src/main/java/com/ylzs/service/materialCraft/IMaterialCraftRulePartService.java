package com.ylzs.service.materialCraft;

import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftRulePart;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺特殊规则对应部件
 * @Date 2020/3/5
 */
public interface IMaterialCraftRulePartService extends IOriginService<MaterialCraftRulePart> {

    int updatePublishStatus(List<Long> list);

    int updateNotActiveStatus(List<Long> list);

    int selectCountByCraftRandomCodes(List<Long> randomCodes);
}
