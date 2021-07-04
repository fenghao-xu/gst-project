package com.ylzs.entity.materialCraft.req;


import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.MaterialCraftProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 新增材料工艺时请求实体
 * @Date 2020/3/5
 */
@Data
public class MaterialCraftReq implements Serializable {

    private static final long serialVersionUID = 3713961549364415500L;

    /**
     * 材料工艺主数据
     */
    private MaterialCraft materialCraft;

    /**
     * 材料工艺对应属性值
     */
    private List<MaterialCraftProperty> craftProperty;

    /**
     * 材料工艺规则和设计部件list
     */
    private MaterialCraftRuleAndPartListReq craftRuleAndPartListReq;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 材料工艺randomCode
     */
    private Long randomCode;

    /**
     * 多个材料属性值编码，用逗号分割
     */
    private String fabricPropertyDataCodes;

    /**
     * 多个材料属性编码，用逗号分割
     */
    private String fabricPropertyCodes;

    /**
     * 表示是否在另存为页面保存，true表示是，false表示否
     */
    private Boolean isEditPage;

}
