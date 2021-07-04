package com.ylzs.entity.sewingcraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author xuwei
 * @create 2020-03-05 13:35
 * 车缝工序词库的缝边位置
 */
@Data
public class SewingCraftPartPosition {
    private String videoUrl;
    /**
     * 车缝工序词库里面的random_code
     */
    private Long sewingCraftRandomCode;
    /**
     * 缝边位置编码
     */
    private String partPositionCode;
    /**
     * 缝边位置名称
     */
    private String partPositionName;
    /**
     * 工序编码
     */
    private String craftCode;
    /**
     * 部件工艺编码
     */
    private String partCraftMainCode;
}
