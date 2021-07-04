package com.ylzs.dao.bigticketno;

import com.ylzs.entity.sewingcraft.SewingCraftWarehouseWorkplace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-07 15:10
 */
public interface BigOrderSewingCraftWarehouseWorkplaceDao {
    public void addData(Map<String, Object> map);

    public List<Long> getIdByBysewingRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public List<Long> getIdByBysewingRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public List<SewingCraftWarehouseWorkplace> getDataBySewingCraftRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public List<SewingCraftWarehouseWorkplace> getDataByParam(Map<String, Object> map);

    public void deleteWorkplaceBysewingRandomCodeAndCraftCode(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public void deleteWorkplaceBysewingRandomCodeAndPartCraftMainCode(@Param("randomCode") Long randomCode, @Param("partCraftMainCode") String partCraftMainCode);

    public void deleteWorkplaceByIds(@Param("ids") List<Long> ids);

    List<SewingCraftWarehouseWorkplace> getSewingCraftWarehouseWorkplace(@Param("styleRandomCode") Long styleRandomCode);

    List<SewingCraftWarehouseWorkplace> getSewingCraftWarehouseWorkplaceNewCraft(@Param("styleRandomCode") Long styleRandomCode);
}
