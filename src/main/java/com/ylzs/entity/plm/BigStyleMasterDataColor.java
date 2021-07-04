package com.ylzs.entity.plm;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Author:tt
* @Description： 大货主数据-颜色 SKC子表
* @Date: Created in 2020/3/14
*/
@Data
public class BigStyleMasterDataColor implements Serializable {

    private static final long serialVersionUID = 2219453020953372185L;
    private int id;
    private String  styleCode   ;  //款的编码，不含颜色
    private String  styleSkcCode   ;  //款色编码，包含颜色编码
    private String  colorCode   ;  //颜色编码
    private String  colorName  ;  // 颜色名称
    private String  skcImageUrl   ;  // 图片地址
    private BigDecimal brightness;//明度

}
