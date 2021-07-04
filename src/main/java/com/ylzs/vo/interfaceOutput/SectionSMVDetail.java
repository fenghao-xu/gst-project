package com.ylzs.vo.interfaceOutput;

import lombok.Data;

import java.io.Serializable;

/**
 * 工段工时明细
 */
@Data
public class SectionSMVDetail implements Serializable {
    private static final long serialVersionUID = -5618064203719475907L;
    /**
     * 工段代码
     */
    private String gddm;
    /**
     * 工段名称
     */
    private String gdmc;
    /**
     * 标准工时
     */
    private String smv;
    /**
     * 时间单位
     */
    private String unit;
    /**
     * 标准单价
     */
    private String dj;
    /**
     * 单价单位
     */
    private String bb;
}
