package com.ylzs.service.datadictionary;

import com.ylzs.entity.datadictionary.DictionaryType;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: watermelon.xzx
 * @Description:字典值类型
 * @Date: Created in 15:26 2020/2/27
 */
public interface IDictionaryTypeService {
    Integer addDictionaryType(DictionaryType dictionaryType);

    Integer deleteDictionaryType(long id);

    Integer deleteDictionaryTypeByCode(List<DictionaryType> DictionaryTypes, String userCode);

    Integer updateDictionaryType(DictionaryType dictionaryType);

    List<DictionaryType> getDictionaryTypeByPage(@Param("keywords") String keywords, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    DictionaryType getDictionaryTypeById(long id);

    List<DictionaryType> getDictionaryTypeByCode(List<String> codes);

    DictionaryType getOneBydictionaryTypeCode(String dictionaryTypeCode);

    List<DictionaryType> getAll();



}
