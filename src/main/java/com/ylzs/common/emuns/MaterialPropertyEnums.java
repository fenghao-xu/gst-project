package com.ylzs.common.emuns;

/**
 * @className MaterialPropertyEnums
 * @Description
 * @Author sky
 * @create 2020-03-26 11:00:10
 */
public enum MaterialPropertyEnums{
    WarpExtensionRatio("WarpExtensionRatio","材料经向弹力等级"),
    WeftExtensionRatio("WeftExtensionRatio","材料纬向弹力等级"),
    WeightGrade("WeightGrade","材料克重等级"),
    MaterialCategoryL2("MaterialCategoryL2","材料中类"),
    YarnSlippage("YarnSlippage","纱线滑移性"),
    patternSymmetry("patternSymmetry","材料条格对称"),
    MaterialCategoryL3("MaterialCategoryL3","材料巨大类"),
    PrimaryHue("PrimaryHue","面料颜色(主色相)");

    private String code;
    private String desc;
    private MaterialPropertyEnums(String code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
