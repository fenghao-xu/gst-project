package com.ylzs.dao.sewingcraft;

import com.ylzs.entity.sewingcraft.SewingCraftAction;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-02-25 18:29
 */
public interface SewingCraftActionDao {
    public void addSewingCraftAction(@Param("motions") List<SewingCraftAction> motions);

    public void updateSewingCraftAction(Map<String, Object> param);

    public List<SewingCraftAction> getDataBySewingCraftRandomCode(Long randomCode);

    public List<SewingCraftAction> getDataBySewingCraftCode(String code);

    public void deleteDataBySewingCraftRandomCode(Long randomCode);
    public List<SewingCraftAction> getZCodeList();

    public void updateTime(@Param("manualTime") BigDecimal manualTime, @Param("id") Long id);

    public void updateBaseTime(@Param("manualTime") BigDecimal manualTime, @Param("id") Long id);



}
