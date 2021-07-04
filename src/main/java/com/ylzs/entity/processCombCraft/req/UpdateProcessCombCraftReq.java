package com.ylzs.entity.processCombCraft.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 更新工序组合工艺请求数据
 * @Date 2020/3/16
 */
@Data
public class UpdateProcessCombCraftReq implements Serializable {

    private static final long serialVersionUID = -2607584822354248708L;

    /**
     * 操作类型1.删除 2.失效
     */
    private Integer operateType;

    /**
     * 工序组合工艺的randomcode
     */
    private Long randomCode;

    /**
     * 用户编码
     */
    private String userCode;
}
