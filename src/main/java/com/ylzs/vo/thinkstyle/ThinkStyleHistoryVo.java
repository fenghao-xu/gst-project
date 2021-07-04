package com.ylzs.vo.thinkstyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 历史版本记录
 */
@Data
public class ThinkStyleHistoryVo implements Serializable {
    private static final long serialVersionUID = -1255727407145237593L;
    /**
     * 行号
     */
    private Integer line_num;


    /**
     * 智库款工艺编码
     */
    private String thinkStyleCode;
    /**
     * 款式编号
     */
    private String styleCode;

    /**
     * 款式名称
     */
    private String styleName;
    /**
     * 工序版本
     */
    private String craftVersion;
    /**
     * 版本说明
     */
    private String versionDesc;
    /**
     * 生效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date inspireTime;
    /**
     * 失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expirationTime;

    /**
     * 发布用户
     */
    private String publishUser;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;
}
