package com.ylzs.entity.sewingcraft;

/**
 * @author xuwei
 * @create 2020-02-25 14:56
 */

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.lang.String;

/**
 * 车缝工序词库
 */
@Data
public class SewingCraftWarehouse extends SuperEntity {
    private String craftCode;// 工序编号
    private String craftName;// 工序名称
    private String craftCategoryCode;// 工艺品类编号
    private String craftCategoryName;// 工艺品类名称
    private String craftPartCode;// 工艺部件编码
    private String craftPartName;// 工艺部件名称
    private String description;// 工序描述
    private String sectionCode;// 工段代码
    private String workTypeCode;// 工种代码
    private String workTypeName;// 工种代码
    private String makeTypeCode;// 做工类型代码
    private String makeTypeName;// 做工类型代码
    private BigDecimal allowanceCode;// 宽放系数
    private String allowanceRandomCode;// 宽放系数
    private BigDecimal strappingCode;// 捆扎时间
    private String craftGradeCode;// 工序等级
    private String isFabricStyleFix;// 是否面料风格补贴
    private BigDecimal standardTime;// 标准时间
    private BigDecimal standardPrice;// 标准单价
    private String remark;// 备注
    private String machineCode;// 机器类型代码
    private String machineName;// 机器名称
    private String helpToolCode;// 辅助工具代码
    private String makeDescription;// 做工说明
    private String qualitySpec;// 品质要求
    private String fabricScorePlanCode;// 面料分值方案编码
    private BigDecimal baseStandardTime;// 基础标准时间
    private BigDecimal baseStandardPrice;// 基础标准单价
    /**
     * 发布人
     */
    private String releaseUser;

    private String releaseUserName;

    private String createUserName;
    private String updateUserName;
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;
    private Integer fixedTime;//固定时间
    private Integer floatingTime;//浮动时间
    private Integer sewingLength;//车缝长度
    private Integer paramLength;//参数长度

    private Integer sysnStatus;//同步到工序词库状态

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sysnTime;//同步到工序词库状态

    //工序建标的线稿图
    private List<String> sketchPicUrl;
    //工序建标的视频
    private List<String> vedioUrl;
    //工位工序
    private List<String> workplaceNameList;

    //工艺品类
    private List<JSONObject> craftCategoryList;
    //结构部件
    private List<JSONObject> craftPartList;

    //动作代码
    private List<SewingCraftAction> motionsList;

    /**
     * 工序流
     */
    private Integer craftFlowNum;

    /**
     * 主框架名称
     */
    private String mainFrameName;

    /**
     * 主框架编码
     */
    private String mainFrameCode;

    /**
     * 时间浮余
     */
    private BigDecimal timeSupplement;

    /**
     * 面料分值
     */
    private Integer fabricFraction;

    /**
     * 面料等级
     */
    private Integer fabricGradeCode;
    /**
     * 订单等级
     */
    private Integer orderGrade;
    /**
     * 面料系数
     */
    private BigDecimal fabricRatio;
    /**
     * 部件工艺编码
     */
    private String partCraftMainCode;

    /**
     * 款式工艺--》部件工艺详情里面的id，用于前端匹配
     */
    private Long partDetailId;
    /**
     * 状态的中文
     */
    private String statusName;
    /**
     * 站位
     */
    private String station;
    /**
     * 站位设备
     */
    private String stationDevice;

    /**
     * 针距
     */
    private BigDecimal stitchLength;
    /**
     * 同级工序代码
     */
    private String sameLevelCraftNumericalCode;

    /**
     * 同级工序名称
     */
    private String sameLevelCraftName;
    /**
     * /**
     * 是否图片、视频、做工说明、品质要求、辅助工具不发送给ME
     */
    private Boolean isCancelSend;

    /**
     * 是否发送线稿图到裁剪
     */
    private Boolean isSendCutPic;

    private String bigStyleAnalyseCode;

    /**
     * 生产工单号
     */
    private String productionTicketNo;

    /**
     * 是否有线稿图
     */
    private transient  Boolean isHandPic;
    /**
     * 是否有视频
     */
    private transient Boolean isVideo;
    /**
     * 建标状态
     * 10  未建标
     * 20  已建标
     */
    private transient Integer craftStdStatus;

    /**
     * 建标创建人
     */
    private transient String stdCreateUser;

    /**
     * 建标创建时间
     */
    private transient String stdCreateTime;


    private String styleName;

    /**
     * 建标审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private transient Date okTime;
    private transient String clothesCategoryName;//服装品类

    private transient String craftStdCode;

    /**
     * 适宜性编码
     */
    private transient String adaptCode;

    private transient String styleDesc;//款式 描述

    private transient String ctStyleCode;//款式 描述

    //工序建标的线稿图
    private transient List<String> orderPictures;

    /**
     * 是否有关联工序 true 有 false没有
     */
    private Boolean havingCraftRelation;

    private String masUpdateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date masUpdateTime;

    /**
     * 实物图地址
     */
    private String effectPicUrls;

    /**
     * 是否是默认主框架（该品类）
     */
    private Boolean isDefault;

    /**
     * 机器浮余
     */
    private Float machineFloatover;

    /**
     * 关联工序创建人
     */
    private String relationCreateUser;

    /**
     * 关联工序创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date relationCreateTime;
}
