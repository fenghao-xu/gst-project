package com.ylzs.entity.materialCraft;

import com.ylzs.core.model.SuperEntity;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @author weikang
 * @Description 材料工艺规则实体
 * @Date 2020/3/5
 */
@Data
public class MaterialCraftRule extends SuperEntity {
    private static final long serialVersionUID = 1471936215028264907L;

    /**
     * 材料工艺主数据material_craft的random_code
     */
    private Long materialCraftRandomCode;

    /**
     * 原工序编码,工序词库sewing_craft_warehouse的craft_code,多个用#分割
     */
    private String sourceCraftCode;

    /**
     * 原工序名称,工序词库sewing_craft_warehouse的craft_name,多个用#分割
     */
    private String sourceCraftName;

    /**
     * 执行工序代码,工序词库sewing_craft_warehouse的craft_code,多个用#分割
     */
    private String actionCraftCode;

    /**
     *执行工序名称,工序词库sewing_craft_warehouse的craft_name,多个用#分割
     */
    private String actionCraftName;

    /**
     * 处理方式 -1减少 0替换 1增加
     */
    private Integer processType;

    /**
     * 材料规则类型 1.默认 2.特殊
     */
    private Integer ruleType;

    /**
     * 位置下标
     */
    private Integer ruleIndex;

    /**
     * 方案编号,默认规则为0
     */
    private Integer specialPlanNumber;

    /**
     * 描述信息
     */
    private String remark;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1040.未生效 1090 删除
     */
    private Integer status;

    private Integer processingSortNum;

}
