package com.ylzs.dao.datadictionary;

import com.ylzs.entity.datadictionary.DictionaryType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 17:36 2020/2/26
 */
@Mapper
public interface DictionaryTypeDao {
    Integer addDictionaryType(DictionaryType dictionaryType);
    Integer deleteDictionaryType(long id);
    Integer deleteDictionaryTypeByCode(List<DictionaryType> DictionaryTypes);
    Integer updateDictionaryType(DictionaryType dictionaryType);
    //List<DictionaryType> getDictionaryTypeByCode(@Param("dictionaryTypeCode") String[] dictionaryTypeCode);
    List<DictionaryType> getDictionaryTypePage(Map<String,Object> map);
    DictionaryType getDictionaryTypeById(long id);
    List<DictionaryType> getDictionaryTypeByCode(@Param("list") List<String> dictionaryTypeCode);

    DictionaryType getOneBydictionaryTypeCode(String dictionaryTypeCode);

    List<DictionaryType> getAll();

}
