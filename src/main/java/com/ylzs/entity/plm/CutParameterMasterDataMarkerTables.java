package com.ylzs.entity.plm;

import lombok.Data;

import java.util.List;

/**
* @Author:tt
* @Description：定制款裁决参数主数据接口
* @Date: Created in 2020/3/18
*/
@Data
public class CutParameterMasterDataMarkerTables {
    private int id;
    private String  orderId;//订单ID
    private String  orderLineId   ;  //订单行ID
    private String  index   ;  //床次编号
    private String  materialType   ;  //面料类型
    private String  plies   ;  //层数
    private String  length   ;  //幅长
    private String  width   ;  //幅宽
    private String   packages  ;  //件数
    private String  cutPartsCount   ;  //裁片数量
    private String   cutImageUrl  ;  //排料图URL
    private String  cutFileUrl   ;  //排料文件URL
    private String  cutFilename;//排料文件名

    private List<CutParameterMasterDataMarkerTablesSizes> sizes;//尺码信息列表（数组）
    private List<CutParameterMasterDataMarkerTablesColors> colors;//颜色信息列表（数组）

}
