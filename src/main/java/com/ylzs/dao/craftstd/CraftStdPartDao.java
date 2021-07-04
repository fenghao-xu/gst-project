package com.ylzs.dao.craftstd;

import com.ylzs.entity.craftstd.CraftStdPart;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CraftStdPartDao {
    Integer addCraftStdParts(@Param("craftStdParts") List<CraftStdPart> craftStdParts);

    Integer deleteCraftStdPart(@Param("craftStdId") Long craftStdId, @Param("craftPartId") Integer craftPartId);

    Integer updateCraftStdPart(CraftStdPart craftStdPart);

    List<CraftStdPart> getCraftStdPartById(@Param("craftStdId") Long craftStdId, @Param("craftPartId") Integer craftPartId);

    List<CraftStdPart> getCraftStdPartByPage(@Param("keywords") String keywords,
                                             @Param("beginDate") Date beginDate,
                                             @Param("endDate") Date endDate,
                                             @Param("craftStdId") Long craftStdId,
                                             @Param("craftPartId") Integer craftPartId);



}
