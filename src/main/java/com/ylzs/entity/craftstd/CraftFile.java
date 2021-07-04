package com.ylzs.entity.craftstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 说明：工艺文件
 *
 * @author lyq
 * 2019-09-29 15:57
 */
@Data
public class CraftFile {
    /**
     * 工艺文件唯一标识
     */
    private Long id;
    /**
     * 资源类型
     */
    private Integer resourceType;
    /**
     * 对应表的id
     */
    private Long keyId;
    /**
     * 对应表的代码
     */
    private String keyStr;
    /**
     * 文件url
     */
    private String fileUrl;
    /**
     * 失效标志
     */
    private Boolean isInvalid;
    /**
     * 备注
     */
    private String remark;
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

}
