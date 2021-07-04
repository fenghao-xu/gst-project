package com.ylzs.entity.materialCraft.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 查询材料工艺数据请求类
 * @Date 2020/3/9
 */
@Data
public class QueryMaterialCraftReq implements Serializable {

    private static final long serialVersionUID = 4495832385830542286L;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1040.未生效 1090 删除
     */
    private Integer status;

    /**
     * 多个材料类型编码,用逗号分割
     */
    private String materialCraftKindCodes;

    /**
     * 多个材料属性编码,用逗号分割
     */
    private String fabricPropertyCodes;

    /**
     * 材料工艺编码
     */
    private String materialCraftCode;

    /**
     * 材料工艺名称
     */
    private String materialCraftName;

    /**
     * 编码和名称
     */
    private String materialCraftCodeAndName;

    private Integer page;

    private Integer rows;
}
