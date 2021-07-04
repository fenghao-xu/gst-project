package com.ylzs.service.craftstd;

import com.ylzs.entity.craftstd.CraftPart;

import java.util.Date;
import java.util.List;

/**
 * 说明：工艺部件服务接口
 *
 * @author Administrator
 * 2019-09-30 9:58
 */
public interface ICraftPartService {
    Integer addCraftPart(CraftPart craftPart);

    Integer deleteCraftPart(String craftPartCode, String userCode);

    Integer updateCraftPart(CraftPart craftPart);

    List<CraftPart> getCraftPartByCode(String[] craftPartCodes);

    List<CraftPart> getCraftPartByPage(String keywords, Integer craftCategoryId, Date beginDate, Date endDate,String craftCategoryCode);

    List<CraftPart> getCraftPartByCategoryId(Integer craftCategoryId);

    CraftPart getCraftPartById(Integer id);

    public List<CraftPart> getAllValidCraftPart();

    public List<CraftPart> getCateGoryCodePartAll(String codeArray);

}
