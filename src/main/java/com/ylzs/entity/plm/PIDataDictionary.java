package com.ylzs.entity.plm;

import lombok.Data;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description: 数据字典
 * @Date: Created in 17:24 2020/3/16
 */
@Data
public class PIDataDictionary {
    private Integer id;
    private String code;
    private String name;
    private List<PIDataDictionaryDicts> dicts;

}
