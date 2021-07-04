package com.ylzs.entity.materialCraft.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author weikang
 * @Description 材料工艺历史版本数据
 * @Date 2020/3/5
 */
@Data
public class MaterialCraftHistoryVersionResp implements Serializable {

    private static final long serialVersionUID = -1176197359248594188L;

    /**
     * 材料工艺randomCode
     */
    private Long randomCode;

    /**
     * 材料工艺编码
     */
    private String materialCraftCode;

    /**
     * 材料工艺名称
     */
    private String materialCraftName;

    /**
     * 服装品类编码
     */
    private String clothingCategoryCode;

    /**
     * 服装品类名称
     */
    private String clothingCategoryName;

    /**
     * 材料工艺版本号
     */
    private String materialVersion;

    /**
     * 版本说明
     */
    private String materialVersionDesc;

    /**
     * 生效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date effectiveTime;

    /**
     * 失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date invalidTime;

    /**
     * 发布人员
     */
    private String releaseUser;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date releaseTime;

}
