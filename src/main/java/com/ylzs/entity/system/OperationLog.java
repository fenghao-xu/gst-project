package com.ylzs.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 说明：操作日志
 *
 * @author Administrator
 * 2019-09-24 11:12
 */

@Data
public class OperationLog {
    /**
     * 唯一标识id
     */
    private Long id;
    /**
     * 用户代码
     */
    private String userCode;
    /**
     * 模块代码
     */
    private String moduleCode;
    /**
     * 操作代码 CRUD
     */
    private String operCode;
    /**
     * 操作说明
     */
    private String operDesc;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 全端传过来的操作入参
     */
    private String param;
}
