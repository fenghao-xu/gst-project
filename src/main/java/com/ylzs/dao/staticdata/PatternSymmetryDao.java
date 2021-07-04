package com.ylzs.dao.staticdata;

import com.ylzs.entity.staticdata.PatternSymmetry;
import org.apache.ibatis.annotations.MapKey;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-27 10:05
 */
public interface PatternSymmetryDao {
    public List<PatternSymmetry> getAllPatternSymmetrys();

    public BigDecimal getPatternSymmetrysCode(String code);

    @MapKey("code")
    public Map<String,PatternSymmetry> getPatternSymmetrysToMap();
}
