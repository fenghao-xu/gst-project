package com.ylzs.service.thinkstyle;

import com.ylzs.entity.thinkstyle.ThinkStyleFabric;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.thinkstyle.ThinkStyleFabricVo;

import java.util.List;

/**
 * @description: 智库款工艺面料
 * @author:  lyq
 * @date: 2020-03-05 18:18
 */
public interface IThinkStyleFabricService extends IOriginService<ThinkStyleFabric> {
    List<ThinkStyleFabricVo> getThinkStyleFabricVos(Long styleRandomCode);
}
