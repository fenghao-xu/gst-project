package com.ylzs.dao.timeparam;

import com.ylzs.vo.FabricScorePlanVO;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-04 9:54
 * 面料分值方案
 */
public interface FabricScorePlanDao {
    public List<FabricScorePlanVO> selectFabricScoresAndPlan();
}
