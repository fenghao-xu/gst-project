package com.ylzs.vo;

import com.ylzs.vo.clothes.ClothesVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className CraftCateGoryVo
 * @Description 服装品类VO
 * @Author sky
 * @create 2020-03-02 16:10:05
 */
@Data
public class CraftCateGoryVo implements Serializable {

    private static final long serialVersionUID = -3160800259407060471L;

    private Integer id;
    private String craftCategoryCode;
    private String craftCategoryName;
    private List<CraftPartVo> craftPartVos;
    private List<ClothesVo> clothesVos;
}
