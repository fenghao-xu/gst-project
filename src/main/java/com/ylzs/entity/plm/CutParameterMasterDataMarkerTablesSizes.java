package com.ylzs.entity.plm;

import lombok.Data;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
@Data
public class CutParameterMasterDataMarkerTablesSizes {
    private int id;
    private String  orderId;//订单ID
    private String  orderLineId   ;  //订单行ID
    private String  index   ;  //床次编号
    private String  size   ;  //尺码
    private String  count  ;  //件数
}

