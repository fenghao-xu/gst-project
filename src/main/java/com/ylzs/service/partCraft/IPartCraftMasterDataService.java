package com.ylzs.service.partCraft;


import com.alibaba.fastjson.JSONObject;
import com.ylzs.entity.partCraft.PartCraftMasterData;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.partCraft.*;

import java.util.HashMap;
import java.util.List;

/**
 * 部件工艺主数据表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
public interface IPartCraftMasterDataService extends IOriginService<PartCraftMasterData> {

    PartCraftMasterData getPartCraftMasterData(Long randomCode);

    List<PartCraftMasterBasicVo> searchPartCraftInfo(HashMap param);
    List<PartCraftMasterExportVo> searchPartCraftInfoExport(HashMap param);

    PartCraftMasterData savePartCraftData(PartCraftMasterBasicVo basicVo) throws Exception;

    PartCraftMasterData verifyPartCraftInf(PartCraftMasterBasicVo basicVo) throws Exception;

    Boolean invalidPartCraftData(PartCraftMasterBasicVo basicVo) throws Exception;

    List<Long> getPartCraftMasterRandomCode(String designCode, String partPostion,
                                            String clothingCategoryCode, String businessType);

    public List<PartCraftMasterBasicVo> searchPartCraftAndDetailInfo(HashMap param);

    public List<PartCraftMasterBasicVo> searchOnlyPartCraftInfo(HashMap param);

    public PartCraftMasterBasicVo searchPartCraftInfoRandomCode(HashMap param);

    public JSONObject checkPartCraftDesignPartParments(String type, List<String> craftDesignParts);

    public JSONObject checkPartCraftDesignPartParments(List<PartCraftDesignPartsVo> designPartCodesList);

    public JSONObject checkPartCraftDesignPartPositionParments(String type, List<String> designPartCodesList,
                                                               List<String> positionCodeList);

    public JSONObject checkPartCraftPositionParments(List<PartCraftPositionVo> positionCodeList);

    public Boolean checkPartCraftRulePaements(List<PartCraftRuleVo> partCraftRuleVoList);

    public Boolean checkPartCraftDetailPaements(List<PartCraftDetailVo> partCraftDetailVoList);

    public List<String> getPartCraftCodeNameAll();

    public boolean isThinkStyleUsed(long randomCode);

    public boolean isDelDesignPartUsed(Long masterRandomCode, List<String> designCodes, List<String> positionCodes);
    public boolean isPartCraftNameUsed(Long masterRandomCode, String categoryCode, String partCraftName);

    Integer updateStatusByDesignPartCode(Integer status, String designPartCode);

}

