package com.ylzs.vo.partCraft;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ylzs.core.model.SuperEntityVo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @className PartCraftMasterBasicVo
 * @Description
 * @Author sky
 * @create 2020-03-04 20:11:41
 */
@Data
public class PartCraftMasterBasicVo extends SuperEntityVo {

    private static final long serialVersionUID = 4113344517408800641L;

    public PartCraftMasterBasicVo(){}

    /**
     * 部件工艺编码
     */
    //@NonNull
    private String partCraftMainCode;
    /**
     * 部件工艺名称
     */
    private String partCraftMainName;
    /**
     * 工艺品类code
     */
    private String craftCategoryCode;
    /**
     * 工艺品类名称
     */
    private String craftCategoryName;
    /**
     * 结构部件，关联工艺部件表craft_part ,craft_part_code字段
     */
    private String craftPartCode;
    /**
     * 结构部件，关联工艺部件名称
     */
    private String craftPartName;
    /**
     * 部件类型，数据字典中获取
     */
    @NonNull
    private String partType;
    /**
     * 部件工艺标准时间之和，单位分钟
     */
    @NonNull
    @JsonSerialize(using=ToStringSerializer.class)
    private BigDecimal standardTime;
    /**
     * 部件工艺标准单价之和，单位元
     */
    @NonNull
    @JsonSerialize(using=ToStringSerializer.class)
    private BigDecimal standardPrice;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 维护人
     */
    private String updateUser;
    /**
     * 维护时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 发布人
     */
    private String releaseUser;
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;
    /**
     * 部件工艺明细
     */
    private List<PartCraftDetailVo> partCraftDetails;
    /**部件工艺设计部件集合*/
    private List<PartCraftDesignPartsVo> craftDesignParts;
    /**部件设计位置VO**/
    private List<PartCraftPositionVo> craftPositions;
    /**部件工艺规则**/
    private List<PartCraftRuleVo> partCraftRules;
    private List<PartCraftMasterPictureVo> pictures;
    /***部件主数据图片RandomCode 关联part_craft_master_picture*/
    private List<Long> pictureRandomCodes;

}
