package com.ylzs.vo.partCraft;

import com.ylzs.core.model.SuperEntityVo;
import lombok.Data;

/**
 * @className PartCraftDesignPartsVo
 * @Description
 * @Author sky
 * @create 2020-03-04 20:34:47
 */
@Data
public class PartCraftDesignPartsVo extends SuperEntityVo {


    private static final long serialVersionUID = 4602139217515952066L;
    /**
     * 设计部件编码,关联设计部件craft_part_code字段
     */
    private String designCode;
    /**
     * 设计部件名称,根据设计部件编码自动带出
     */
    private String designName;
    /**
     * 图案方式
     */
    private String patternMode;

    private String patternType;//图案类型 PI对应patternTechnology

    private String patternTechnology;//图案工艺
    /**
     * 部件工艺主数据编码
     */
    private Long partCraftMainRandomCode;

}