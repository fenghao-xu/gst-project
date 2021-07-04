package com.ylzs.dao.timeparam;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.craftstd.MakeType;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.timeparam.StrappingTimeConfig;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-02-27 16:00
 * update watermelon.xzx
 */
public interface StrappingTimeConfigDao extends BaseDAO<StrappingTimeConfig> {

    /**
     * 获取所有的捆扎系数
     */
    List<StrappingTimeConfig> getAllStrappingTimeConfigs();

    Integer addStrappingTime(StrappingTimeConfig strappingTimeConfig);

    Integer deleteStrappingTime(@Param("strappingTimeList") List<StrappingTimeConfig> strappingTimeList);

    Integer updateStrappingTime(StrappingTimeConfig strappingTimeConfig);

    //List<StrappingTimeConfig> getStrappingTimeByCode(@Param("strappingCode") String[] strappingCode);

    //Long getStdCountByMakeTypeCode(String strappingCode);

    StrappingTimeConfig getStrappingTimeByStrappingCode(String strappingCode);

    List<StrappingTimeConfig> getStrappingTimeByCode(@Param("strappingCodeList") List<String> strappingCodeList);

    List<StrappingTimeConfig> getStrappingTimeByPage(@Param("keywords") String keywords,
                                     @Param("beginDate") Date newbeginDate,
                                     @Param("endDate") Date  newendDate);



}
