package com.ylzs.dao.bigticketno;

import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraftDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-24 9:55
 */
public interface BigOrderPartCraftDetailDao {
    public void insertPartCraftDetailForeach(@Param("detailList") List<BigStyleAnalysePartCraftDetail> detailList);

    public void deleteByStyleIds(@Param("ids") List<Long> ids);

    public List<Long> getIdsByStyleRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public List<Long> getIdsByStyleRandomCodeAndPartCraftMainCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode, @Param("craftCode") String craftCode);

    List<BigStyleAnalysePartCraftDetail> getBigOrderSectionTime(@Param("styleRandomCode") Long styleRandomCode);

    List<BigStyleAnalysePartCraftDetail> getBigOrderAnalysePartCraftDetail(@Param("styleRandomCode") Long styleRandomCode);

    List<BigStyleAnalysePartCraftDetail> getBigOrderNewCraft(@Param("styleRandomCode") Long styleRandomCode);

    public  void updatePartCraftDetail(BigStyleAnalysePartCraftDetail detail);

    List<String> checkCraftName(@Param("styleRandomCode") Long styleRandomCode);


    /**
     * 本地刷工序等级和工序单价的
     */
    public  Integer updateCraftLevelAndPrice(Map<String, Object> param);

    public Integer updatePriceByStyleRandomCodeAndCraftCode(Map<String, Object> param);


}
