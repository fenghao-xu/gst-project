package com.ylzs.dao.bigticketno;

import com.ylzs.entity.sewingcraft.SewingCraftAction;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-02-25 18:29
 */
public interface BigOrderSewingCraftActionDao {
    public void addSewingCraftAction(@Param("motions") List<SewingCraftAction> motions);

    public void updateSewingCraftAction(Map<String, Object> param);

    public List<SewingCraftAction> getDataBySewingCraftRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public List<SewingCraftAction> getDataBySewingCraftCode(String code);

    public void deleteDataBySewingCraftRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public void deleteDataBySewingCraftRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public void deleteDataByids(@Param("ids") List<Long> ids);

    public List<Long> getIdsBySewingCraftRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public List<Long> getIdsBySewingCraftRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public List<SewingCraftAction> getZCodeList();

    public void updateTime(@Param("manualTime") BigDecimal manualTime, @Param("id") Long id);

    public void updateBaseTime(@Param("manualTime") BigDecimal manualTime, @Param("id") Long id);

}
