package com.ylzs.dao.timeparam;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.timeparam.StrappingTimeConfig;
import com.ylzs.entity.timeparam.WideCoefficient;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-02-27 15:34
 */
public interface WideCoefficientDao extends BaseDAO<WideCoefficient> {
    /**
     * 获取所有的宽放系数
     */
     List<WideCoefficient> getAllWideCoefficient();

    /**
     * 根据品类查询对应的宽放系数
     */
     List<WideCoefficient> getWideCoefficientByCraftCatagory();

    List<WideCoefficient> getWideCoefficientByCode(@Param("wideCodeList") List<String> wideCodeList);

    Integer deleteWideCoefficient(@Param("wideCoefficientList") List<WideCoefficient> wideCoefficientList);

    Integer deleteByWideCode(WideCoefficient wideCoefficient);

    List<WideCoefficient> getWideCoefficientByPage(@Param("keywords") String keywords,
                                                     @Param("beginDate") Date newbeginDate,
                                                     @Param("endDate") Date  newendDate);

    WideCoefficient getWideCoefficientByWideCode(String wideCode);

    void addWideCoefficient (@Param("wideCoefficientList") List<WideCoefficient> wideCoefficientList);

    void updateByWideAndCategory (@Param("wideCoefficientList") List<WideCoefficient> wideCoefficientList);


    void deleteCoefficientCode(@Param("wideCode")String wideCode);
}
