package com.ylzs.service.timeparam;

import com.ylzs.dao.timeparam.FabricScoreDao;
import com.ylzs.entity.timeparam.FabricScore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-04-07 16:30
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FabricScoreService {

    @Resource
    private FabricScoreDao fabricScoreDao;

    public List<FabricScore> getFabricScoreByPlanCode(String planCode) {
        return fabricScoreDao.getFabricScoreByPlanCode(planCode);
    }
}
