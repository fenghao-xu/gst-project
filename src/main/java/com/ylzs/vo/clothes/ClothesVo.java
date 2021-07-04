package com.ylzs.vo.clothes;

import lombok.Data;

import java.io.Serializable;

/**
 * @className ClothesVo
 * @Description 服装品类VO 根据工艺品类关联
 * @Author sky
 * @create 2020-04-08 16:57:55
 */
@Data
public class ClothesVo implements Serializable {

    private Integer craftCategoryId;
    private String clothesCategoryCode;
    private String clothesCategoryName;
}
