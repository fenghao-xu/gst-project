package com.ylzs.entity.plm;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
* @Author:tt
* @Description：大货主数据-skc子表
* @Date: Created in
*/
@Data
public class BigStyleMasterDataSKC implements Serializable {
    private static final long serialVersionUID = 2039333001021762698L;
    private int id;
    private String  styleSKCcode   ;  //SKC款号编码
    private String  skcColorCode   ;  //SKC颜色编码
    private String  skcColorName   ;  //SKC颜色描述
    private String  skcImageURL   ;  //SKC款式图片（外网）
    private String  brightness   ;  //明度
    private String  ctStyleCode   ;  //物料编码(款号)  用于级联
    private transient  Long styleRandomCode;
    private List<BigStyleMasterDataSKCMaterial> materials;//用于装子表数据，这个字段不存储数据库

}
