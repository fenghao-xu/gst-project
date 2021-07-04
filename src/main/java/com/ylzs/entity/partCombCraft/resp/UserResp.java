package com.ylzs.entity.partCombCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description
 * @Date 2020/3/17
 */
@Data
public class UserResp implements Serializable {

    private static final long serialVersionUID = -7777066031713513341L;

    /**
     * 用户代码
     */
    private String userCode;
    /**
     * 用户名称
     */
    private String userName;
}
