package com.ylzs.dao.sewingcraft;

import com.ylzs.entity.sewingcraft.SewingCraftStd;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-17 16:59
 */
public interface SewingCraftStdDao {
    public void addSewingCraftStd(Map<String, Object> map);

    public void updateSewingCraftStd(Map<String, Object> map);

    public List<SewingCraftStd> getDataBySewingCraftRandomCode(Long randomCode);

    public void deleteDataBySewingCraftRandomCode(Long randomCode);
}
