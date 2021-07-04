package com.ylzs.entity.craftmainframe;

import lombok.Data;

@Data
public class FlowNumConfig {
    private Long id;
    /**
     * 工序流首字符
     */
    private String flowNum;
    /**
     * 生产部件代码
     */
    private String productionPartCode;
}
