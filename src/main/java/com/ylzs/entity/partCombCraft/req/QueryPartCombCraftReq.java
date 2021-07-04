package com.ylzs.entity.partCombCraft.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 清单页面查询请求数据
 * @Date 2020/3/13
 */
@Data
public class QueryPartCombCraftReq implements Serializable {

    private static final long serialVersionUID = 3188094287515322640L;
    /**
     * 部件组合工艺名称/设计部件编码/设计部件名称
     */
    private String searchText;

    /**
     * 设计部件代码
     */
    private String designPartCode;
    /**
     * 设计部件名称
     */
    private String designPartName;
    /**
     * 部件组合工艺代码
     */
    private String partCombCraftCode;
    /**
     * 部件组合工艺名称
     */
    private String partCombCraftName;

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
