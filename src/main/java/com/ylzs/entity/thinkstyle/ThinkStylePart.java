package com.ylzs.entity.thinkstyle;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 智库款工艺部件
 * @author: lyq
 * @date: 2020-03-05 11:50
 */
@Data
public class ThinkStylePart extends SuperEntity {
    /**
     * 序号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer orderNum;

    /**
     * 是否锁定
     */
    private Byte isLock;

    /**
     * 智库款关联代码
     */
    private Long styleRandomCode;

    /**
     * 部件关联代码
     */
    private Long partRandomCode;

    /**
     * 设计部件编码
     */
    private String designPartCode;

    /**
     * 设计部件名称
     */
    private String designPartName;
    /**
     * 父部件代码
     */
    private String parentPartCode;

    /**
     * 顶层部件代码
     */
    private String topPartCode;


    /**
     * 是否默认
     */
    private Boolean isDefault;

    /**
     * 是否特殊
     */
    private Boolean isSpecial;

    /**
     * 是否生效
     */
    private Boolean isValid;

    /**
     * 是不是虚拟部件
     */
    private Boolean isVirtual;

    /**
     * 部件位置代码
     */
    private String positionCode;

    /**
     * 部件位置名称
     */
    private String positionName;

    /**
     * 部件位置类型
     */
    private String positionType;

    /*
     * 标准单价
     */
    private BigDecimal standardPrice;


    /**
     * 标准时间
     */
    private BigDecimal standardTime;


    /**
     * 智库款工序列表
     */
    private transient List<ThinkStyleCraft> craftList;

    /**
     * 智库款工序处理规则
     */
    private transient List<ThinkStyleProcessRule> ruleList;



}

