package com.ylzs.entity.custom;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ylzs.core.model.SuperEntity;
import com.ylzs.entity.craftstd.CraftStd;
import com.ylzs.entity.sewingcraft.SewingCraftPartPosition;
import com.ylzs.entity.sewingcraft.SewingCraftStd;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouseWorkplace;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 定制款部件工序
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-04-01 15:47:56
 */
@Data
public class CustomStylePartCraft extends SuperEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 工序流
     */
    private String craftFlowNum;
    /**
     * 序号
     */
    private Integer orderNum;
    /**
     * 工序编码
     */
    private String craftCode;
    /**
     * 工序名称
     */
    private String craftName;
    /**
     * 工序描述
     */
    private String craftDescript;
    /**
     * 工艺品类编号
     */
    private String craftCategoryCode;
    /**
     * 工序品类名称
     */
    private String craftCategoryName;
    /**
     * 工艺部件名称
     */
    private String craftPartName;
    /**
     * 工艺部件编码
     */
    private String craftPartCode;
    /**
     * 做工类型关联代码（做工类型make_type的random_code）
     */
    private String makeTypeCode;
    /**
     * 工序等级
     */
    private String craftGradeCode;
    /**
     * 机器编码
     */
    private String machineCode;
    /**
     * 机器名称
     */
    private String machineName;
    /**
     * 宽放系数关联代码（宽放系数的random_code）
     */
    private Long allowanceRandomCode;
    /**
     * 宽放系数
     */
    private BigDecimal allowanceCode;
    /**
     * 捆扎时间的random_code
     */
    private BigDecimal strappingCode;
    /**
     * 是否面料风格补贴 0是 1否
     */
    private Boolean isFabricStyleFix;
    /**
     * 面料等级
     */
    private String fabricGrade;
    /**
     * 标准时间
     */
    private BigDecimal standardTime;
    /**
     * 标准单价
     */
    private BigDecimal standardPrice;
    /**
     * 面料分值方案编码
     */
    private String fabricScorePlanCode;
    /**
     * 定制款部件code
     */
    private Long stylePartRandomCode;
    /**
     * 工段代码（字典的code）
     */
    private String sectionCode;
    /**
     * 工种代码（字典的code）
     */
    private String workTypeCode;
    /**
     * 面料系数
     */
    private BigDecimal fabricTimeCoefficient;
    /**
     * 时间浮余
     */
    private BigDecimal timeSupplement;
    /**
     * 面料分值
     */
    private Integer fabricScore;
    /**
     * 订单等级
     */
    private Integer orderGrade;
    /**
     * 做工说明
     */
    private String makeDescription;
    /**
     * 品质要求
     */
    private String qualitySpec;
    /**
     * 固定时间
     */
    private Integer fixedTime;
    /**
     * 浮动时间
     */
    private Integer floatingTime;
    /**
     * 车缝长度
     */
    private Integer sewingLength;
    /**
     * 参数长度
     */
    private Integer paramLength;
    /**
     * 来源工序
     */
    private String sourceCraftName;
    /**
     * 备注
     */
    private String remark;

    /**
     * 设计部件代码
     */
    private String designPartCode;
    /**
     * 设计部件名称
     */
    private String designPartName;
    /**
     * 缝边位置
     **/
    @TableField(exist = false)
    private String partPositionCode;

    /**
     * 缝边位置名称
     **/
    @TableField(exist = false)
    private String partPositionName;

    /**
     * 针距
     */
    private BigDecimal stitchLength;

    /**
     * 转速
     */
    private Float rpm;

    //工艺品类
    @TableField(exist = false)
    private List<JSONObject> craftCategoryList;
    //结构部件
    @TableField(exist = false)
    private List<JSONObject> craftPartList;
    @TableField(exist = false)
    List<CustomStylePartCraftMotion> stylePartCraftMotionList;

    //缝边位置
    @TableField(exist = false)
    private  List<SewingCraftPartPosition> sewPartPositionList;

    //工序工序
    @TableField(exist = false)
    private  List<SewingCraftWarehouseWorkplace> workplaceCraftList;

    //工艺建标
    @TableField(exist = false)
    private  List<CraftStd> craftStdList;
}
