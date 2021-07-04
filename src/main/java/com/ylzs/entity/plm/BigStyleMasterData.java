package com.ylzs.entity.plm;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author:tt
 * @Description： 大货款主数据
 * @Date: Created in 2020/3/14
 */
@Data
public class BigStyleMasterData implements Serializable {
    private static final long serialVersionUID = 1970990995044166458L;
    private int id;
    private String OEMClientCode;      //OEM客户编号
    private String OEMStyleCode;       //OEM客户款号
    private String yqCombinationType;      //衣裙连接方式
    private String actionType;      //操作类型
    private String categoryType;      //超类
    private String styleType;     //款式类型
    private String ctStyleCode;     //物料编码(款号) 级联子表
    private String styleName;     //名称
    private String brand;     //品牌
    private String mainMaterialType;     // 新旧面料
    private String subBrand;  //子品牌
    private String year;  //年份
    private String belongWithCustomer;  //客户款式智化
    private String band;  //波段
    private String season;  //季节
    private String bodyType;  //衣身型
    private String collar;  //领型
    private String sleeve;  //袖肩型
    private String wayOfTakingOff;  //脱卸方式
    private String cuff;  //袖口型
    private String malibu;  //里布形式
    private String pocket;  //口袋型（多选）
    private String dege;  //袋盖型
    private String belt;  //装饰带
    private String trouserLoop;  //耳袢
    private String stitch;  //外观线迹（多选）
    private String decorate;  //装饰细节（多选）
    private String footMouth;  //脚口结构
    private String waistBand;  //腰头型
    private String waistMouthType;  //腰面嘴型
    private String lumbarPosition;  //腰位分类
    private String trouserLegType;  //裤腿型
    private String rainType;  //雨搭型
    private String entranceGuard;  //门襟挂面
    private String weave2;  //织法新
    private String grandCategory;  //大类
    private String midCategory;  //中类
    private String smallCategory;  //小类
    private String bigGoodsPrice;  //吊牌价
    private String intProcessFee;  //内部加工费
    private String outProcessFee;  //外部加工费
    private String weave;  //织法
    private String isKnitTatMatch;  //是针梭配
    private String supplySourceNew;  //新供应来源
    private String ornamentExplain;  //备注
    private String noDiscountNew;  //款式定位
    private String packingMethodCoat;  //包装方式
    private String isHaveWX;  //是否有外协工序
    private String setStylecode;  //套款款号
    private String isCustomizable;  //是否可传统定做
    private String isPersonalTailor;  //是私人定制
    private String intellectualizationIdentification;  //款式智化标示
    private String intelligentProduction;  //可上智能产线
    private String patterning;//图案装饰（多选）

    //private List<BigStyleMasterDataColor> bigStyleMasterDataColorList;//用于装子表数据，这个字段不存储数据库
    private List<BigStyleMasterDataWXProcedures> WXProcedures;//用于装子表数据，这个字段不存储数据库
    private List<BigStyleMasterDataSKC> skc;//用于装子表数据，这个字段不存储数据库
    //面料分值----面料难度等级
    private Integer materialGrade;

    //面料分值----经向弹性等级
    private String warpElasticGrade;

    //面料分值----纬向弹性等级
    private String weftElasticGrade;

    //面料分值----面料难度等级
    private transient  Integer fabricFraction;

    private transient String subBrandName;

    private transient String grandCategoryName;

    /**
     * 工艺品类代码
     */
    private transient String craftCategoryCode;
    /**
     * 工艺品类名称
     */
    private transient String craftCategoryName;

    private transient Date forSalesTime;//上市日期

}
