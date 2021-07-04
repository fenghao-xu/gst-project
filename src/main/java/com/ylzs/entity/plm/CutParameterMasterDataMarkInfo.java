package com.ylzs.entity.plm;

import com.ylzs.vo.plm.CutParameterMasterDataMarkInfoHemsVO;
import lombok.Data;

import java.util.List;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
@Data
public class CutParameterMasterDataMarkInfo {
    private int id;
    private String  orderId;//订单ID
    private String  orderLineId   ;  //订单行ID
    private String  perimeter   ;  //二度裁剪总周长
    private String  dotCount   ;  //点位个数
    private String  singlePerimeter   ;  //单件总周长
    private String  knifeEdgeCount   ;  //刀口数
    private String  count   ;  //总床数
    private String  plies   ;  //订单总层数
    private List<CutParameterMasterDataMarkInfoHemsVO> hems;
}
