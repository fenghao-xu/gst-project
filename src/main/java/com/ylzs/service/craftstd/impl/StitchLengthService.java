package com.ylzs.service.craftstd.impl;

import com.ylzs.dao.craftstd.StitchLengthDao;
import com.ylzs.entity.craftstd.StitchLength;
import com.ylzs.service.craftstd.IStitchLengthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 说明：针距服务实现
 *
 * @author Administrator
 * 2019-09-30 11:34
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StitchLengthService implements IStitchLengthService {
    @Resource
    private StitchLengthDao stitchLengthDao;

    @Override
    public Integer addStitchLength(StitchLength stitchLength) {
        return stitchLengthDao.addStitchLength(stitchLength);
    }

    @Override
    public Integer deleteStitchLength(String stitchLengthCode, String userCode) {
        Long count = stitchLengthDao.getStdCountByStitchLengthCode(stitchLengthCode);
        Integer ret = 0;
        if (count != null && count > 0) {
            StitchLength stitchLength = new StitchLength();
            stitchLength.setStitchLengthCode(stitchLengthCode);
            stitchLength.setIsInvalid(true);
            stitchLength.setUpdateTime(new Date());
            stitchLength.setUpdateUser(userCode);
            ret = stitchLengthDao.updateStitchLength(stitchLength);
        } else {
            ret = stitchLengthDao.deleteStitchLength(stitchLengthCode);
        }
        return ret;
    }

    @Override
    public Integer updateStitchLength(StitchLength stitchLength) {
        return stitchLengthDao.updateStitchLength(stitchLength);
    }

    @Override
    public List<StitchLength> getStitchLengthByCode(String[] stitchLengthCodes) {
        return stitchLengthDao.getStitchLengthByCode(stitchLengthCodes);
    }

    @Override
    public List<StitchLength> getStitchLengthByPage(String keywords, Date beginDate, Date endDate, Integer lineId) {
        return stitchLengthDao.getStitchLengthByPage(keywords, beginDate, endDate, lineId);
    }

}
