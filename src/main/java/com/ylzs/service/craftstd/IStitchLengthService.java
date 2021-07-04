package com.ylzs.service.craftstd;

import com.ylzs.entity.craftstd.StitchLength;

import java.util.Date;
import java.util.List;

/**
 * 说明：针距服务接口
 *
 * @author Administrator
 * 2019-09-30 11:34
 */
public interface IStitchLengthService {
    Integer addStitchLength(StitchLength stitchLength);
    Integer deleteStitchLength(String stitchLengthCode, String userCode);
    Integer updateStitchLength(StitchLength stitchLength);
    List<StitchLength> getStitchLengthByCode(String[] stitchLengthCodes);
    List<StitchLength> getStitchLengthByPage(String keywords, Date beginDate, Date endDate, Integer lineId);
}
