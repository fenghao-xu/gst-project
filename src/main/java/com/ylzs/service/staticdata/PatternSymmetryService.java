package com.ylzs.service.staticdata;

import com.ylzs.dao.staticdata.PatternSymmetryDao;
import com.ylzs.entity.staticdata.PatternSymmetry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-27 10:05
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatternSymmetryService {

    @Resource
    private PatternSymmetryDao patternSymmetryDao;

    public List<PatternSymmetry> getAllPatternSymmetrys() {
        return patternSymmetryDao.getAllPatternSymmetrys();
    }

    public BigDecimal getPatternSymmetrysCode(String code){return patternSymmetryDao.getPatternSymmetrysCode(code);}
}
