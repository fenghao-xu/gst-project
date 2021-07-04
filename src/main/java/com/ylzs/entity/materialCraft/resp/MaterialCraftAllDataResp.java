package com.ylzs.entity.materialCraft.resp;

import com.alibaba.fastjson.annotation.JSONField;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftProperty;
import com.ylzs.entity.materialCraft.MaterialCraftRule;
import com.ylzs.entity.materialCraft.MaterialCraftRulePart;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺所有关联表数据
 * @Date 2020/3/18
 */
@Data
public class MaterialCraftAllDataResp implements Serializable {
    private static final long serialVersionUID = 930389119772067729L;


    private Long id;
    /**
     * 随机码
     */
    private Long randomCode;

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
     * 材料工艺方案编号，多个用逗号分割
     */
    private String planNumber;

    /**
     * 属性
     */
    private List<MaterialCraftProperty> properties;

    /**
     * 规则
     */
    private List<MaterialCraftRule> rules;

    /**
     * 部件
     */
    private List<MaterialCraftRulePart> parts;
}
