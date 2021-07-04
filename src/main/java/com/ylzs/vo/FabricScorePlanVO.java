package com.ylzs.vo;

import com.ylzs.core.model.SuperEntity;
import com.ylzs.entity.timeparam.FabricScore;
import lombok.Data;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-04 9:43
 * 面料分值方案
 */
@Data
public class FabricScorePlanVO extends SuperEntity {
    /**
     * 面料分值方案编码
     */
    private String fabricScorePlanCode;
    /**
     * 面料分值方案名称
     */
    private String fabricScorePlanName;
    /**
     * 备注
     */
    private String remark;

    private List<FabricScore> fabricScoreList;
}
