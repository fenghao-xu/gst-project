package com.ylzs.entity.processCombCraft.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 清单页面查询请求数据
 * @Date 2020/3/16
 */
@Data
public class QueryProcessCombCraftReq implements Serializable {

    private static final long serialVersionUID = 7671738131348997922L;

    /**
     * 工序组合工艺名称/工序编码/工序名称
     */
    private String searchText;

    /**
     * 工序编码
     */
    private String craftCode;
    /**
     * 工序描述
     */
    private String craftDescript;
    /**
     * 工艺组合工艺代码
     */
    private String craftCombCraftCode;
    /**
     * 工序组合工艺名称
     */
    private String craftCombCraftName;

    /**
     * 创建人
     */
    private String userCode;

    /**
     * 服装品类编码
     */
    private String clothingCategoryCode;

    /**
     * 原工序编码/工序名称
     */
    private String processCraft;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1090 删除
     */
    private Integer status;

    private Integer page;

    private Integer rows;

}
