package com.ylzs.vo.sewing;

import com.ylzs.entity.sewingcraft.SewingCraftAction;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @className VSewingCraftVo
 * @Description
 * @Author sky
 * @create 2020-04-01 15:53:09
 */
@Data
public class VSewingCraftVo implements Serializable {

    private static final long serialVersionUID = -3789599703788283685L;
    private Long id;
    /****/
    private Long randomCode;
    /****/
    private Integer status;
    /**工序流**/
    private String craftFlowNum;
    /**工序编码**/
    private String craftCode;
    /**工序名称**/
    private String craftName;
    /**工序描述**/
    private String description;
    /**工艺品类编码**/
    private String craftCategoryCode;
    /**工艺品类名称**/
    private String craftCategoryName;
    /**部件工艺名称**/
    private String craftPartName;
    /**部件工艺编码**/
    private String craftPartCode;
    /**做工类型编码**/
    private String makeTypeCode;
    /**工序等级**/
    private String craftGradeCode;
    /**机器编码**/
    private String machineCode;
    /**机器名称**/
    private String machineName;
    /**
     * 宽放系数关联代码（宽放系数的random_code）
     */
    private Long allowanceRandomCode;
    /**宽放系数**/
    private BigDecimal allowanceCode;
    /**捆扎时间**/
    private BigDecimal strappingCode;
    /**是否面料风格补贴**/
    private Boolean isFabricStyleFix;
    /**面料等级**/
    private String fabricGrade;
    /**标准时间**/
    private BigDecimal standardTime;
    /**标准单价**/
    private BigDecimal standardPrice;
    /**面料分值方案编码**/
    private String fabricScorePlanCode;
    /**工段编码**/
    private String sectionCode;
    /**工种编码**/
    private String workTypeCode;
    /**面料系数**/
    private BigDecimal fabricTimeCoefficient;
    /**缝边位置**/
    private String partPositionCode;
    /**缝边位置名称**/
    private String partPositionName;
    /**订单等级**/
    private Integer orderGrade;
    /**做工说明**/
    private String makeDescription;
    /**品质要求**/
    private String qualitySpec;
    /**固定时间**/
    private Integer fixedTime;
    /**浮动时间**/
    private Integer floatingTime;
    /**车缝长度**/
    private String sewingLength;
    /**参数长度**/
    private Integer paramLength;
    /**备注**/
    private String remark;

    private String craftDescript;

    //动作代码
    private  List<SewingCraftAction> motionsList;

    /**
     * 针距
     */
    private BigDecimal stitchLength;

    /**
     * 转速
     */
    private Float rpm;
    private String bigStyleAnalyseCode;
}
