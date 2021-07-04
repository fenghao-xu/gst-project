package com.ylzs.service.custom;


import com.ylzs.entity.custom.CustomStyleCraftCourse;
import com.ylzs.entity.custom.CustomStylePartCraft;
import com.ylzs.entity.custom.CustomStylePartCraftMotion;
import com.ylzs.entity.custom.CustomStyleSewPosition;
import com.ylzs.entity.plm.PICustomOrder;
import com.ylzs.entity.plm.PICustomOrderPartMaterial;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.bigstylereport.CraftVO;

import java.util.HashMap;
import java.util.List;

/**
 * 定制款工艺路线主数据表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:01:58
 */
public interface ICustomStyleCraftCourseService extends IOriginService<CustomStyleCraftCourse> {

    boolean createCustomStyleCratCouresData(PICustomOrder customOrder);

    /**
     * 根据条件查询定制款订单工艺数据
     * @param params
     * @return
     */
    public List<CustomStyleCraftCourse> searchStyleCraftCourseList(HashMap params);


    /**
     * 根据订单号获取定制款订单所有版本数据
     * @param orderNo
     * @return
     */
    public List<CustomStyleCraftCourse> getOrderCustmStyleBaseList(String orderNo,String orderLineId);

    public CustomStyleCraftCourse getCustomStyleCraftCourse(String orderNo,String orderLineNo);


    /**
     * 根据订单号和订单行号查询是否已存在订单数据
     * @param orderNo
     * @param orderLineId
     * @return
     */
    public Integer getCustomOrderLine(String orderNo,String orderLineId);

    int deleteByRandomList( List<Long> randomCodeList);

    public CustomStyleCraftCourse getOrderCustomStyleByVersion(String orderNo,String orderLineId,String releaseVersion);
    /**
     * 计算工序费用
     * @param mainFabric
     * @param customStylePartCraftList
     * @param motionList
     * @param craftPositionList
     * @return
     */
    public List<CustomStylePartCraft> calculateCost(String factoryCode,PICustomOrderPartMaterial mainFabric,
                                                    List<CustomStylePartCraft> customStylePartCraftList,
                                                    List<CustomStylePartCraftMotion> motionList,
                                                    List<CustomStyleSewPosition> craftPositionList);

    public CustomStyleCraftCourse getOrderCustomStyleByRandomCode(Long randomCode);

    public Boolean changeCustomStyleOrderInfo(CustomStyleCraftCourse customStyleCraftCourse,Integer status);

    public Boolean releaseCustomStyle(CustomStyleCraftCourse craftCourse);

    public List<CustomStyleCraftCourse> queryCustomOrderForTheCurrentDateList();
    public Boolean removeAllCustomStyleDetailData(Long randomCode);

    void createCustomStyleByBigStyle(String orderId, String orderLineId);

    List<CraftVO> getDataForExcelReportByCustomRandomCode(Long randomCode);

    List<CraftVO> getDataForExcelReportOrderByCustomWorkOrder(Long randomCode);

    boolean isCustomOrderExist(String orderNo, String orderLineId, String releaseVersion);
}

