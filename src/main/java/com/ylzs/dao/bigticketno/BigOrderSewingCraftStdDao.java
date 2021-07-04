package com.ylzs.dao.bigticketno;

import com.ylzs.entity.craftstd.CraftFile;
import com.ylzs.entity.craftstd.CraftStd;
import com.ylzs.entity.sewingcraft.SewingCraftStd;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-17 16:59
 */
public interface BigOrderSewingCraftStdDao {
    public void addSewingCraftStd(Map<String, Object> map);

    public List<SewingCraftStd> getDataBySewingCraftRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public void deleteDataBySewingCraftRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public void deleteDataBySewingCraftRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public List<Long> getIDSBySewingCraftRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public List<Long> getIdByBysewingRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public void deleteDataByIds(@Param("ids") List<Long> ids);

    List<SewingCraftStd> getSewingCraftStdNewCraft(@Param("styleRandomCode") Long styleRandomCode);

    List<CraftFile> getBigOrderCraftStdFileNewCraft(@Param("styleRandomCode") Long styleRandomCode);

    @MapKey("remark")
    Map<String, CraftStd> getBigOrderCraftStdMap(@Param("styleRandomCode") Long styleRandomCode, @Param("isNew") Boolean isNew);
}
