package com.ylzs.service.thinkstyle;

import com.ylzs.entity.thinkstyle.ThinkStyleCraft;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.thinkstyle.CustomFlowNumVo;
import com.ylzs.vo.thinkstyle.ThinkStyleCraftVo;

import java.util.List;

/**
 * @description: 智库款工艺工序
 * @author: lyq
 * @date: 2020-03-05 17:34
 */
public interface IThinkStyleCraftService extends IOriginService<ThinkStyleCraft> {
    ThinkStyleCraftVo getThinkStyleCraftVo(ThinkStyleCraft obj);
    List<CustomFlowNumVo> getCustomFlowNum(String clothesCategoryCode, String[] craftCodes);


}
