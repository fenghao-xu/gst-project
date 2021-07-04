package com.ylzs.service.craftstd;

import com.ylzs.entity.craftstd.MakeType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：做工类型服务接口
 *
 * @author Administrator
 * 2019-09-30 11:11
 */
public interface IMakeTypeService {
     Integer addMakeType(MakeType makeType);

     Integer deleteMakeType(String makeTypeCode, String userCode);

     Integer updateMakeType(MakeType makeType);

     List<MakeType> getMakeTypeByCode(String[] makeTypeCodes);

     List<MakeType> getMakeTypeByPage(String keywords, Date beginDate, Date endDate, Integer workTypeId);

     MakeType getMakeTypeById(Integer id);

     List<MakeType> getAllMakeType();

     Map<String, MakeType> getMakeTypeMap();
}
