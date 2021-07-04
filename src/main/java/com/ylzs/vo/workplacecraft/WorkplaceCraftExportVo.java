package com.ylzs.vo.workplacecraft;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class WorkplaceCraftExportVo implements Serializable {
    private static final long serialVersionUID = -3476726071114598003L;
    /**
     * 行号
     */
    private Integer lineNo;
    /**
     * 工艺品类名称
     */
    private String craftCategoryName;
    /**
     * 工艺主框架名称
     */
    private String mainFrameName;
    /**
     * 生产部件名称
     */
    private String productionPartName;
    /**
     * 工序流（数字，隔100）
     */
    private Integer craftFlowNum;


    /**
     * 工位工序代码
     */
    private String workplaceCraftCode;

    /**
     * 工位工序名称
     */
    private String workplaceCraftName;


    /**
     * 备注
     */
    private String remark;


    /**
     * 状态名称
     */
    private String statusName;


    /**
     * 维护人
     */
    private String updateUser;

    /**
     * 维护时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
