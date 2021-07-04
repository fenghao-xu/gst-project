package com.ylzs.dao.bigstylecraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.vo.SewingCraftResource;
import com.ylzs.vo.bigstylereport.CraftVO;
import com.ylzs.vo.sewing.VSewingCraftVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-02-26 10:44
 */
public interface StyleSewingCraftWarehouseDao extends BaseDAO<SewingCraftWarehouse> {

    public void addSewingCraftWarehouse(Map<String, Object> map);

    int insertSewingCraftWarehouseForeach(@Param("sewList") List<SewingCraftWarehouse> sewList);

    public List<SewingCraftResource> getSewingStdPicAndVedio(Long param);

    public List<SewingCraftWarehouse> getDataByCraftCodeList(@Param("codeList") List<String> codeList, @Param("bigStyleAnalyseCode") String bigStyleAnalyseCode);

    public List<SewingCraftWarehouse> getDataByBigStyleAnalyseCodeList(@Param("codeList") List<String> codeList);

    public SewingCraftWarehouse getDataByRandom(Long randomCode);

    public void updateStatus(Integer status, Long randomCode);

    public void deleteDataByRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public List<SewingCraftWarehouse> getDataByPartDetailIds(@Param("idList") List<Long> idList);

    public List<CraftVO> getDataForExcelReport(@Param("randomCode") Long randomCode);

    public List<CraftVO> getDataForExcelReportOrderByWorkOrder(@Param("randomCode") Long randomCode);


    public void deleteDataByIds(@Param("ids") List<Long> ids);

    public List<Long> getIdsByRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public List<Long> getIdsByRandomCodeAndPartCraftMainCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode, @Param("craftCode") String craftCode);

    List<SewingCraftWarehouse> getSewingCraftWarehouse(@Param("styleRandomCode") Long styleRandomCode);

    List<SewingCraftWarehouse> getSewingCraftWarehouseNewCraft(@Param("styleRandomCode") Long styleRandomCode);

    public List<VSewingCraftVo> getCraftCodeDataAll(@Param("materialGrade") int materialGrade, @Param("mainFrameCode") String mainFrameCode, @Param("bigStyleAnalyseCodes") List<String> bigStyleAnalyseCodes);

    public String isExistByNeedlePitchValue(@Param("needlePitchValue")String needlePitchValue);

    /**
     * 本地刷工序等级和工序单价的
     */
    public  Integer updateCraftLevelAndPrice(Map<String, Object> param);

    public  Integer updateCraftMakeTypeCode(Map<String, Object> param);

    public Integer updatePriceById(Map<String, Object> param);

    public List<SewingCraftWarehouse>getAllData();
}
