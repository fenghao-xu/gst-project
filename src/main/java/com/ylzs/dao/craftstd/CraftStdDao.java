package com.ylzs.dao.craftstd;


import com.ylzs.entity.craftstd.CraftStd;
import com.ylzs.entity.craftstd.CraftStdStatistic;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：
 *
 * @author Administrator
 * 2019-09-25 11:35
 */
public interface CraftStdDao {

    Integer addCraftStd(CraftStd craftStd);

    Integer deleteCraftStd(long id);

    Integer updateCraftStd(CraftStd craftStd);

    Boolean isCraftStdExist(long id);

    List<CraftStd> getCraftStdById(@Param("ids") long[] ids);

    List<CraftStd> getCraftStdByPage(@Param("keywords") String keywords,
                                     @Param("beginDate") Date beginDate, @Param("endDate") Date endDate,
                                     @Param("id") Long id);

    List<CraftStd> getCraftStdByCode(@Param("craftStdCodes") String[] craftStdCodes);

    List<CraftStd> getDeletedCraftStd();

    Boolean getIsVideoById(long id);

    CraftStdStatistic getCraftStdStatistic();

    List<CraftStd> getNoVideoCraftStd(@Param("craftStdCode") String craftStdCode, @Param("craftStdName") String craftStdName);
    List<CraftStd> getNoHandleImageCraftStd(@Param("craftStdCode") String craftStdCode);

    int getNoVideoCraftStdCount();

    public  List<CraftStd> getCraftStdByLikeParam(Map<String,Object> param);

    List<CraftStd> getCraftStdByCondition(@Param("craftStdCode") String craftStdCode,
                                          @Param("craftStdName") String craftStdName,
                                          @Param("styleCode") String styleCode,
                                          @Param("requireQuality") String requireQuality,
                                          @Param("makeDesc") String makeDesc,
                                          @Param("status") String status,
                                          @Param("createTimeBeginDate") Date createTimeBeginDate,
                                          @Param("createTimeEndDate") Date createTimeEndDate,
                                          @Param("okTimeBeginDate") Date okTimeBeginDate,
                                          @Param("okTimeEndDate") Date okTimeEndDate,
                                          @Param("updateTimeBeginDate") Date updateTimeBeginDate,
                                          @Param("updateTimeEndDate") Date updateTimeEndDate,
                                          @Param("remark") String remark,
                                          @Param("isVideo") Boolean isVideo,
                                          @Param("isPic") Boolean isPic,
                                          @Param("commitTimeBeginDate") Date commitTimeBeginDate,
                                          @Param("commitTimeEndDate") Date commitTimeEndDate,
                                          @Param("user") String user,
                                          @Param("extractTimeBeginDate") Date extractTimeBeginDate,
                                          @Param("extractTimeEndDate") Date extractTimeEndDate,
                                          @Param("craftCategoryId") Integer craftCategoryId,
                                          @Param("craftPartId") Integer craftPartId,
                                          @Param("craftCategoryCode") String craftCategoryCode,
                                          @Param("craftPartCode") String craftPartCode
                                          );

    String getCraftStdInUsed(@Param("craftStdCode") String craftStdCode);
}
