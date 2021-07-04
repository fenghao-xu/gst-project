package com.ylzs.entity.craftstd;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

/**
 * @author xuwei
 * @create 2020-03-04 15:19
 * 部件位置
 */
@Data
public class PartPosition extends SuperEntity {
    /**
     * 部件类型
     *  sewing缝边 embroider绣花 decoration装饰 bjposition 部件
     */
    private String partType;
    /**
     * 部件位置编码
     */
    private String partPositionCode;
    /**
     * 部件位置名称
     */
    private String partPositionName;
    /**
     * 款式类型---服装品类
     */
    private String clothingCategoryCode;

    /**
     * 款式类型---服装品类
     */
    private transient String clothingCategoryName;
    /**
     * 部件类型
     */
    private String remark;
}
