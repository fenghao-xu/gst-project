package com.ylzs.entity.materialCraft;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author weikang
 * @Description 材料工艺主数据实体
 * @Date 2020/3/5
 */
@Data
public class MaterialCraft extends SuperEntity {

    private static final long serialVersionUID = -5422861875926549769L;

    /**
     * 材料工艺编码
     */
    private String materialCraftCode;

    /**
     * 材料工艺名称
     */
    private String materialCraftName;

    /**
     * 材料类型编码
     */
    private String materialCraftKindCode;

    /**
     * 材料类型名称
     */
    private String materialCraftKindName;

    /**
     * 材料工艺描述
     */
    private String materialCraftDesc;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1040.未生效 1090 删除
     */
    private Integer status;

    /**
     * 材料工艺版本号
     */
    private String materialVersion;

    /**
     * 版本说明
     */
    private String materialVersionDesc;

    /**
     * 生效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date effectiveTime;

    /**
     * 失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invalidTime;

    /**
     * 发布人员
     */
    private String releaseUser;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 材料工艺方案编号，多个用逗号分割
     */
    private String planNumber;

    /**
     * 服装品类编码
     */
    private String clothingCategoryCode;

    /**
     * 服装品类名称
     */
    private String clothingCategoryName;
}
