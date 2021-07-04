package com.ylzs.entity.craftstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 说明：最大流水号服务
 *
 * @author lyq
 * 2019-09-24 10:20
 */

@Data
public class MaxCode {
    /**
     * 自增序号
     */
    private Integer id;
    /**
     * 模块代码
     */
    private String moduleCode;
    /**
     * 前缀
     */
    private String preStr;
    /**
     * 最大流水号
     */
    private Integer maxId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date CreateTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date UpdateTime;

}
