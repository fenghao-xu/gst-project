package com.ylzs.entity.partCombCraft.req;

import com.ylzs.entity.partCombCraft.PartCombCraftProgramDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 部件组合工艺方案请求数据
 * @Date 2020/3/12
 */
@Data
public class PartCombCraftProgramReq implements Serializable {

    private static final long serialVersionUID = 2921138411364783008L;

    /**
     * 组合工艺部件和位置详情数据
     */
    private List<PartCombCraftProgramDetail> programDetailList;

    /**
     * 部件编码和位置编码用#拼接,多个用逗号分隔
     */
    private String designCodeAndPositionCode;

    /**
     * 方案编码
     */
    private Integer partNumber;
}
