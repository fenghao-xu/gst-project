package com.ylzs.entity.plm;

import lombok.Data;

import java.io.Serializable;

/**
* @Author:tt
* @Description：大货主数据 -SKC-material子表
* @Date: Created in
*/
@Data
public class BigStyleMasterDataSKCMaterial implements Serializable {
    private static final long serialVersionUID = -8750277063712064472L;
    private int id;
    private String itemCode    ;  //BOM行号
    private String materialUse   ;  //物料用途
    private String materialCode    ;  //材料编码
    private String bomRemark    ;  //BOM备注
    private Boolean isMainMaterial    ;  //是否主面料
    private String materialMiddleType    ;  //材料大类
    private String materialSmallType    ;  //材料中类
    private String materialColor    ;  //材料颜色
    private String colorSectionCode    ;  //色区区号
    private String materialType    ;  //材料类型
    private String materialStyle    ;  //材料小类（即材料表面风格）
    private String  materialGrade   ;  //材料综合难度等级
    private String weight    ;  //克重
    private String  weightGrade   ;  //克重等级
    private String weftElasticGrade    ;  //纬向弹性等级
    private String  warpElasticGrade   ;  //经向弹性等级
    private String  drapingGrade   ;  //悬垂性等级
    private String  hYGrade   ;  //滑移性
    private String  styleSkcCode   ;  //SKC款号编码 用于级联

}
