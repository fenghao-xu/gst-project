package com.ylzs.entity.plm;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: watermelon.xzx
 * @Description: PI传入品类主数据
 * @Date: Created in 10:57 2020/3/10
 */
@Data
public class CategoryMasterData implements Serializable {

    private static final long serialVersionUID = -7924716266237026909L;

    private String categoryType;//品类分类

    private String categoryCode;//品类编码

    private String categoryName;//品类名称

    private String topCategoryType;//上级品类分类

    private String topCategoryCode;//上级品类编码

    private String styleType;//品类类型




}
