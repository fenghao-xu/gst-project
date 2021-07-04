package com.ylzs.entity.processCombCraft.resp;

import com.ylzs.entity.processCombCraft.ProcessCombCraft;
import com.ylzs.entity.processCombCraft.ProcessCombCraftProgram;
import com.ylzs.entity.processCombCraft.ProcessCombCraftRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 工序组合工艺返回所有数据
 * @Date 2020/3/18
 */
@Data
public class ProcessCombCraftAllDataResp implements Serializable {

    private static final long serialVersionUID = 5918164192658126287L;

    private Long id;
    /**
     * 工序组合工艺编码
     */
    private String processCombCraftCode;

    /**
     * 工序组合工艺名称
     */
    private String processCombCraftName;

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
    private String processCombCraftDesc;

    /**
     * 工序组合工艺方案编号，多个用逗号分割
     */
    private String processNumbers;

    /**
     * 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1090 删除
     */
    private Integer status;

    private Long randomCode;

    /**
     * 规则
     */
    private List<ProcessCombCraftRule> rules;

    /**
     * 方案
     */
    private List<ProcessCombCraftProgram> programs;
}
