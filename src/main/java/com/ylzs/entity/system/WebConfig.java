package com.ylzs.entity.system;

import lombok.Data;

/**
 * @author xuwei
 * @create 2019-08-16 14:12
 */
@Data
public class WebConfig {
    /**
     * 唯一标识
     */
    private Integer id;
    /**
     * 键
     */
    private  String key;
    /**
     * 值
     */
    private String value;
}
