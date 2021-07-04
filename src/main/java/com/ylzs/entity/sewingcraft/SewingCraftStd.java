package com.ylzs.entity.sewingcraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author xuwei
 * @create 2020-03-17 16:57
 */
@Data
public class SewingCraftStd extends SuperEntity {
    private String craftStdCode;
    private String craftStdName;
    private Long sewingCraftRandomCode;
    /**
     * 工序编码
     */
    private String craftCode;
    /**
     * 部件工艺编码
     */
    private String partCraftMainCode;
}
