package com.ylzs.vo.thinkstyle;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 智库款发布信息
 */
@Data
public class ThinkStylePublishInfoVo implements Serializable {
    private static final long serialVersionUID = -7848473271600036248L;

    /**
     * 智库款代码
     */
    private String bjdm;

    /**
     * 智库款工序明细
     */
    private List<ThinkStylePublishCraftVo> details;

}
