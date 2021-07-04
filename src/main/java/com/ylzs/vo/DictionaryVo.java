package com.ylzs.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @className DictionaryVo
 * @Description
 * @Author sky
 * @create 2020-03-02 16:14:13
 */
@Data
public class DictionaryVo implements Serializable {

    private static final long serialVersionUID = 3100764720967083788L;
    /**
     * 序号
     */
    private Integer id;

    /**
     * 字典值
     */
    private String dicValue;

    /**
     * 值描述
     */
    private String valueDesc;

    private String remark;
}
