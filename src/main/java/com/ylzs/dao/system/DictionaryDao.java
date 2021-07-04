package com.ylzs.dao.system;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.vo.DictionaryVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-25 14:30
 */
@Mapper
public interface DictionaryDao extends BaseMapper<Dictionary> {
    Integer addDictionary(Dictionary dictionary);

    Integer deleteDictionary(@Param("id") Integer id);

    Integer updateDictionary(Dictionary dictionary);

    List<Dictionary> getDictionaryById(@Param("id") Integer id);

    List<Dictionary> getDictionaryByParentId(@Param("parentId") Integer parentId);

    List<Dictionary> getDictionaryByParent(@Param("parentCode") String parentCode, @Param("parentId") Integer parentId);

    List<Dictionary> getDictionaryByPage(@Param("parentId") Integer parentId,
                                         @Param("keywords") String keywords,
                                         @Param("beginDate") Date beginDate,
                                         @Param("endDate") Date endDate);

    List<Dictionary> getLineDictionary(@Param("linePositionId") Integer linePositionId,
                                       @Param("lineNameId") Integer lineNameId);

    List<Dictionary> getRoleByUserCode(@Param("userCode") String userCode);

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictoryTypeCode
     * @return
     */
    @Select("SELECT * FROM dictionary WHERE IFNULL(is_invalid,0)=0 and dictionary_type_code = #{dictoryTypeCode} ")
    List<Dictionary> getDictoryAll(String dictoryTypeCode);

    public List<Dictionary> getDictionaryByTypeCode(String dictoryTypeCode);

    //void addOrUpdate(Dictionary data);

    List<DictionaryVo> getDictoryValueList(Map map);

    List<Dictionary> getAll();

    List<Dictionary> getNoParentCode();

    void addDictionaryList(List<Dictionary> dictionaryList);

    void updateDictionaryList(List<Dictionary> dictionaryList);

    List<Dictionary> getDictoryByValueAndType(@Param("dicValue") String dicValue,@Param("dictionaryTypeCode") String dictionaryTypeCode );

    /**
     * 查询指定typeCode的字典值，并且把 dic_value和typecode 作为key放入map，方便快速查询
     */
    @MapKey("mapKey")
    public Map<String,Dictionary>getValueAndTypeCodeAsMapKey(@Param("typeCodeList") List<String>typeCodeList);
    public String getBrandName(String brandCode);

    @MapKey("dicValue")
    Map<String,Dictionary> getDictionaryMap(@Param("dictionaryTypeCode") String dictionaryTypeCode);

    void updateAndAddDictionary(@Param("dictionaryList")List<Dictionary> dictionaryList);

}
