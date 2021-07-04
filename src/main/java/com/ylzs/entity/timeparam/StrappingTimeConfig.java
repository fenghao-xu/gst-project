package com.ylzs.entity.timeparam;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author xuwei
 * @create 2020-02-27 15:56
 * 捆扎时间
 */
@Data
public class StrappingTimeConfig extends SuperEntity {
    /**
     * 捆扎编码
     */
    private String strappingCode;

    /**
     * 捆扎名称
     */
    private String strappingName;

    /**
     * 时间
     */
    private Integer time;


    /**
     * 备注
     */
    private String remark;

}
