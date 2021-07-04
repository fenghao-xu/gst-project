package com.ylzs.service.thinkstyle;

import com.ylzs.entity.thinkstyle.ThinkStyleCraftHistory;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.thinkstyle.ThinkStyleCraftHistoryVo;

import java.util.List;

/**
 * 智库款工序历史
 */
public interface IThinkStyleCraftHistoryService extends IOriginService<ThinkStyleCraftHistory> {
    List<ThinkStyleCraftHistoryVo> getThinkStyleCraftHistoryVos(Long partRandomCode);


}
