package com.ylzs.entity.plm;

import lombok.Data;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
@Data
public class CutParameterMasterDataDosages {
    private int id;
    private String  orderId;//订单ID
    private String  orderLineId   ;  //订单行ID
    private String  code   ;  //材料编码
    private String  dosage   ;  //材料用量
}
