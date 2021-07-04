package com.ylzs.service.custom;


import com.ylzs.entity.custom.CustomStylePartCraftMotion;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * 定制款工艺工序动作，此表为定制款部件工序子表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:01:58
 */
public interface ICustomStylePartCraftMotionService extends IOriginService<CustomStylePartCraftMotion> {

    public List<CustomStylePartCraftMotion> getCraftRandomCodeMotionList(List<Long> randomCodes);
    public List<CustomStylePartCraftMotion> getCraftRandomCodeMotionList(Long partCraftRandomCode);

    public int deleteCustomStyleMotionList(List<Long> randomCodes);
}

