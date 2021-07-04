package com.ylzs.entity.timeparam;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.core.model.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author xuwei
 * @create 2020-06-22 14:34
 */
@Data
public class SameLevelCraft extends SuperEntity {
    private Long id;
    /**
     *服装大类代码
     */
    private String clothesBigCatogoryCode;

    /**
     * 工艺品类代码
     */
    private String craftCategoryCode;

    /**
     * 工艺品类名称
     */
    private String craftCategoryName;
    /**
     *做工类型编码
     */
    private String makeTypeCode;
    /**
     *做工类型代码--序列号
     */
    private String makeTypeNumericalCode;
    /**
     *做工类型名称
     */
    private String makeTypeName;
    /**
     *同级工序名称
     */
    private String sameLevelCraftName;
    /**
     *同级工序流水号
     */
    private String sameLevelCraftSerial;
    /**
     *同级工序代码
     */
    private String sameLevelCraftNumericalCode;
    /**
     * 难度等级
     */
    private String hardLevel;

    private String remark;

    private String releaseUser;

    private String createUserName;

    private String updateUserName;

    private String releaseUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;

}
