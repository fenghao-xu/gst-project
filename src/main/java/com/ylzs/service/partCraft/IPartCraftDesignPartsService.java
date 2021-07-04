package com.ylzs.service.partCraft;


import com.ylzs.entity.partCraft.PartCraftDesignParts;
import com.ylzs.entity.thinkstyle.ThinkStyleCraft;
import com.ylzs.entity.thinkstyle.ThinkStylePart;
import com.ylzs.entity.thinkstyle.ThinkStyleProcessRule;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.partCraft.DesignPartVO;
import com.ylzs.vo.partCraft.PartCraftDesignPartsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 部件工艺设计部件
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
public interface IPartCraftDesignPartsService extends IOriginService<PartCraftDesignParts> {

    List<PartCraftDesignParts> getDesignPartsMainList(Long partCraftMainCode);

    List<PartCraftDesignParts> getDesignPartsMainList(Long partCraftMainCode, Integer status);

    List<PartCraftDesignParts> getDesignPartsRandomList(@Param("randomCodes") List<Long> randomCodes);

    List<PartCraftDesignPartsVo> getDesignPartsVoList(Long partCraftMainCode, Integer status);

    Map<Long, List<PartCraftDesignPartsVo>> getDesignPartGroupMainRandomCodeByList(List<Long> randomCodes);

    List<DesignPartVO> getDataByDesignCode(String designCode);

    public Integer getCountByDesignPartAndPosition(String designCode, String positionCode, String clothingCategory);

    public List<ThinkStyleCraft> getPartCraftByDesignPartAndPosition(String designCode, String positionCode, String clothingCategory);

    public List<ThinkStyleProcessRule> getPartCraftRulesByDesignPartAndPosition(String designCode, String positionCode, String clothingCategory);

    public List<ThinkStylePart> getPartPriceAndTImeByDesignPartAndPosition(String designCode, String positionCode, String clothingCategory);
}

