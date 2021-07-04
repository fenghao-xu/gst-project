package com.ylzs.service.craftmainframe;

import com.ylzs.entity.craftmainframe.CraftMainFrame;
import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;
import com.ylzs.entity.craftmainframe.ProductionPart;
import com.ylzs.vo.craftmainframe.CraftMainFrameExportVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 工艺主框架服务接口
 * @author: lyq
 * @date: 2020-03-05 16:16
 */
public interface ICraftMainFrameService {

    int deleteByPrimaryKey(Long randomCode);

    int insert(CraftMainFrame record);

    int insertSelective(CraftMainFrame record);

    CraftMainFrame selectByPrimaryKey(Long randomCode);

    int updateByPrimaryKeySelective(CraftMainFrame record);

    int updateByPrimaryKey(CraftMainFrame record);

    int batchInsert(List<CraftMainFrame> list);

    int insertAll(CraftMainFrame frame, List<ProductionPart> parts, List<CraftMainFrameRoute> routes);

    int updateAll(CraftMainFrame frame, List<ProductionPart> parts, List<CraftMainFrameRoute> routes);

    CraftMainFrame selectByCode(String code);

    List<CraftMainFrame> getByCondition(String keywords, Date beginDate, Date endDate);

    boolean isDefaultMainFrameExists(Long craftCategoryRandomCode, Long excludeMainFrameRandomCode);

    List<CraftMainFrameExportVo> getCraftMainFrameExportVos(List<CraftMainFrame> craftMainFrames);

    public Map<String, CraftMainFrame> getAllMainFrameToMap();

    CraftMainFrame selectMainFrameByClothesCategoryCode(String clothesCategoryCode);

    List<CraftMainFrame> selectMainFrameListByClothesCategoryCode(String clothesCategoryCode);

    List<CraftMainFrame>getByCraftCategoryAndType(String craftCategoryCode,String frameType);


    void updateRelateMainFrameInfo(String mainFrameCode);

}