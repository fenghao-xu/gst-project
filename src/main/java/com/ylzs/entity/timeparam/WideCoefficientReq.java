package com.ylzs.entity.timeparam;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 19:34 2020/5/13
 */
@Data
public class WideCoefficientReq {
    private String wideCode;//宽放编码
    private String wideName;//宽泛名称
    private BigDecimal coefficient;//系数（根据工艺品类进行维护，保留2位小数）
    private String craftCategoryCode;//工艺品类编码（来源基础数据工艺品类，关联工艺品类crafts_category)
    private String[] craftCategoryCodes;//工艺品类编码集合
    private String remark;
    private String status;
    private String updateUser;
    private Date updateTime;
}
