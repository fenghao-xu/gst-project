package com.ylzs.vo.thinkstyle;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：lyq
 * @description：智库款设计部件
 * @date ：2020-03-07 13:37
 */
@Data
public class ThinkStylePartVo implements Serializable {
    private static final long serialVersionUID = 1839894082193999193L;
    /**
     * 智库款部件id
     */
    private Long id;
    /**
     * 智库款工艺部件关联代码
     */
    private Long randomCode;
    /**
     * 行号
     */
    private Integer lineNo;
    /**
     * 是否特殊
     */
    private Boolean isSpecial;
    /**
     * 设计部件代码
     */
    private String designPartCode;
    /**
     * 父设计部件代码
     */
    private String parentPartCode;

    /**
     * 顶层设计部件代码
     */
    private String topPartCode;
    /**
     * 设计部件代码名称，中间用-分隔
     */
    private String designPart;
    /**
     * 位置代码和名称
     */
    String position;
    /**
     * 是否生效
     */
    private Boolean isValid;
    /**
     * 是否默认部件
     */
    private Boolean isDefault;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 状态名称
     */
    private String statusName;
    /**
     * 处理次序号
     */
    private Integer orderNum;

    /**
     * 部件工序编号
     */
    private String partCraftMainCode;
    /**
     * 子集
     */
    private transient List<ThinkStylePartVo> children;

    private transient List<ThinkStyleCraftVo> crafts;
    private transient List<ThinkStyleProcessRuleVo> rules;


    /**
     * 设计部件名称
     */
    private String designPartName;
    /**
     * 位置代码
     */
    private String positionCode;
    /**
     * 位置名称
     */
    private String positionName;

    private String patternType;
    private String patternMode;

}
