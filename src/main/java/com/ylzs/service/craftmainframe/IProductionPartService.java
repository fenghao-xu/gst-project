package com.ylzs.service.craftmainframe;

import com.ylzs.entity.craftmainframe.FlowNumConfig;
import com.ylzs.entity.craftmainframe.ProductionPart;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 生产部件服务接口
 * @author: lyq
 * @date: 2020-03-05 16:17
 */

public interface IProductionPartService {
    int deleteByPrimaryKey(Long randomCode);

    int insert(ProductionPart record);

    int insertSelective(ProductionPart record);

    ProductionPart selectByPrimaryKey(Long randomCode);

    int updateByPrimaryKeySelective(ProductionPart record);

    int updateByPrimaryKey(ProductionPart record);

    int batchInsert(List<ProductionPart> list);

    List<ProductionPart> getByMainFrameRandomCode(Long mainFrameRandomCode);
    List<ProductionPart> getByCondition(Long craftCategoryRandomCode, Long mainFrameRandomCode,
                                        String keywords, Date beginDate, Date endDate);
    public  Map<String, ProductionPart> getMapByMainFrameCode(String mainFrameCode);


    List<FlowNumConfig> getFlowNumConfigAll();


}


