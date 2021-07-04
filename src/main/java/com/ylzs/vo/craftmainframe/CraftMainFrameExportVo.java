package com.ylzs.vo.craftmainframe;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class CraftMainFrameExportVo implements Serializable {
    private static final long serialVersionUID = -823981427728466883L;

    /**
     * 行号
     */
    private Integer lineNo;

    /**
     * 工艺品类名称
     */
    private String craftCategoryName;

    /**
     * 主框架编码
     */
    private String mainFrameCode;

    /**
     * 主框架名称
     */
    private String mainFrameName;



    /**
     * 描述
     */
    private String description;

    /**
     * 是否是默认主框架（该品类）
     */
    private Boolean isDefault;


    /**
     * 状态名称
     */
    private String statusName;


    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
