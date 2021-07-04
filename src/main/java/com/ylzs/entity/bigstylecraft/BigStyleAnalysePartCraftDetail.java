package com.ylzs.entity.bigstylecraft;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-24 9:30
 * 大货款式分析--部件工艺的详情
 */
@Data
public class BigStyleAnalysePartCraftDetail  {
    private Long randomCode;
    private Long id;
    /**
     * 工序代码
     */
    private String craftCode;
    /**
     * 工序名称
     */
    private String craftName;

    private String craftPartName;// 工艺部件名称

    /**
     * 工序描述
     */
    private String craftRemark;
    /**
     * 工序流
     */
    private String craftNo;

    /**
     * 工票号
     */
    private String workOrderNo;
    /**
     * 机器设备
     */
    private String machineCode;
    //机器设备名称
    private String machineName;
    /**
     * 部件工艺标准时间之和，单位分钟
     */
    private BigDecimal standardTime;
    /**
     * 部件工艺标准单价之和，单位元
     */
    private BigDecimal standardPrice;
    /**
     * 描述信息
     */
   // private String remark;

    /**
     * 主框架编码
     */
    private String mainFrameCode;

    /**
     * 主框架编码
     */
    private String mainFrameName;

    /**
     * 时间浮余
     */
    private BigDecimal timeSupplement;

    /**
     * 面料等级编码
     */
    private String fabricGradeCode;

    /**
     * 工序等级
     */
    private String craftGradeCode;

    /**
     * 是否新增  是否新增(1,新增，0，不是新增),默认是0
     */
    private Boolean isNew;

    /**
     * 是否有颜色  是否新增(1,新增，0，不是新增),默认是0
     */
    private Boolean hasColor;

    /**
     * 款的随机码
     */
    private Long styleRandomCode;

    /**
     * 款的码
     */
   // private String ctStyleCode;

    private transient List<SewingCraftAction> motionList;

    /**
     * 车缝工序所有信息，包括基础信息，缝边位置，建标，动作代码等
     */
    private transient String sewingCraftData;

    private String updateUser;

    private String craftCategoryCode;// 工艺品类编号
    private String craftCategoryName;// 工艺品类名称

    /**
     * 部件工艺编码
     */
    private String partCraftMainCode;
    /**
     * 部件工艺名称
     */
    private String partCraftMainName;

    //工序建标的线稿图
    private transient List<String> sketchPicUrl;
    //工序建标的视频
    private transient List<String> vedioUrl;

    /**
     * 站位
     */
    private String station;
    /**
     * 站位设备
     */
    private String stationDevice;

    /**
     * 部件详情的行号
     */
    private Integer orderNum;

    @TableField(fill = FieldFill.INSERT_UPDATE,value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timeUpdate;

}
