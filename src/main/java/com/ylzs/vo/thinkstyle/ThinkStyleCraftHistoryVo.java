package com.ylzs.vo.thinkstyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class ThinkStyleCraftHistoryVo implements Serializable {
    private static final long serialVersionUID = -6335521389345202402L;
    /**
     * 行号
     */
    private Integer lineNo;
    /**
     * 工序代码
     */
    private String craftCode;

    /**
     * 工序名称
     */
    private String craftName;

    /**
     * 工序描述
     */
    private String craftDesc;

    /**
     * 工序流
     */
    private Integer craftFlowNum;

    /**
     * 移除用户
     */
    private String removeUser;

    /**
     * 移除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date removeTime;

    /**
     * 原始工序流
     */
    private Integer oldCraftFlowNum;

    /**
     * 操作方式
     */
    private String operationType;

}
