package com.ylzs.dao.custom;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.custom.CustomStyleCraftCourse;
import com.ylzs.vo.bigstylereport.CraftVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

//import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * 定制款工艺路线主数据表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:01:58
 */
@Mapper
public interface CustomStyleCraftCourseDao extends BaseDAO<CustomStyleCraftCourse> {

    public List<CustomStyleCraftCourse> searchCustomStyleCraftMasterAll(HashMap params);

    public List<CustomStyleCraftCourse> getOrderCustmStyleBaseList(String orderNo, String orderLineId);

    public Integer getCustomOrderLine(@Param("orderNo") String orderNo, @Param("orderLineId") String orderLineId);

    public CustomStyleCraftCourse getOrderCustomStyleByVersion(String orderNo, String orderLineId, String releaseVersion);

    public CustomStyleCraftCourse getOrderCustomStyleByRandomCode(Long randomCode);

    public List<CustomStyleCraftCourse> queryCustomOrderForTheCurrentDateList();

    public int updateCustomMaster(CustomStyleCraftCourse customStyleCraftCourse);

    public List<CustomStyleCraftCourse> getDataByUniqueKey(@Param("orderNo") String orderNo, @Param("orderLineId") String orderLineId, @Param("releaseVersion") String releaseVersion);

    int deleteByRandomList(@Param("arrays") List<Long> randomCodeList);

    List<CraftVO> getDataForExcelReportByCustomRandomCode(@Param("randomCode") Long randomCode);

    List<CraftVO> getDataForExcelReportOrderByCustomWorkOrder(@Param("randomCode") Long randomCode);

    boolean isCustomOrderExist(@Param("orderNo") String orderNo, @Param("orderLineId") String orderLineId, @Param("releaseVersion") String releaseVersion);
}
