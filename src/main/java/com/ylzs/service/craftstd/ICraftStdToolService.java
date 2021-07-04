package com.ylzs.service.craftstd;

import com.ylzs.entity.craftstd.CraftStdTool;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * 工艺标准与辅助工具关联服务接口
 */
public interface ICraftStdToolService extends IOriginService<CraftStdTool> {

    public List<CraftStdTool> getCraftStdToolByCraftStdId(Long craftStdId);
}
