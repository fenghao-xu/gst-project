package com.ylzs.entity.materialCraft.resp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author weikang
 * @Description 材料工艺和材料属性返回数据
 * @Date 2020/3/7
 */
@Data
public class MaterialCraftAndPropertyResp implements Serializable {
    private static final long serialVersionUID = -2949508654941826487L;

    /**
     * 材料工艺id
     */
    private Long materialCraftId;

    /**
     * 材料属性id
     */
    private Long materialCraftPropertyId;

    /**
     * 材料属性值编码
     */
    private String fabricPropertyDataCode;

    /**
     * 材料属性编码
     */
    private String fabricPropertyCode;

    /**
     * 材料属性名称
     */
    private String fabricPropertyName;

    /**
     * 材料属性值
     */
    private String fabricPropertyData;

    /**
     * 材料工艺主数据material_craft的random_code
     */
    private Long materialCraftRandomCode;

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
     * 服装品类编码
     */
    private String clothingCategoryCode;

    /**
     * 服装品类名称
     */
    private String clothingCategoryName;

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
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date effectiveTime;

    /**
     * 失效时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date invalidTime;

    /**
     * 发布人员
     */
    private String releaseUser;

    /**
     * 发布时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;

    /**
     * 创建人
     */
    private String createUser;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
