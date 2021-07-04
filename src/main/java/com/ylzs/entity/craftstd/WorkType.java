package com.ylzs.entity.craftstd;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-16 17:36
 * 工种
 */
@Data
public class WorkType extends SuperEntity {
    /**
     * 工种编码
     */
    private String workTypeCode;
    /**
     * 工种名称
     */
    private String workTypeName;

    /**
     * 工段编码
     */
    private String sectionCode;

    /**
     * 工段名称
     */
    private String sectionName;

    /**
     * 做工类型
     */
    private List<MakeType>makeTypes;
}
