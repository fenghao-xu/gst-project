package com.ylzs.entity.partCombCraft.req;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 更新部件组合工艺请求数据
 * @Date 2020/3/13
 */
@Data
public class UpdatePartCombCraftReq implements Serializable {

    private static final long serialVersionUID = -3310895042446406383L;
    /**
     * 操作类型1.删除 2.失效
     */
    private Integer operateType;

    /**
     * 部件组合工艺的randomCode
     */
    private Long randomCode;

    /**
     * 用户编码
     */
    private String userCode;
}
