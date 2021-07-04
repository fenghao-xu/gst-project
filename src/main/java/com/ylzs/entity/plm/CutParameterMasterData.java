package com.ylzs.entity.plm;

import lombok.Data;

import java.util.List;

/**
* @Author:tt
* @Description： 定制款裁决参数主数据
* @Date: Created in 2020/3/18
*/
@Data
public class CutParameterMasterData {
    //private int id;
    private String  orderId;//订单ID
    private String  orderLineId   ;  //订单行ID
    private String  patternFileUrl   ;  //纸样文件URL  -> PLM
    private String styleCode;
    private List<CutParameterMasterDataDosages> dosages;//材料用量列表(数组)
    private List<CutParameterMasterDataSizes> sizes;//尺码表 （数组）
    private List<CutParameterMasterDataMarkInfo> markerInfo;//排料汇总信息

    private List<CutParameterMasterDataMaterialMarkers> materialMarkers;//用于接收markerTables床次信息列表
}
