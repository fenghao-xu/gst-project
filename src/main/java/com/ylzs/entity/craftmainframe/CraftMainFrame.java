package com.ylzs.entity.craftmainframe;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;



@Data
public class CraftMainFrame extends SuperEntity {
    /**
    * 主框架编码
    */
    private String mainFrameCode;

    /**
    * 主框架名称
    */
    private String mainFrameName;

    /**
    * 工艺品类关联代码
    */
    private Long craftCategoryRandomCode;

    /**
    * 工艺品类代码
    */
    private String craftCategoryCode;

    /**
    * 工艺品类名称
    */
    private String craftCategoryName;

    /**
    * 描述
    */
    private String description;

    /**
     * 是否是默认主框架（该品类）
     */
    private Boolean isDefault;

    /**
     * 创建用户名称
     */
    private String createUserName;
    /**
     * 更新用户名称
     */
    private String updateUserName;
    /**
     * 审核用户名称
     */
    private String auditUserName;

    /**
     * 工艺主框架新建/编辑/详情页面，增加框架类型字段，必填项，枚举下拉，多选、框架类型：大货或定制，
     */
    private String frameType;
}