package com.ylzs.vo.designpart;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylzs.core.model.SuperEntityVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @className DesignPartVo
 * @Description
 * @Author sky
 * @create 2020-03-04 11:03:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DesignPartVo extends SuperEntityVo {

    /**
     * 设计部件编码
     */
    private String designCode;
    /**
     * 设计部件名称
     */
    private String designName;
    /**
     * 设计部件图案
     */
    private String designImage;
    /**
     * 服装品类
     */
    private String clothingCategory;
    /**
     * 服装品类中文名称
     **/
    private String craftCategoryName;
    /**
     * 部件中类，关联部件中类表part_middle_code字段
     */
    private String partMiddleCode;
    /**
     * 部件中类中文名名称
     **/
    private String partMiddleName;
    /**
     * 部件位置
     */
    private String partPosition;
    /**
     * 图案类型
     */
    private String patternType;

    /**
     * 图案方式
     */
    private String patternMode;
    /**
     * 工艺说明
     */
    private String gongYiExplain;
    /**
     * 图案工艺编码
     */
    private String patternTechnologyD;
    /**
     * 图案工艺
     */
    private String patternTechnology;
    /**
     * 图案线色说明
     */
    private String patternMsg;
    /**
     * 是否影响工艺
     */
    private boolean affectCraft;


    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;

}
