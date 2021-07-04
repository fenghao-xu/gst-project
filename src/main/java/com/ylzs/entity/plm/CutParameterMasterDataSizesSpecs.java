package com.ylzs.entity.plm;

import lombok.Data;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
@Data
public class CutParameterMasterDataSizesSpecs {
    private int id1;
    private String  orderId;//订单ID
    private String  orderLineId   ;  //订单行ID
    private String  id   ;  //尺码编码
    private String  code   ;  //部位编号
    private String  name   ;  //部位名称
    private String  value   ;  // 尺寸数值
}
