package com.ylzs.service.partCraft;


import com.ylzs.entity.partCraft.PartCraftPosition;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.partCraft.PartCraftPositionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 部件工艺位置
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
public interface IPartCraftPositionService extends IOriginService<PartCraftPosition> {

    List<PartCraftPosition> getPartCraftPositionMainList(Long partCraftMainRandomCode);
    List<PartCraftPosition> getPartCraftPositionMainList(Long partCraftMainRandomCode,Integer status);
    List<PartCraftPosition> getPartCraftPositionRandomList(@Param("randomCodes") List<Long> randomCodes);
    List<PartCraftPositionVo> getPartCraftPositionVoList(Long partCraftMainRandomCode, Integer status);
    Map<Long,List<PartCraftPositionVo>> getPartCraftPositionGroupMainRandomCodeByList(List<Long> randomCodes);
    public Integer getNumberByPartCraftMainRandomCode( Long partCraftMainRandomCode);
}

