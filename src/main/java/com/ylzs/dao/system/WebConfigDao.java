package com.ylzs.dao.system;

import com.ylzs.entity.system.WebConfig;

import java.util.List;

/**
 * @author xuwei
 * @create 2019-08-16 14:35
 */
public interface WebConfigDao {
    public List<WebConfig>getConfigList();

    public  WebConfig getConfigByKey(String key);
}
