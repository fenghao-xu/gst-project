package com.ylzs.vo.designpart;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 设计部件来源
 */
@Data
public class DesignPartSourceVo implements Serializable {
    private static final long serialVersionUID = -3157298131266272123L;

    /**
     * 行号
     */
    private Integer lineNo;
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 定制款号
     */
    private String customStyleNo;
    /**
     * 来源
     */
    private String source;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
