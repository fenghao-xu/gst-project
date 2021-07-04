package com.ylzs.core.model;

import lombok.Data;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * @Classname SuperEntityVo
 * @Description VO实体类超类
 * @Date 2020/3/5 11:01
 * @Created by SKY
 */
@Data
public class SuperEntityVo implements Serializable {

    private static final long serialVersionUID = -5098479453953328363L;

    private Long id;

    private Long randomCode;

    private Integer status;

    /**
     * 是否失效 0生效 1失效
     */
    private Boolean isInvalid;

    /**
     * 描述信息
     */
    private String remark;

    private String version;
}
