package com.ylzs.entity.plm;

import lombok.Data;

import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 17:25 2020/3/16
 */
@Data
public class PIDataDictionaryDicts {
    private String label;//字典标签
    private String value;//字典值
    private String status;//状态值
    private String parentValues;//父级字典值列表
}
