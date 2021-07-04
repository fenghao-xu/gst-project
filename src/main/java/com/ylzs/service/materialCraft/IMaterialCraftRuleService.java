package com.ylzs.service.materialCraft;

import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftRule;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺规则
 * @Date 2020/3/5
 */
public interface IMaterialCraftRuleService extends IOriginService<MaterialCraftRule> {

    int updatePublishStatus(List<Long> list);

    int updateNotActiveStatus(List<Long> list);
}
