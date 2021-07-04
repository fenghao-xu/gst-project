package com.ylzs.entity.partCombCraft.resp;

import com.ylzs.entity.partCombCraft.PartCombCraft;
import com.ylzs.entity.partCombCraft.PartCombCraftProgramDetail;
import com.ylzs.entity.partCombCraft.PartCombCraftRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 部件组合工艺所有数据
 * @Date 2020/3/18
 */
@Data
public class PartCombCraftAllDataResp implements Serializable {

    private static final long serialVersionUID = -2397904370179954553L;

    private Long id;

    private Long randomCode;
    /**
     * 部件组合工艺编码
     */
    private String partCombCraftCode;

    /**
     * 部件组合工艺名称
     */
    private String partCombCraftName;

    /**
     * 服装品类编码
     */
    private String clothingCategoryCode;

    /**
     * 服装品类名称
     */
    private String clothingCategoryName;

    /**
     * 描述
     */
    private String partCombCraftDesc;

    /**
     * 部件组合工艺方案,多个用逗号分割
     */
    private String partCombCraftNumber;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1090 删除
     */
    private Integer status;

    /**
     * 方案
     */
    private List<PartCombCraftProgramDetail> details;

    /**
     * 规则
     */
    private List<PartCombCraftRule> rules;
}
