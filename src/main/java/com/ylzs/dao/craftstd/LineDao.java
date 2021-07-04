package com.ylzs.dao.craftstd;

import com.ylzs.entity.craftstd.Line;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-25 16:11
 */
public interface LineDao {
    Integer addLine(Line line);
    Integer deleteLine(String lineCode);
    Integer updateLine(Line line);
    List<Line> getLineByCode(@Param("lineCodes") String[] lineCodes);
    List<Line> getLineByPage(@Param("keywords") String keywords, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);
    Integer getStitchCountByCode(String lineCode);
    List<Line> getAllLine();
}