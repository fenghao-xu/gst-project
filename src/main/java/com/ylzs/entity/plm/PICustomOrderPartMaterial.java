package com.ylzs.entity.plm;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 17:27 2020/3/18
 */
@Data
public class PICustomOrderPartMaterial {
    private Integer id;
    private String orderId;//订单号
    private String orderLineId;//订单行号
    private Long parentRandomCode;//部件随机码
    private String code;//材料编号
    private String materialName;//材料名称
    private String patternSymmetry;//条格对称
    private String grandCategory;//材料大类
    private String midCategory;//材料中类
    private String smallCategory;//材料小类
    private Integer materialGrade;//材料综合难度等级
    private String materialStyle;//面料表面风格
    private String weight;//克重
    private String weightGrade;//克重等级
    private String weftElasticGrade;//维向伸长率等级
    private String warpElasticGrade;//经向伸长率等级
    private String drapingGrade;//悬垂性等级
    private String draping;//悬垂性系数
    private String HYGrade;//滑移性
    private String hardnessGrade;//硬柔度
    private String thickGrade;//表面粗密度
    private String materialDirection;//光差属性
    private String positionCode;//缝边位置编码
    private String buttonhole;//扣子孔数
    private String clasp;//扣子钩数
    private String rhinestoneHole;//珠片烫钻孔数
    private String materialNumber;//数量
    private String mtmPositionCode;//mtm位置编码
    private String colorSystemCode;//大色相编号
    private String colorSystemName;//大色相名称
    private Boolean isMain;//大色相名称
    private String systemCode;//小色相编号（颜色code）
    private String systemName;//小色相名称（颜色名称）
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;//接收时间

    /******************特殊用途，不需要和数据库字段相对应****************************/
    /**
     * 材料属性编码
     */
    private transient String fabricPropertyCode;

    /**
     * 材料属性名称
     */
    private transient String fabricPropertyName;

    /**
     * 属性值
     */
    private transient String propertyValue;
    /******************END****************************/


}
