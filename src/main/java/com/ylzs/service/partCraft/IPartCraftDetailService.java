package com.ylzs.service.partCraft;


import com.ylzs.entity.partCraft.PartCraftDetail;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.partCraft.PartCraftDetailVo;

import java.util.List;
import java.util.Map;

/**
 * 部件工艺明细
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
public interface IPartCraftDetailService extends IOriginService<PartCraftDetail> {

    List<PartCraftDetail> getPartCraftDetailMainList(Long partCraftMainCode);
    List<PartCraftDetail> getPartCraftDetailMainList(Long partCraftMainCode, Integer status);
    List<PartCraftDetail> getPartCraftDetailRandomList(List<Long> randomCode);
    List<PartCraftDetailVo> getPartCraftDetailVo(Long partCraftMainCode, Integer status);
    Map<Long,List<PartCraftDetailVo>> getPartCraftDetailMainRandomCodeByList(List<Long>  randomCode);
    public void updateCraftRemarkAndName(Map<String,Object> param);
}

