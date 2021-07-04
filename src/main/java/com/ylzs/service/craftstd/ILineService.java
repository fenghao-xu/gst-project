package com.ylzs.service.craftstd;

import com.ylzs.entity.craftstd.Line;

import java.util.Date;
import java.util.List;

/**
 * 说明：线号/线型服务接口
 *
 * @author Administrator
 * 2019-09-30 11:05
 */
public interface ILineService {
    Integer addLine(Line line);
    Integer deleteLine(String lineCode);
    Integer updateLine(Line line);
    List<Line> getLineByCode(String[] lineCodes);
    List<Line> getLineByPage(String keywords, Date beginDate, Date endDate);
    Integer getStitchCountByCode(String lineCode);
    List<Line> getAllLine();
}
