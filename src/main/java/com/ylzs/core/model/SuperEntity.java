package com.ylzs.core.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Date;

/**
 * @className BaseBO
 * @Description 实体类父级基类，所有实体类都必须要继承此类
 * @Author sky
 * @create 2020-02-20 21:46:44
 */
@Data
@ApiModel(description = "bean父级超类")
public class SuperEntity implements Serializable {

    private static final long serialVersionUID = 4041166912551245238L;
    /**
     * 主键自增
     */
    @Id
    @Field(type = FieldType.Long)
    @TableId
    private Long id;
    /**
     * 随机码
     */
    @TableField("random_code")
    private Long randomCode;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1040.未生效 1090 删除
     */
    private Integer status;
    /**
     * 是否失效 0生效 1失效
     */
    @TableField("is_invalid")
    private Boolean isInvalid;
    /**
     * 审核人
     */
    @TableField("audit_user")
    private String auditUser;
    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("audit_time")
    private Date auditTime;
    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT,value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 维护人
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 维护时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE,value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 版本
     */
    @TableField(fill = FieldFill.INSERT)
    private String version;
}
