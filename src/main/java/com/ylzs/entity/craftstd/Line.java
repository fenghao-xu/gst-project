package com.ylzs.entity.craftstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 说明：线号
 *
 * @author lyq
 * 2019-09-24 10:35
 */
@Data
public class Line {
    /**
     * 唯一id
     */
    private Integer id;
    /**
     * 线号
     */
    private String lineCode;
    /**
     * 线号名称
     */
    private String lineName;
    /**
     * 转换率
     */
    private Float convertRate;
    /**
     * 用线部位
     */
    private Integer linePositionId;

    /**
     * 线名id
     */
    private Integer lineNameId;
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
     * 维护用户
     */
    private String updateUser;

    /**
     * 维护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    private transient String linePositionCode;
    private transient String linePositionName;
    private transient String lineNameCode;
    private transient String lineNameName;


    /**
     * 用线部位对象
     */
    private transient Dictionary linePosition;
    /**
     * 线名对象
     */
    private transient Dictionary lineNameObj;
}
