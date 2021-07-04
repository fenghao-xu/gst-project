package com.ylzs.dao.custom;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.craftstd.CraftFile;
import com.ylzs.entity.craftstd.CraftStd;
import com.ylzs.entity.custom.CustomStylePartCraft;
import com.ylzs.entity.workplacecraft.WorkplaceCraft;
import com.ylzs.vo.bigstylereport.WorkTypeReport;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 定制款部件工序
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:01:58
 */
@Mapper
public interface CustomStylePartCraftDao extends BaseDAO<CustomStylePartCraft> {


    public List<CustomStylePartCraft> getPartRandomCodeCraftList(@Param("arrays") List<Long> randomCodeList);

    public List<CustomStylePartCraft> getPartRandomCodeCraftListOne(Long partRandomCode);

    int deleteCustomStyleCraftList(@Param("arrays") List<Long> randomCodeList);

    List<CraftFile> getCustomCraftStdFile(@Param("randomCodeList") List<Long> randomCodeList);

    @MapKey("remark")
    Map<String, CraftStd> getCustomCraftStdMap(@Param("randomCodeList") List<Long> randomCodeList);


    @MapKey("remark")
    Map<String, WorkplaceCraft> getWorkplaceCraftMap(@Param("mainFrameCode") String mainFrameCode,
                                                     @Param("randomCodeList") List<Long> randomCodeList);

    List<CustomStylePartCraft> getCustomStyleSectionTime(@Param("styleRandomCode") Long styleRandomCode);

    List<WorkTypeReport> selectWorkTypeReport(Map<String, Object> param);

}
