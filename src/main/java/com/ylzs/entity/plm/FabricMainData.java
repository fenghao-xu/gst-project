package com.ylzs.entity.plm;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: watermelon.xzx
 * @Description:PI材料主数据接收(面料主数据)
 * @Date: Created in 8:45 2020/3/11
 */
@Data
@TableName(value = "capp_pi_material_main_data")
public class FabricMainData implements Serializable {
    private static final long serialVersionUID = -4493058151048217132L;
    private Integer id;
    private String name;//物料名称
    private String code;//物料编码
    private String grandCategory;//物料大类名称
    private String midCategory;//物料中类名称
    private String smallCategory;//物料小类名称
    private String brand;//品牌
    private String year;//年份(Year)
    private String band;//波段
    private String productionLine;//产线类别
    private BigDecimal width;//合同门幅（cm）
    private BigDecimal actualWidth;//实际门幅（cm）
    private String unit;//单位
    private String materialStyle;//表面风格
    private String systemCode; //颜色代码
    private String systemName; //颜色名称
    private BigDecimal warpElasticExtension;//经向弹力伸长率
    private BigDecimal warpStatus;//经向塑性变形率
    private String warpElasticGrade;//经向弹性等级
    private BigDecimal weftElasticExtension;//纬向弹力伸长率
    private BigDecimal weftStatus;//纬向塑性变形率
    private String weftElasticGrade;//纬向弹性等级
    private String draping;//悬垂系数
    private String drapingGrade;//悬垂性等级
    private String airPermeability;//透气性
    private String airPermeabilityGrade;//透气性等级
    @TableField(value = "HY_grade")
    private String HYGrade;//纱线滑移性
    private String hardnessGrade;//硬柔度
    @TableField(value = "CX_grade")
    private String CXGrade;//表面粗细度
    private String applyProduct;//适用产品
    private String density;//纱支密度
    private String densityGrade;//纱支密度等级
    private BigDecimal weight;//克重(g/m2)
    private String weightGrade;//克重等级
    private BigDecimal warpWaterShrinkage;//水缩-横向
    private BigDecimal weftWaterShrinkage;//水缩-纵向
    private BigDecimal warpHeatShrinkage;//经向热缩率
    private BigDecimal weftHeatShrinkage;//纬向热缩率
    private String composition;//面料成分（fiber content)
    private String creator;//创建人
    private String inWarehouseDate;//入库年月
    private String dispatchPlace;//发运地
    private BigDecimal price;//价格
    private BigDecimal bulkProductionLeadTime;//大货货期/天
    private String supplyMaterialCode;//供应商面料编号
    private String supplierCode;//供应商名称与编号
    private String remark;//备注
    private String materialGrade;//面料难度综合评定
    private String materialDirection;//光差属性
    private String materialMotif;//是否对条对格或定位花
    private String patternSymmetry;//条格类型
    private BigDecimal warpstart;//条格边距
    private BigDecimal warpRepeat;//条格经向循环尺寸
    private BigDecimal warpRepeatLine1;//横条间隔 1
    private BigDecimal warpRepeatLine2;//横条间隔 2
    private BigDecimal warpRepeatLine3;//横条间隔 3
    private BigDecimal warpRepeatLine4;//横条间隔 4
    private BigDecimal warpRepeatLine5;//横条间隔 5
    private String weftstart;//竖条格起始位置
    private BigDecimal weftRepeat;//条格纬向循环尺寸
    private BigDecimal weftRepeatLine1;//竖条间隔 1
    private BigDecimal weftRepeatLine2;//竖条间隔 2
    private BigDecimal weftRepeatLine3;//竖条间隔 3
    private BigDecimal weftRepeatLine4;//竖条间隔 4
    private BigDecimal weftRepeatLine5;//竖条间隔 5
    private String colorShade;//色差
    private BigDecimal lengthSpec;//配料规格（长度）
    private String spec;//配料规格（其他）
    private BigDecimal coloSadeValue;//段差数值
    private String remarkOfTesting;//检测结果备注
    @TableField(value = "testing_ok")
    private Boolean testingOK;//材料检测OK
    private String lightnessCode;//明度
    private String vividnessCode;//艳度
    private String yanduDescription;//冷艳度描述
    private String hueCode;//色相号
    private String colorSystemName;//大色相名称
    private String colorSystemCode;//大色相编码
    private String dropColorName;//吊牌颜色名称
    private String description;//颜色描述
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateDate;//更新时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;//接收时间
}
