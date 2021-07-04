package com.ylzs.service.craftstd.impl;

import com.ylzs.dao.craftstd.LineDao;
import com.ylzs.entity.craftstd.Line;
import com.ylzs.service.craftstd.ILineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 说明：线型服务实现
 *
 * @author Administrator
 * 2019-09-30 11:06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LineService implements ILineService {
    @Resource
    private LineDao lineDao;

    @Override
    public Integer addLine(Line line) {
        return lineDao.addLine(line);
    }

    @Override
    public Integer deleteLine(String lineCode) {
        return lineDao.deleteLine(lineCode);
    }

    @Override
    public Integer updateLine(Line line) {
        return lineDao.updateLine(line);
    }

    @Override
    public List<Line> getLineByCode(String[] lineCodes) {
        return lineDao.getLineByCode(lineCodes);
    }

    @Override
    public List<Line> getLineByPage(String keywords, Date beginDate, Date endDate) {
        return lineDao.getLineByPage(keywords, beginDate, endDate);
    }

    @Override
    public Integer getStitchCountByCode(String lineCode) {
        return lineDao.getStitchCountByCode(lineCode);
    }

    @Override
    public List<Line> getAllLine() {
        return lineDao.getAllLine();
    }
}
