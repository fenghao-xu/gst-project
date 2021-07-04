package com.ylzs.dao.craftstd;

import com.ylzs.entity.craftstd.MakeType;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-25 16:59
 */
public interface MakeTypeDao {
    Integer addMakeType(MakeType makeType);

    Integer deleteMakeType(String makeTypeCode);

    Integer updateMakeType(MakeType makeType);

    List<MakeType> getMakeTypeByCode(@Param("makeTypeCodes") String[] makeTypeCodes);

    List<MakeType> getMakeTypeByPage(@Param("keywords") String keywords,
                                     @Param("beginDate") Date beginDate,
                                     @Param("endDate") Date endDate,
                                     @Param("workTypeId") Integer workTypeId);

    Long getStdCountByMakeTypeCode(String makeTypeCode);

    MakeType getMakeTypeById(Integer id);

    public List<MakeType> getAllMakeType();

    @MapKey("makeTypeCode")
    Map<String, MakeType> getMakeTypeMap();
}