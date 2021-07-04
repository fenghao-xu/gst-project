package com.ylzs.vo.partCraft;

import com.ylzs.core.model.SuperEntityVo;
import lombok.Data;

/**
 * @className PartCraftPositionVo
 * @Description
 * @Author sky
 * @create 2020-03-04 20:47:04
 */
@Data
public class PartCraftPositionVo extends SuperEntityVo {


    private static final long serialVersionUID = 8225036006620039094L;
    /**
     * 部件位置编码,关联part_position部件位置表
     */
    private String partPositionCode;
    /**
     * 部件位置名称,根据部件位置编码自动带出
     */
    private String partPositionName;
    /**
     * 服装品类
     */
    //private String clothingCategory;
    /**
     * 部件工艺主数据编码
     */
    private Long partCraftMainRandomCode;


    /**
     * 服装品类代码
     */
    private String clothingCategoryCode;

    /**
     * 服装品类名称
     */
    private String clothingCategoryName;

}
