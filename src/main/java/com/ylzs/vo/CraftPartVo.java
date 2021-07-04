package com.ylzs.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @className CraftPartVo
 * @Description
 * @Author sky
 * @create 2020-03-02 16:10:24
 */
@Data
public class CraftPartVo implements Serializable {

    private static final long serialVersionUID = -9112263289071406939L;

    private Integer id;

    private String craftPartCode;

    private String craftPartName;
}
