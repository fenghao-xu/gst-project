package com.ylzs.entity.plm;

import lombok.Data;

import java.util.List;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
@Data
public class CutParameterMasterDataSizes {
    private int id1;
    private String  orderId;//订单ID
    private String  orderLineId   ;  //订单行ID
    private String  id   ;  //尺码编码
    private String   name  ;  //尺码名称

    private List<CutParameterMasterDataSizesSpecs> specs;//部位列表（数组）
}
