package com.ylzs.entity.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author weikang
 * @Description 用户服装品类权限实体
 * @Date 2020/5/5
 */
@Data
public class CappUserClothingCategory implements Serializable {
    private static final long serialVersionUID = 1753689671499916552L;

    private Integer id;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 服装品类编码（大类）
     */
    private String clothingCategoryCode;

    /**
     * 服装品类名称
     */
    private String clothingCategoryName;

    /**
     * 编辑权限 0未拥有 1拥有
     */
    private Boolean isEditAuthority;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    private String  updateUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
