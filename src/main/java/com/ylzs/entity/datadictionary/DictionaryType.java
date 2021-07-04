package com.ylzs.entity.datadictionary;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @Author: watermelon.xzx
 * @Description:字典值管理
 * @Date: Created in 17:17 2020/2/26
 */
@Data
public class DictionaryType extends SuperEntity {
    private String dictionaryTypeCode;//类型编码
    private String dictionaryTypeName;//类型名称
}
