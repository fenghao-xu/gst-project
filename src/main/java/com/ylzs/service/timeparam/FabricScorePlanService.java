package com.ylzs.service.timeparam;

import com.ylzs.dao.timeparam.FabricScorePlanDao;
import com.ylzs.vo.FabricScorePlanVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-04 11:52
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FabricScorePlanService {
    @Resource
    private FabricScorePlanDao fabricScorePlanDao;

    public List<FabricScorePlanVO> selectFabricScoresAndPlan() {
        return fabricScorePlanDao.selectFabricScoresAndPlan();
    }
}
