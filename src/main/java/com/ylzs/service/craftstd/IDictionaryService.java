package com.ylzs.service.craftstd;

import cn.hutool.core.lang.Dict;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.vo.DictionaryVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：字典服务接口
 *
 * @author Administrator
 * 2019-09-30 14:22
 */
public interface IDictionaryService {
    Integer addDictionary(Dictionary dictionary);

    Integer deleteDictionary(int id);

    Integer updateDictionary(Dictionary dictionary);

    List<Dictionary> getDictionaryById(Integer id);

    List<Dictionary> getDictionaryByParent(String parentCode, Integer parentId);

    List<Dictionary> getDictionaryByPage(Integer parentId, String keywords, Date beginDate, Date endDate);

    List<Dictionary> getDictionaryByParentId(Integer parentId);

    List<Dictionary> getLineDictionary(Integer linePositionId, Integer lineNameId) throws Exception;

    public List<Dictionary> getDictionaryByTypeCode(String dictoryTypeCode);

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictoryTypeCode
     * @return
     */
    List<DictionaryVo> getDictoryAll(String dictoryTypeCode);

    public Map<String,Dictionary> getValueAndTypeCodeAsMapKey(@Param("typeCodeList") List<String>typeCodeList);


    /**
     * 根据属性编码、属性值查询数据
     *
     * @param kindCode
     * @param valueDesc
     * @return
     */
    List<DictionaryVo> getDictoryValueList(String kindCode, String valueDesc);

    List<Dictionary> getAll();

    List<Dictionary> getNoParentCode();

    void addDictionaryList(List<Dictionary> dictionaryList);

    void updateDictionaryList(List<Dictionary> dictionaryList);

    void updateAndAddDictionary(List<Dictionary> dictionaryList);

    List<Dictionary> getDictoryByValueAndType(String dicValue,String dictionaryTypeCode );

    public String getBrandName(String brandCode);
}
