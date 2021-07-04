package com.ylzs.vo.thinkstyle;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClothesCategoryVo implements Serializable {
    private static final long serialVersionUID = 9166943698066384718L;
    /**
     * 字典id
     */
    private Integer clothesCategoryId;
    /**
     * 服装品类代码
     */
    private String clothesCategoryCode;
    /**
     * 服装品类名称
     */
    private String clothesCategoryName;
}
