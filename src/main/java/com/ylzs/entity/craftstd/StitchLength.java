package com.ylzs.entity.craftstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 说明：针距
 *
 * @author lyq
 * 2019-09-27 14:04
 */
@Data
public class StitchLength {
    /**
     * 唯一id
     */
    private Integer id;
    /**
     * 针距代码
     */
    private String stitchLengthCode;
    /**
     * 针距名称
     */
    private String stitchLengthName;
    /**
     * 线号
     */
    private Integer lineId;
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
     * 维护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private transient String lineCode;
    private transient String lineName;
    private transient String linePositionCode;
    private transient String linePositionName;
    private transient String lineNameCode;
    private transient String lineNameName;
}
