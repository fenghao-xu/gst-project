package com.ylzs.dao.timeparam;

import com.ylzs.entity.timeparam.FabricGrade;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-04-07 16:19
 */
public interface FabricGradeDao {
    public List<FabricGrade> getAllFabricGrade();

    public BigDecimal fabricTimeConfficientByCode(String fabricGradeCode);
}
