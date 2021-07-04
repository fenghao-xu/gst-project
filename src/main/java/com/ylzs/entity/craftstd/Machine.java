package com.ylzs.entity.craftstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;



/**
 * 说明：机器
 *
 * @author lyq
 * 2019-09-24 10:26
 */
@Data
public class Machine {
    /**
     * 唯一id
     */
    private Integer id;
    /**
     * 机器代码
     */
    private String machineCode;
    /**
     * 机器中文名
     */
    private String machineNameCn;
    /**
     * 机器英文名
     */
    private String machineNameEn;
    /**
     * 工种类型id
     */
    private Integer workTypeId;

    /**
     * 设备id
     */
    private Integer deviceId;

    /**
     * 机器浮余
     */
    private Float machineFloatover;
    /**
     * 人工浮余
     */
    private Float manualFloatover;
    /**
     * 针距
     */
    private Float stitchLength;
    /**
     * 转速
     */
    private Float rpm;
    /**
     * 自动断线
     */
    private Boolean isAutoCutLine;
    /**
     * 是否存在图片
     */
    private Boolean isPic;
    /**
     * 失败标志
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

    /**
     * 中文首字母
     */
    private String pyHeadChar;


    /**
     * 备注
     */
    private String remark;


    /**
     * 图片存放路径
     */
    private transient String picUrl;

    private String workTypeCode;
    private transient String workTypeName;
    private transient String deviceCode;
    private transient String deviceName;



}
