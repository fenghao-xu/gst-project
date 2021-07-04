package com.ylzs.entity.processCombCraft.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weikang
 * @Description 工序组合工艺方案请求返回数据
 * @Date 2020/3/14
 */
@Data
public class ProcessCombCraftProgramResp implements Serializable {
    private static final long serialVersionUID = 3215773179720534300L;

    /**
     * 工序编码
     */
    private String processCraftCodes;
}
