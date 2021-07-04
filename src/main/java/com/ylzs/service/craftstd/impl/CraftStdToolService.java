package com.ylzs.service.craftstd.impl;

import com.ylzs.service.impl.OriginServiceImpl;
import org.springframework.stereotype.Service;
import com.ylzs.entity.craftstd.CraftStdTool;
import com.ylzs.dao.craftstd.CraftStdToolDao;
import com.ylzs.service.craftstd.ICraftStdToolService;

import javax.annotation.Resource;
import java.util.List;


/**
 * 工艺标准与辅助工具关联服务
 */
@Service
public class CraftStdToolService extends OriginServiceImpl<CraftStdToolDao, CraftStdTool> implements ICraftStdToolService {
    @Resource
    private CraftStdToolDao craftStdToolDao;

    @Override
    public List<CraftStdTool> getCraftStdToolByCraftStdId(Long craftStdId) {
        return craftStdToolDao.getCraftStdToolByCraftStdId(craftStdId);
    }
}
