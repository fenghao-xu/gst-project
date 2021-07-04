package com.ylzs.entity.craftstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ylzs.entity.craftmainframe.CraftMainFrame;
import com.ylzs.vo.thinkstyle.ClothesCategoryVo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

//import com.sun.org.apache.xpath.internal.operations.Bool;


/**
 * 说明：工艺品类
 *
 * @author lyq
 * 2019-09-23 14:45
 */
@Data
public class CraftCategory {
    /**
     * 自增id
     */
    private Integer id;

    /**
     * 行标识
     */
    private Long randomCode;

    /**
     * 工艺品类代码
     */
    private String craftCategoryCode;
    /**
     * 工艺品类名称
     */
    private String craftCategoryName;

    /**
     * 服装大类
     */
    private String clothesBigCategoryCode;

    /**
     * 序号
     */
    private Integer seqNum;

    /**
     * 品类描述
     */
    private String description;
    /**
     * 备注
     */
    private String remark;
    /**
     * 有效标志
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 服装品类（PLM传过来的的品类代码）
     */
    private  String clothesCategoryIds;

    /**
     * 部件列表
     */
    private  List<CraftPart> craftPartList;

    /**
     * 服装品类列表
     */
    private  String clothesCategoryNames;

    /**
     * code name 组合
     */
    private  String categoryCodeName;

    /**
     *工艺品类 下面的所有服装品类
     */
    private  List<ClothesCategoryVo> clothesCategoryList;
     /**
     *工艺品类 下的工艺主框架
     */
    private  List<CraftMainFrame> craftMainFrameList;

}
