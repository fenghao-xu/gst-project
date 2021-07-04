package com.ylzs.service.thinkstyle;

import com.ylzs.entity.thinkstyle.ThinkStylePart;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.thinkstyle.ThinkStylePartVo;

import java.util.List;
import java.util.Map;

/**
 * @description: 智库款工艺部件
 * @author: lyq
 * @date: 2020-03-05 17:35
 */
public interface IThinkStylePartService extends IOriginService<ThinkStylePart> {
    List<ThinkStylePartVo> selectThinkStylePartVo(Long thinkStyleRandomCode, String clothingCategoryCode);
    Map<String, ThinkStylePart> getThinkStylePartMap(String styleCode);
}
