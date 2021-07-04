package com.ylzs.service.system;


import com.ylzs.entity.system.WebConfig;

import java.util.List;

/**
 * 说明：web服务配置接口
 *
 * @author xuwei
 * @create 2019-08-16 14:38
 */
public interface IWebConfigService {
    public List<WebConfig> getConfigList();
    public WebConfig getConfigByKey(String key);
}
