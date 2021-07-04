package com.ylzs.dao.craftstd;

import com.ylzs.entity.craftstd.StitchLength;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-27 14:10
 */
public interface StitchLengthDao {
    Integer addStitchLength(StitchLength stitchLength);
    Integer deleteStitchLength(String stitchLengthCode);
    Integer updateStitchLength(StitchLength stitchLength);
    List<StitchLength> getStitchLengthByCode(@Param("stitchLengthCodes") String[] stitchLengthCodes);
    List<StitchLength> getStitchLengthByPage(@Param("keywords") String keywords,
                                             @Param("beginDate") Date beginDate,
                                             @Param("endDate") Date endDate,
                                             @Param("lineId") Integer lineId);
    Long getStdCountByStitchLengthCode(@Param("stitchLengthCode") String stitchLengthCode);
}
