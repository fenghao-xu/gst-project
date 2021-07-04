package com.ylzs.service.timeparam;

import com.ylzs.dao.timeparam.FabricGradeDao;
import com.ylzs.entity.timeparam.FabricGrade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-04-07 16:23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FabricGradeService {

    @Resource
    private FabricGradeDao fabricGradeDao;

    public List<FabricGrade> getAllFabricGrade() {
        return fabricGradeDao.getAllFabricGrade();
    }

    public BigDecimal fabricTimeConfficientByCode(String fabricGradeCode){
        BigDecimal timeConfficient = BigDecimal.ZERO.setScale(3);
        try {
            timeConfficient = fabricGradeDao.fabricTimeConfficientByCode(fabricGradeCode).setScale(3);
        }catch (Exception x){
            x.printStackTrace();
            timeConfficient = BigDecimal.ZERO.setScale(3);
        }
        return timeConfficient;
    }
}
