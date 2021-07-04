package com.ylzs.entity.craftstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 说明：做工类型
 *
 * @author lyq
 * 2019-09-24 10:20
 */
@Data
public class MakeType {
    /**
     * 自增id
     */
    private Integer id;

    /**
     * 做工类型代码
     */
    private String makeTypeCode;
    /**
     * 做工类型名称
     */
    private String makeTypeName;
    /**
     * 工种类型id
     */
    private Integer workTypeId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 失效标志
     */
    @JsonIgnore
    private Boolean isInvalid;
    /**
     * 维护人
     */
    private String updateUser;

    /**
     *做工类型代码--序列号
     */
    private String makeTypeNumericalCode;


    /**
     * 维护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 中文首字母
     */
    private String pyHeadChar;


    /**
     * 工段代码
     */
    private String section;

    /**
     * 工种代码
     */
    private transient String workTypeCode;
    /**
     * 工种名称
     */
    private transient String workTypeName;
}
