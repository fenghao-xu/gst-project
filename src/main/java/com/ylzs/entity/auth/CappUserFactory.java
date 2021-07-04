package com.ylzs.entity.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author weikang
 * @Description 用户工厂权限实体
 * @Date 2020/5/5
 */
@Data
public class CappUserFactory implements Serializable {
    private static final long serialVersionUID = 1155548659487700901L;

    private Integer id;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 工厂编码
     */
    private String factoryCode;

    /**
     * 工厂名称
     */
    private String factoryName;

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
