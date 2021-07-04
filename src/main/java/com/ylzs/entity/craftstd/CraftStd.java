package com.ylzs.entity.craftstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 说明：标准工艺
 *
 * @author Administrator
 * 2019-09-24 10:45
 */
@Data
public class CraftStd {
    /**
     * id
     */
    private Long id;
    /**
     * 工艺标准代码
     */
    private String craftStdCode;
    /**
     * 工艺标准名称
     */
    private String craftStdName;
    /**
     * 来源款号
     */
    private String sourceCode;
    /**
     * 建议款号
     */
    private String suggestCode;
    /**
     * 工段id
     */
    private Integer sectionId;
    /**
     * 工种id
     */
    private Integer workTypeId;
    /**
     * 做工类型id
     */
    private Integer makeTypeId;
    /**
     * 品质要求
     */
    private String requireQuality;
    /**
     * 线号id
     */
    private Integer lineId;
    /**
     * 烫斗温度
     */
    private BigDecimal ironTemperature;
    /**
     * 做工说明
     */
    private String makeDesc;
    /**
     * 针距id
     */
    private Integer stitchLengthId;
    /**
     * 是否有效果图
     */
    private Boolean isEffectPic;
    /**
     * 是否有线稿图
     */
    private Boolean isHandPic;
    /**
     * 机器Id
     */
    private Integer machineId;
    /**
     * 是否有视频
     */
    private Boolean isVideo;
    /**
     * 状态
     */
    private String status;
    /**
     * 失效标志
     */
    @JsonIgnore
    private Boolean isInvalid;
    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 审核人
     */
    private String okUser;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date okTime;
    /**
     * 维护人
     */
    private String updateUser;

    /**
     * 维护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 提取人
     */
    private String extractUser;

    /**
     * 提取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date extractTime;

    /**
     * 备注
     */
    private String remark;




    /**
     * 提交用户
     */
    private String commitUser;

    /**
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date commitTime;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 是否部件共享
     */
    private Boolean isPartShare;
    /**
     * 是否品类共享
     */
    private Boolean isCategoryShare;

    /**
     * 是否是关键工序
     */
    private Boolean isKeyCraft;

    /**
     * 是不是常规工序
     */
    private Boolean isNormalCraft;



    private transient String craftCategoryNames;
    private transient String craftCategoryCodes;
    private transient String craftPartNames;

    private transient String sectionCode;
    private transient String sectionName;
    private transient String workTypeCode;
    private transient String workTypeName;
    private transient String makeTypeCode;
    private transient String makeTypeName;
    private transient String lineCode;
    private transient String lineName;
    private transient String machineCode;
    private transient String machineNameCn;

    private transient List<String> vedioUrlList;
    private transient List<String> pictureUrlList;

    /**
     * 工序部件id列表
     */
    private transient String craftPartIds;


    /**
     * 工艺品类id列表
     */
    private transient String craftCategoryIds;


    /**
     * 视频上传地址列表
     */
    private transient String videoUrls;
    /**
     * 线稿图上传地址列表
     */
    private transient String handPicUrls;

    /**
     * 效果图上传地址列表
     */
    private transient String effectPicUrls;

    /**
     * 辅助工具---名称
     */
    private transient String helpToolCode;
    /**
     * 辅助工具编码
     */
    private transient String helpTool;

    /**
     * 线稿图缩略图列表
     */
    private transient String handPicThumbUrls;

    /**
     * 效果图缩略图列表
     */
    private transient String effectPicThumbUrls;




}



