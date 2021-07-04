package com.ylzs.vo.thinkstyle;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CraftCategoryVo implements Serializable {
    private static final long serialVersionUID = 3165861827990866124L;
    /**
     * 工艺品类id
     */
    private Integer craftCategoryId;
    /**
     * 工艺品类代码
     */
    private String craftCategoryCode;
    /**
     * 工艺品类名称
     */
    private String craftCategoryName;
    /**
     * 服务品类列表
     */
    private List<ClothesCategoryVo> clothesCategoryVoList;

}
