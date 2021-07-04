package com.ylzs.entity.plm;

import lombok.Data;

/**
 * @Author: watermelon.xzx
 * @Description: 智库款、智化大货款传递EBOM->GST,CAD后给到MTM
 * @Date: Created in 10:18 2020/3/16
 */
@Data
public class ProductModelEBomData {
    private String bomLineId;//PLM BOM行ID号
    private String componentType;//部位/部件类型
    private String isComponent;//是否部件
    private String defaultComponent;//部件编号
    private String optionalComponents;//可选部件编号
    private String belongToComponent;//所属顶层部件号
    private String parentComponent;//上级部件编号
    private String isMainComponentOrMaterial;//是否主部件/材料
    private String canBeCancelled;//可取消
    private String canBeCustomized;//可定制
    private String embroideryPosition;//喜好位置
    private String unitPositionCode;//部件位置
    private String mtmPositionCode;//MTM位置
    private String embroideryPositionValue;//缝边位置
    private String materialLargeType;//材料大类
    private String materialMidType;//材料中类
    private String defaultMaterial;//默认材料编号
    private String sameAsParentMaterial;//随上级面料
    private String sameAsComponentMaterial;//随部位面料
    private String routing;//旋转角度
    private String group;//关系组





}
