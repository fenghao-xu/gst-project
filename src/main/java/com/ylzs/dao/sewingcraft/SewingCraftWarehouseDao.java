package com.ylzs.dao.sewingcraft;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.sewingcraft.relation.SewingCraftWarehouseRelationResp;
import com.ylzs.vo.SewingCraftResource;
import com.ylzs.vo.SewingCraftVo;
import com.ylzs.vo.sewing.CraftAnalyseVo;
import org.apache.ibatis.annotations.MapKey;
import com.ylzs.vo.sewing.VSewingCraftVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-02-26 10:44
 */
public interface SewingCraftWarehouseDao extends BaseDAO<SewingCraftWarehouse> {
    public Integer updatePrice(@Param("id") Long id, @Param("standardPrice") BigDecimal standardPrice);

    public Integer deleteByRandomCode(@Param("randomCode") Long randomCode);

    public List<SewingCraftVo> searchSewingCraftData(HashMap param);

    public void addSewingCraftWarehouse(Map<String, Object> map);

    public void updateSewingCraftWarehouse(Map<String, Object> map);

    public List<SewingCraftWarehouse> getDataByPager(Map<String, Object> param);

    public List<SewingCraftWarehouse> getDataByMainFramePartCode(Map<String, Object> param);


    public List<SewingCraftResource> getSewingStdPicAndVedio(@Param("param") Long param,@Param("status") String[] statusArr);

    List<String> selectListByCraftCode(@Param("list") List<String> sourceCraftCodes);

    public SewingCraftWarehouse getDataByRandom(Long randomCode);

    public void updateStatus(@Param("status") Integer status, @Param("randomCode") Long randomCode, @Param("userCode") String userCode, @Param("releaseTime") Date releaseTime);

    public List<SewingCraftWarehouse> getDataByCraftCodeList(@Param("codeList") List<String> codeList);

    /**
     * 通过工序编码和工序主框架 查询对应的工序流
     */
    @MapKey("craftCode")
    public Map<String, SewingCraftWarehouse> getCraftFlowNumByCraftCodeAndMainFrameCode(@Param("craftCodeList") List<String> craftCodeList, @Param("mainFrameCode") String mainFrameCode);

    public List<VSewingCraftVo> getCraftCodeDataAll(@Param("craftCodeArray") String[] craftCodeArray, @Param("materialGrade") int materialGrade, @Param("mainFrameCode") String mainFrameCode, @Param("status") Integer status);

    public List<VSewingCraftVo> getCraftCodeCraftFlowNumDataAll(@Param("craftCodeArray") String[] craftCodeArray, @Param("mainFrameCode") String mainFrameCode);

    Long getSewingCraftRandomCode(String craftCode);

    public List<SewingCraftWarehouse> getDataByCraftCodeLike(Map<String, Object> param);

    public void updateSameLevel(Map<String, String> param);

    int updateRelateCraftInfo(@Param("craftCode") String craftCode);

    String getCraftInUsed(@Param("craftCode") String craftCode);

    List<CraftAnalyseVo> getCraftAnalyseAll(Map<String, Object> param);

    SewingCraftWarehouseRelationResp getCraftListByRandomCode(Long valueOf);

    List<SewingCraftWarehouse> getCraftRelationList(@Param("randomCode") Long randomCode,@Param("mainFrameCode")String mainFrameCode);

    List<SewingCraftWarehouse> getSewingCraftDataByCraftCode(@Param("craftCode") String craftCode, @Param("mainFrameCode") String mainFrameCode);

    List<SewingCraftResource> getSewingStdPicAndVedioGroup(@Param("randomCode")Long randomCode);

    List<SewingCraftResource> getSewingStdRawPicAndVedioGroup(Long randomCode);

    /**
     * 本地刷工序等级和工序单价的
     */
    public  Integer updateCraftLevelAndPrice(Map<String, Object> param);

    public  Integer updateCraftMakeTypeCode(Map<String, Object> param);

    public Integer updatePriceByCraftCode(Map<String, Object> param);


    List<SewingCraftResource> getCustomPicAndVedio(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    String getCraftCodeDataByRandomCode(Long randomCode);

    String isExistByNeedlePitchValue(@Param("needlePitchValue")String needlePitchValue);
}
