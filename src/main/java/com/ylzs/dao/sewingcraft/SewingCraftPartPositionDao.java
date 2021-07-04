package com.ylzs.dao.sewingcraft;

import com.ylzs.entity.sewingcraft.SewingCraftPartPosition;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-07 18:48
 */
public interface SewingCraftPartPositionDao {
    public void addSewingCraftPartPosition(Map<String, Object> map);

    public List<SewingCraftPartPosition> getDataBySewingCraftRandomCode(Long randomCode);

    public void deleteDataBySewingCraftRandomCode(Long randomCode);
}
