package com.ylzs.dao.timeparam;

import com.ylzs.entity.timeparam.FabricScore;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-04 9:57
 * 面料方案
 */
public interface FabricScoreDao {
    public List<FabricScore> getFabricScoreByPlanCode(String planCode);

    public List<FabricScore> getAllFabricScore();
}
