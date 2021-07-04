package com.ylzs.service.system.impl;

import com.ylzs.dao.system.WebConfigDao;
import com.ylzs.entity.system.WebConfig;
import com.ylzs.service.system.IWebConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 说明：系统配置服务实现
 * * @author xuwei
 * @create 2019-08-16 14:38
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WebConfigService implements IWebConfigService {
    @Resource
    private WebConfigDao webConfigDao;

    @Override
    public List<WebConfig> getConfigList() {
        return webConfigDao.getConfigList();
    }
    @Override
    public  WebConfig getConfigByKey(String key){
        return webConfigDao.getConfigByKey(key);
    }
}
