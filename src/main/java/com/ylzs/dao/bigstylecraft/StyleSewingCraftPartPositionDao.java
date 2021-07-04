package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.sewingcraft.SewingCraftPartPosition;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-07 18:48
 */
public interface StyleSewingCraftPartPositionDao {
    public void addSewingCraftPartPosition(Map<String, Object> map);

    public void updateSewingCraftPartPosition(Map<String, Object> map);

    public List<SewingCraftPartPosition> getDataBySewingCraftRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public void deleteDataBySewingCraftRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public void deleteDataBySewingCraftRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public List<Long> getIDsBySewingCraftRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public List<Long> getIDsBySewingCraftRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public void deleteDataByids(@Param("ids") List<Long> ids);

}
